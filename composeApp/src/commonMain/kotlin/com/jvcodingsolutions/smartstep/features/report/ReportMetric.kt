package com.jvcodingsolutions.smartstep.features.report

enum class ReportMetric {
    STEPS,
    CALORIES,
    TIME,
    DISTANCE
}

enum class DayStatus {
    DONE,        // past day with recorded data (check icon)
    IN_PROGRESS, // today within the current week (clock icon)
    NO_DATA      // no available data (minus icon)
}
