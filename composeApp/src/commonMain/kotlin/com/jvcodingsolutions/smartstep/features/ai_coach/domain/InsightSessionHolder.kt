package com.jvcodingsolutions.smartstep.features.ai_coach.domain

/**
 * In-memory session cache for the AI insight shown on the main screen.
 * Survives navigation and ViewModel recreation, dies with the process —
 * which is exactly the "session" scope the requirements define.
 */
class InsightSessionHolder {
    var cachedInsight: String? = null
    var lastGoalSeen: Int? = null

    // Set when the app actually goes to background (ON_STOP) so that the next
    // ON_RESUME can be distinguished from a resume caused by back-navigation.
    var pendingForegroundRefresh: Boolean = false
}
