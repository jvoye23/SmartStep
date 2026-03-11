package com.jvcodingsolutions.smartstep

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jvcodingsolutions.smartstep.app.MainViewModel
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.navigation.NavigationRoot
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {

    val viewModel: MainViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SmartStepTheme {
        if(!state.isCheckingProfile) {
            NavigationRoot(
                isProfileSetupComplete = state.isProfileSet
            )
        }
    }
}