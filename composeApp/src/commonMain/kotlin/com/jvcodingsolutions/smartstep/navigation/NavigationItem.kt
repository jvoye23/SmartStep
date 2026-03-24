package com.jvcodingsolutions.smartstep.navigation

import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.StringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.exit
import smartstep.composeapp.generated.resources.personal_settings
import smartstep.composeapp.generated.resources.step_goal

data class NavigationItem(
    val title: StringResource
)

val TOP_LEVEL_DESTINATIONS: Map<NavKey, NavigationItem> = mapOf<NavKey, NavigationItem>(
    Route.StepCounterRoute.StepGoalRoute to NavigationItem(
        title = Res.string.step_goal
    ),
    /*Route.StepCounterRoute.PersonalSettingsRoute to NavigationItem(
        title = Res.string.personal_settings
    ),*/

    /*Route.StepCounterRoute.ExitRoute to NavigationItem(
        title = Res.string.exit
    )*/
)

