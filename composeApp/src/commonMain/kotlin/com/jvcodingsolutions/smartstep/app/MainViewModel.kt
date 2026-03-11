package com.jvcodingsolutions.smartstep.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val profileStorage: ProfileStorage
): ViewModel() {

    private val _state = MutableStateFlow(MainState())
    private var hasLoadedInitialData = false

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                delay(3000L)
                _state.update { it.copy(
                    isProfileSet = profileStorage.get() != null
                ) }
                _state.update { it.copy(
                    isCheckingProfile = false
                ) }
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )


}