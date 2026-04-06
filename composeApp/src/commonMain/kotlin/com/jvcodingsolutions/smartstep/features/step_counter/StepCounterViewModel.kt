package com.jvcodingsolutions.smartstep.features.step_counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.presentation.util.*
import com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker
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
    private val applicationScope: CoroutineScope
): ViewModel() {


    private val _state = MutableStateFlow(StepCounterState())

    private var hasLoadedInitialData = false
    private var numberOfStepsNotWrittenToDb = 0
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
                    trackRepository.getLiveStepsFlow(profileId, today)
                        .distinctUntilChanged()
                        .collect { currentTotalSteps ->
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
                        }
                    }
                }

                // Collect step deltas and update unwritten steps in repository
                launch {
                    stepTracker.stepDeltas.collect { delta ->
                        val currentTime = Clock.System.now().toEpochMilliseconds()
                        val timeSinceLastStep = lastStepDetectionTime?.let {
                            (currentTime - it).milliseconds
                        }
                        lastStepDetectionTime = currentTime

                        val activeTimeDelta = calculateActiveTimeDelta(timeSinceLastStep)

                        // Centralize delta tracking in repository
                        trackRepository.addStepDelta(delta)
                        if (activeTimeDelta > Duration.ZERO) {
                            trackRepository.addDurationDelta(activeTimeDelta)
                        }

                        // Periodic DB save logic
                        numberOfStepsNotWrittenToDb += delta
                        if (numberOfStepsNotWrittenToDb >= savingStepsToDbInterval) {
                            trackRepository.saveCurrentSteps(profileId, today, _state.value.currentSteps)
                            trackRepository.saveActivityDuration(profileId, today, _state.value.activityDurationRaw)
                            numberOfStepsNotWrittenToDb = 0
                        }
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
            else -> Unit
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
            numberOfStepsNotWrittenToDb = 0
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
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            trackRepository.saveCurrentSteps(state.value.profileId, date, steps)
            
            if (date == today) {
                numberOfStepsNotWrittenToDb = 0
                // The live steps flow will pick up the change from DB, 
                // but we can update immediately for better UX if needed.
                // However, the collector in loadInitialData is already watching this.
            }
            
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