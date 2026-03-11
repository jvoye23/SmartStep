package com.jvcodingsolutions.smartstep.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {
    @Serializable
    data object ProfileOnboardingRoute: Route, NavKey {
        @Serializable
        data object ProfileSetupScreenRoute: Route, NavKey
    }

    @Serializable
    data object StepCounterRoute: Route, NavKey {
        @Serializable
        data object PersonalSettingsRoute: Route, NavKey

        @Serializable
        data object StepGoalRoute: Route, NavKey

     //@Serializable
     //   data object ExitRoute: Route, NavKey
    }
}