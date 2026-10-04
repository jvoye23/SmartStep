package com.jvcodingsolutions.smartstep.features.step_counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.connectivity.ConnectivityObserver
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import com.jvcodingsolutions.smartstep.core.presentation.util.*
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ActivityContext
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.AiCoachRepository
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.InsightSessionHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


const val savingStepsToDbInterval = 10
class StepCounterViewModel(
    private val trackRepository: TrackRepository,
    private val profileStorage: ProfileStorage,
    private val stepTracker: StepTracker,
    private val applicationScope: CoroutineScope,
    private val aiCoachRepository: AiCoachRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val insightSessionHolder: InsightSessionHolder
): ViewModel() {


    private val _state = MutableStateFlow(StepCounterState())

    private var hasLoadedInitialData = false
    private var lastStepDetectionTime: Long? = null

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
            stepTracker.startTracking()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )



    private fun loadInitialData() {
        viewModelScope.launch {
            val profileInfo = profileStorage.get()
            if (profileInfo != null) {
                val profileId = profileInfo.id
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                
                _state.update {
                    it.copy(
                        profileId = profileId,
                        isProfileMetricSystem = profileInfo.isMetricSystem
                    )
                }

                // Load steps from database and unwritten steps reactively
                launch {
                    var previousSteps: Int? = null
                    trackRepository.getLiveStepsFlow(profileId, today)
                        .distinctUntilChanged()
                        .collect { currentTotalSteps ->
                            // Refresh the AI insight exactly when the daily goal is crossed
                            val goal = _state.value.dailyGoalSteps
                            val previous = previousSteps
                            if (goal > 0 && previous != null && previous < goal && currentTotalSteps >= goal) {
                                refreshInsight(steps = currentTotalSteps, goal = goal)
                            }
                            previousSteps = currentTotalSteps
                            val calculatedDistance = if (state.value.isProfileMetricSystem) {
                                calculateFormattedDistance(
                                    steps = currentTotalSteps,
                                    heightCm = profileInfo.heightInCm ?: 175,
                                    system = MeasurementSystem.METRIC,
                                )
                            } else  {
                                calculateFormattedDistance(
                                    steps = currentTotalSteps,
                                    heightCm = profileInfo.heightInCm ?: 175,
                                    system = MeasurementSystem.IMPERIAL,
                                )
                            }
                            val calories = calculateCalories(
                                steps = currentTotalSteps,
                                profileInfo = profileInfo
                            )

                            _state.update {
                                it.copy(
                                    currentSteps = currentTotalSteps,
                                    distanceTraveled = calculatedDistance,
                                    caloriesBurned = calories
                                )
                            }
                        }
                }

                // Load activity duration reactively
                launch {
                    trackRepository.getLiveDurationFlow(profileId, today)
                        .distinctUntilChanged()
                        .collect { duration ->
                            _state.update { it.copy(
                                activityDurationRaw = duration,
                                activityDuration = formatActivityDuration(duration)
                            ) }
                        }
                }

                // Fetch step goal reactively
                launch {
                    trackRepository.getCurrentStepGoalFlow(profileId, today).collect { stepGoal ->
                        if (stepGoal != null) {
                            _state.update { it.copy(dailyGoalSteps = stepGoal) }
                            // Refresh the AI insight when the user changes the daily goal
                            val lastGoalSeen = insightSessionHolder.lastGoalSeen
                            insightSessionHolder.lastGoalSeen = stepGoal
                            if (lastGoalSeen != null && lastGoalSeen != stepGoal) {
                                refreshInsight(goal = stepGoal)
                            }
                        }
                    }
                }

                // AI insight: first launch of the session fetches, otherwise the cached one is reused
                launch {
                    val cachedInsight = insightSessionHolder.cachedInsight
                    if (cachedInsight == null) {
                        // Build the first prompt from the real step count and goal instead of the
                        // state, which the other collectors may not have populated yet
                        val steps = trackRepository.getLiveStepsFlow(profileId, today).first()
                        val goal = trackRepository.getCurrentStepGoalFlow(profileId, today).first()
                        refreshInsight(steps = steps, goal = goal)
                    } else {
                        _state.update { it.copy(aiInsight = cachedInsight) }
                    }
                }

                launch {
                    val weeklyDbFlow = trackRepository.getWeeklyTracksFlow(
                        profileId = profileId,
                        startDate = today.minus(6, DateTimeUnit.DAY),
                        endDate = today
                    )

                    // We extract just the currentSteps from our state to observe it
                    val liveTodayStepsFlow = _state.map { it.currentSteps }.distinctUntilChanged()

                    // combine() waits for BOTH flows to emit, then gives us the latest from both
                    combine(weeklyDbFlow, liveTodayStepsFlow) { tracks, realTimeTodaySteps ->
                        mapTracksToDailyAverageState(
                            tracks = tracks,
                            today = today,
                            todayCurrentSteps = realTimeTodaySteps // Feed the live data in!
                        )
                    }.collect { newDailyAverageState ->
                        _state.update { it.copy(dailyAverageState = newDailyAverageState) }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Ensure final steps are saved before view model is destroyed
        applicationScope.launch {
            val currentState = _state.value
            if (currentState.profileId.isNotEmpty()) {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                trackRepository.saveCurrentSteps(currentState.profileId, today, currentState.currentSteps)
            }
        }
        stepTracker.stopTracking()
    }

    fun onAction(action: StepCounterAction) {
        when(action) {
            StepCounterAction.OnToDoClick -> {}
            StepCounterAction.TogglePermissionRequest -> { togglePermissionRequest() }
            StepCounterAction.ToggleBackgroundAccessBottomSheet -> { toggleBackgroundAccessBottomSheet() }
            StepCounterAction.ToggleEnableAccessManuallyBottomSheet -> { toggleEnableAccessManuallyBottomSheet() }
            StepCounterAction.OnToggleStepGoalBottomSheet -> { toggleStepGoalBottomSheet() }
            StepCounterAction.StartTracking -> { stepTracker.startTracking() }
            StepCounterAction.ToggleEditStepsDialog -> { toggleEditStepsDialog() }
            StepCounterAction.OpenEditStepsDialog -> { openEditStepsDialog() }
            StepCounterAction.ToggleEditDateClick -> { toggleDatePickerDialog() }
            StepCounterAction.TogglePlayPause -> { togglePlayPause() }
            is StepCounterAction.OnSaveStepGoal -> { saveStepGoal(action.value) }
            is StepCounterAction.OnConfirmEditSteps -> { confirmEditSteps(action.date, action.steps) }
            is StepCounterAction.OnConfirmEditDate -> { confirmEditDate(action.date) }
            StepCounterAction.OnToggleResetStepsConfirmationDialog -> { toggleResetStepsConfirmationDialog() }
            StepCounterAction.OnResetTodayStepsClick -> { resetTodaySteps() }
            StepCounterAction.OnAppBackgrounded -> { insightSessionHolder.pendingForegroundRefresh = true }
            StepCounterAction.OnAppResumed -> { onAppResumed() }
            StepCounterAction.OnTryAgainInsightClick -> { refreshInsight() }
            else -> Unit
        }
    }

    private fun onAppResumed() {
        // Only refresh when the app actually returned from background (a real ON_STOP happened).
        // Lifecycle observers re-attached after back-navigation replay ON_RESUME without ON_STOP,
        // and those must reuse the cached insight.
        if (insightSessionHolder.pendingForegroundRefresh) {
            insightSessionHolder.pendingForegroundRefresh = false
            refreshInsight()
        }
    }

    /**
     * [steps] and [goal] override the values from the state when the caller already knows
     * fresher ones than the state collectors have applied.
     */
    private fun refreshInsight(steps: Int? = null, goal: Int? = null) {
        viewModelScope.launch {
            val isOnline = connectivityObserver.isConnected.first()
            if (!isOnline) {
                // No API request while offline; the block shows a static message + Try Again
                _state.update { it.copy(isInsightOffline = true, isInsightLoading = false) }
                return@launch
            }

            // Connectivity is available, so the block leaves the offline state regardless
            // of whether the API call itself later succeeds
            _state.update { it.copy(isInsightLoading = true, isInsightOffline = false, isInsightError = false) }

            val currentState = _state.value
            val hour = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).hour
            val context = ActivityContext.create(
                currentSteps = steps ?: currentState.currentSteps,
                dailyStepGoal = goal ?: currentState.dailyGoalSteps,
                hourOfDay = hour
            )

            when (val result = aiCoachRepository.generateInsight(context)) {
                is Result.Success -> {
                    insightSessionHolder.cachedInsight = result.data
                    _state.update { it.copy(
                        aiInsight = result.data,
                        isInsightLoading = false,
                        isInsightOffline = false,
                        isInsightError = false
                    ) }
                }
                is Result.Error -> {
                    if (result.error == DataError.Network.NO_INTERNET) {
                        _state.update { it.copy(isInsightOffline = true, isInsightLoading = false) }
                    } else {
                        // Degrade silently when a previous insight exists; otherwise show an
                        // error with Try Again instead of an empty card
                        _state.update { it.copy(
                            isInsightLoading = false,
                            isInsightError = it.aiInsight == null
                        ) }
                    }
                }
            }
        }
    }

    private fun confirmEditDate(editDate: LocalDate) {
        _state.update { it.copy(
            editedDate = editDate,
            isDatePickerDialogVisible = false,
            isEditStepsDialogVisible = true
        ) }
    }

    private fun togglePlayPause() {
        if(_state.value.isStepTrackerPaused) {
            stepTracker.startTracking()
        } else {
            stepTracker.stopTracking()
        }
        _state.update { it.copy(
            isStepTrackerPaused = !_state.value.isStepTrackerPaused
        ) }
    }

    private fun resetTodaySteps() {
        viewModelScope.launch {
            val profileInfo = profileStorage.get()
            if (profileInfo != null) {
                val currentSensorValue = stepTracker.currentTotalSteps.first()
                trackRepository.resetDailySteps(
                    profileId = profileInfo.id,
                    date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    newBaseline = currentSensorValue
                )
            }
            _state.update { it.copy(
                isResetStepsConfirmationDialogVisible = false,
                activityDurationRaw = Duration.ZERO,
                activityDuration = "0 min"
            ) }
        }
    }

    private fun toggleResetStepsConfirmationDialog() {
        _state.update { it.copy(
            isResetStepsConfirmationDialogVisible = !_state.value.isResetStepsConfirmationDialogVisible
        ) }
    }

    private fun toggleDatePickerDialog() {
        _state.update { it.copy(
            isDatePickerDialogVisible = !_state.value.isDatePickerDialogVisible
        ) }
    }

    private fun confirmEditSteps(date: LocalDate, steps: Int) {
        viewModelScope.launch {
            trackRepository.saveCurrentSteps(state.value.profileId, date, steps)
            
            _state.update { it.copy(
                isEditStepsDialogVisible = false
            ) }
        }
    }

    private fun toggleEditStepsDialog() {
        _state.update { it.copy(
            isEditStepsDialogVisible = !_state.value.isEditStepsDialogVisible
        ) }
    }

    private fun openEditStepsDialog() {
        _state.update { it.copy(
            isEditStepsDialogVisible = true
        ) }
    }

    private fun toggleStepGoalBottomSheet() {
        _state.update {
            it.copy(
                isStepGoalBottomSheetVisible = !_state.value.isStepGoalBottomSheetVisible
            )
        }
    }

    private fun saveStepGoal(value: Int) {
        viewModelScope.launch {
            trackRepository.saveDailyStepGoal(
                profileId = state.value.profileId,
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                stepGoal = value
            )
        }
    }

    fun togglePermissionRequest() {
        _state.update { it.copy(
            isPermissionRequested = !_state.value.isPermissionRequested
        ) }
    }

    private fun toggleEnableAccessManuallyBottomSheet() {
        _state.update {
            it.copy(
                isEnableAccessManuallyBottomSheetVisible = !_state.value.isEnableAccessManuallyBottomSheetVisible
            )
        }
    }

    private fun toggleBackgroundAccessBottomSheet() {
        _state.update {
            it.copy(
                isBackgroundAccessBottomSheetVisible = !_state.value.isBackgroundAccessBottomSheetVisible
            )
        }
    }
}