package com.jvcodingsolutions.smartstep.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundMain
import com.jvcodingsolutions.smartstep.features.profile_setup.ProfileSetupScreenRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    isProfileSetupComplete: Boolean
) {
    // The rootBackStack with its NavDisplay will only navigate between features and not single screens
    // The initial screens will be handled in different backStack for each feature
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.ProfileOnboardingRoute::class, Route.ProfileOnboardingRoute.serializer())
                    subclass(Route.StepCounterRoute::class, Route.StepCounterRoute.serializer())
                }
            }
        },
        if(isProfileSetupComplete) Route.StepCounterRoute else Route.ProfileOnboardingRoute
    )
    NavDisplay(
        modifier = modifier,
        backStack = rootBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()

        ),
        entryProvider = entryProvider {
            entry<Route.ProfileOnboardingRoute> {
                ProfileSetupScreenRoot(
                    onSkipClick = {
                        //rootBackStack.navigate(Route.StepCounterRoute)
                        rootBackStack.clear()
                        rootBackStack.add(Route.StepCounterRoute)
                    },
                    onNavigateBackClick = {
                        rootBackStack.clear()
                        rootBackStack.add(Route.StepCounterRoute)
                    },
                    isOnboarding = true
                )
            }
            entry<Route.StepCounterRoute> {
                SmartStepNavigation(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.backgroundMain)
                )

            }
        }
    )
}