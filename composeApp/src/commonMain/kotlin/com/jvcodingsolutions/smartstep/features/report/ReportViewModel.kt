package com.jvcodingsolutions.smartstep.features.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo
import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.presentation.util.MeasurementSystem
import com.jvcodingsolutions.smartstep.core.presentation.util.calculateCalories
import com.jvcodingsolutions.smartstep.core.presentation.util.calculateDistance
import com.jvcodingsolutions.smartstep.core.presentation.util.formatDistanceValue
import com.jvcodingsolutions.smartstep.core.presentation.util.formatWeekRange
import com.jvcodingsolutions.smartstep.core.presentation.util.fullDayName
import com.jvcodingsolutions.smartstep.core.presentation.util.startOfWeek
import com.jvcodingsolutions.smartstep.design_system.util.formattedSteps
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration

class ReportViewModel(
    private val trackRepository: TrackRepository,
    private val profileStorage: ProfileStorage
): ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    private var hasLoadedInitialData = false

    private val weekStartFlow = MutableStateFlow(_state.value.weekStart)
    private val metricFlow = MutableStateFlow(ReportMetric.STEPS)

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )

    fun onAction(action: ReportAction) {
        when(action) {
            ReportAction.OnPreviousWeekClick -> {
                weekStartFlow.update { it.minus(7, DateTimeUnit.DAY) }
            }
            ReportAction.OnNextWeekClick -> {
                val today = today()
                // Navigating forward is only possible into weeks that have already started
                if (weekStartFlow.value < today.startOfWeek()) {
                    weekStartFlow.update { it.plus(7, DateTimeUnit.DAY) }
                }
            }
            is ReportAction.OnMetricSelected -> {
                metricFlow.value = action.metric
            }
            ReportAction.OnBackClick -> Unit // handled by the navigation layer
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadData() {
        viewModelScope.launch {
            val profileInfo = profileStorage.get() ?: return@launch

            val weeklyTracksFlow = weekStartFlow.flatMapLatest { weekStart ->
                trackRepository.getWeeklyTracksFlow(
                    profileId = profileInfo.id,
                    startDate = weekStart,
                    endDate = weekStart.plus(6, DateTimeUnit.DAY)
                ).map { tracks -> weekStart to tracks }
            }

            // Re-subscribe to the live flows when the date rolls over at midnight
            todayFlow().flatMapLatest { today ->
                combine(
                    weeklyTracksFlow,
                    metricFlow,
                    trackRepository.getLiveStepsFlow(profileInfo.id, today),
                    trackRepository.getLiveDurationFlow(profileInfo.id, today)
                ) { (weekStart, tracks), metric, todayLiveSteps, todayLiveDuration ->
                    buildState(
                        profileInfo = profileInfo,
                        weekStart = weekStart,
                        tracks = tracks,
                        metric = metric,
                        today = today,
                        todayLiveSteps = todayLiveSteps,
                        todayLiveDuration = todayLiveDuration
                    )
                }
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    private fun buildState(
        profileInfo: ProfileInfo,
        weekStart: LocalDate,
        tracks: List<Tracks>,
        metric: ReportMetric,
        today: LocalDate,
        todayLiveSteps: Int,
        todayLiveDuration: Duration
    ): ReportState {
        val measurementSystem = if (profileInfo.isMetricSystem) {
            MeasurementSystem.METRIC
        } else {
            MeasurementSystem.IMPERIAL
        }
        val trackByDate = tracks.associateBy { it.currentDate }
        val weekDates = (0..6).map { weekStart.plus(it, DateTimeUnit.DAY) }

        var total = 0.0
        var daysWithData = 0

        val dayItems = weekDates.map { date ->
            val track = trackByDate[date]
            val isToday = date == today
            val steps = if (isToday) todayLiveSteps else track?.currentSteps ?: 0
            val minutes = if (isToday) {
                todayLiveDuration.inWholeMinutes
            } else {
                track?.minutes?.inWholeMinutes ?: 0L
            }

            val hasData = when {
                isToday -> steps > 0 || minutes > 0
                else -> track != null && date < today
            }

            val status = when {
                isToday -> DayStatus.IN_PROGRESS
                hasData -> DayStatus.DONE
                else -> DayStatus.NO_DATA
            }

            val metricValue: Double = when (metric) {
                ReportMetric.STEPS -> steps.toDouble()
                ReportMetric.CALORIES -> calculateCalories(steps, profileInfo).toDouble()
                ReportMetric.TIME -> minutes.toDouble()
                ReportMetric.DISTANCE -> calculateDistance(
                    steps = steps,
                    heightCm = profileInfo.heightInCm ?: 175,
                    system = measurementSystem
                )
            }

            total += metricValue
            if (hasData) daysWithData++

            ReportDayUi(
                date = date,
                dayName = date.fullDayName(),
                value = formatMetricValue(metric, metricValue),
                status = status,
                goalSteps = if (metric == ReportMetric.STEPS && status != DayStatus.NO_DATA) {
                    track?.dailyStepGoal ?: DEFAULT_STEP_GOAL
                } else null,
                isToday = isToday
            )
        }

        // The daily average only considers days that actually have recorded data
        val average = if (daysWithData > 0) total / daysWithData else 0.0

        return ReportState(
            isMetricSystem = profileInfo.isMetricSystem,
            selectedMetric = metric,
            weekStart = weekStart,
            weekRangeLabel = formatWeekRange(weekStart, weekStart.plus(6, DateTimeUnit.DAY)),
            isNextWeekEnabled = weekStart < today.startOfWeek(),
            isCurrentWeek = weekStart == today.startOfWeek(),
            weeklyTotal = formatMetricValue(metric, total),
            dailyAverage = formatMetricValue(metric, average, isAverage = true),
            dayItems = dayItems
        )
    }

    private fun formatMetricValue(
        metric: ReportMetric,
        value: Double,
        isAverage: Boolean = false
    ): String {
        return when (metric) {
            ReportMetric.DISTANCE -> formatDistanceValue(value)
            ReportMetric.STEPS -> {
                val rounded = value.toInt()
                if (isAverage) "$rounded" else formattedSteps(rounded)
            }
            else -> "${value.toInt()}"
        }
    }

    private fun todayFlow(): Flow<LocalDate> = flow {
        while (true) {
            emit(today())
            delay(DATE_CHECK_INTERVAL_MS)
        }
    }.distinctUntilChanged()

    private fun today(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    companion object {
        private const val DEFAULT_STEP_GOAL = 6000
        private const val DATE_CHECK_INTERVAL_MS = 60_000L
    }
}
