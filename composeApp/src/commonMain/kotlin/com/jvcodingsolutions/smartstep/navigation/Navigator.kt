package com.jvcodingsolutions.smartstep.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        // 1. Check if the target route is a Top Level Destination (Tab)
        if (route in state.backStacks.keys) {
            // Switch to this tab
            state.topLevelRoute = route

            // 2. CRITICAL CHANGE: Reset the stack for this tab
            // This ensures that whether you are switching tabs or clicking the current one,
            // you always land on the "Root" of that tab, not a sub-screen.
            state.backStacks[route]?.apply {
                clear()
                add(route)
            }
        } else {
            // Standard behavior: Push new screen onto the ACTIVE stack
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} does not exist")

        val currentRoute = currentStack.lastOrNull()

        // If we are at the root of the tab, typically we might want to:
        // A) Do nothing (or close app)
        // B) Go back to the "Start Route" (Home)
        if (currentRoute == state.topLevelRoute) {
            // Optional: If you want 'Back' on a root tab to go to the Start Route (Menu)
            if (state.topLevelRoute != state.startRoute) {
                state.topLevelRoute = state.startRoute
            }
        } else {
            // Otherwise, pop the top screen (e.g., remove PizzaDetail)
            currentStack.removeLastOrNull()
        }
    }
}