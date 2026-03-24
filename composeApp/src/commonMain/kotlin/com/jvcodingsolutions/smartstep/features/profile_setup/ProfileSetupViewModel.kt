@file:OptIn(ExperimentalUuidApi::class)

package com.jvcodingsolutions.smartstep.features.profile_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.Height
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.Weight
import com.jvcodingsolutions.smartstep.core.domain.model.Gender
import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo
import com.jvcodingsolutions.smartstep.core.presentation.util.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProfileSetupViewModel(
    private val profileStorage: ProfileStorage
): ViewModel() {

    private val _state = MutableStateFlow(ProfileSetupState())

    private var hasLoadedInitialData = false

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {

                getProfileInfo()

                hasLoadedInitialData = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )

    private val eventChannel = Channel<ProfileSetupEvent>()
    val events = eventChannel.receiveAsFlow()

    private fun getProfileInfo() {
        viewModelScope.launch {
            val profileInfo = profileStorage.get()
            if (profileInfo != null) {
                _state.update { it.copy(
                    profileId= profileInfo.id,
                    selectedGender = profileInfo.gender,
                    selectedHeightInCm = profileInfo.heightInCm,
                    selectedHeightInFeetInches = Pair(first = profileInfo.feet, second = profileInfo.inches),
                    selectedWeightInKg = profileInfo.weightInKg,
                    isMetricSystem = profileInfo.isMetricSystem
                ) }
            }
        }
    }

    fun onAction(action: ProfileSetupAction) {
        when (action) {
            ProfileSetupAction.OnStartClick -> {finishProfileSetup()}
            ProfileSetupAction.OnSaveClick -> {saveProfileSettings()}
            ProfileSetupAction.OnGenderDropdownClick -> {toggleGenderDropdown()}
            ProfileSetupAction.OnToggleHeightDropdownClick -> {toggleHeightPicker()}
            ProfileSetupAction.OnToggleWeightDropdownClick -> {toggleWeightPicker()}
            is ProfileSetupAction.OnGenderOptionSelected -> {selectGender(option = action.option)}
            is ProfileSetupAction.OnConfirmSelectedHeight -> {confirmSelectedHeight(selectedHeight = action.height)}
            is ProfileSetupAction.OnConfirmSelectedWeight -> {confirmSelectedWeight(selectedWeight = action.weight)}
            is ProfileSetupAction.OnChangeUnitSystem -> { changeUnitSystem(isMetricSystem = action.isMetricSystem) }
            else -> Unit

        }
    }

    private fun changeUnitSystem(isMetricSystem: Boolean) {
        _state.update { it.copy(
            isMetricSystem = isMetricSystem
        ) }
    }

    private fun finishProfileSetup() {
        viewModelScope.launch {
            val newProfileInfo = ProfileInfo(
                id = Uuid.random().toString(),
                gender = state.value.selectedGender,
                weightInKg = state.value.selectedWeightInKg,
                heightInCm = state.value.selectedHeightInCm,
                feet = state.value.selectedHeightInFeetInches.first,
                inches = state.value.selectedHeightInFeetInches.second,
                isMetricSystem = state.value.isMetricSystem
            )

            when(val result = profileStorage.upsert(newProfileInfo)) {
                is com.jvcodingsolutions.multipizza.core.domain.util.Result.Success -> {
                    eventChannel.send(ProfileSetupEvent.ProfileIsSaved)
                }
                is com.jvcodingsolutions.multipizza.core.domain.util.Result.Error -> {
                    eventChannel.send(ProfileSetupEvent.Error(result.error.asUiText()))
                }
            }
        }
    }

    private fun saveProfileSettings() {
        viewModelScope.launch {
            val newProfileInfo = ProfileInfo(
                id = state.value.profileId,
                gender = state.value.selectedGender,
                weightInKg = state.value.selectedWeightInKg,
                heightInCm = state.value.selectedHeightInCm,
                feet = state.value.selectedHeightInFeetInches.first,
                inches = state.value.selectedHeightInFeetInches.second,
                isMetricSystem = state.value.isMetricSystem
            )
            when(val result = profileStorage.upsert(newProfileInfo)) {
                is com.jvcodingsolutions.multipizza.core.domain.util.Result.Success -> {
                    eventChannel.send(ProfileSetupEvent.ProfileIsSaved)
                }
                is com.jvcodingsolutions.multipizza.core.domain.util.Result.Error -> {
                    eventChannel.send(ProfileSetupEvent.Error(result.error.asUiText()))
                }
            }
        }

    }

    private fun confirmSelectedWeight(selectedWeight: Weight) {
        _state.update { it.copy(
            selectedWeightInKg = selectedWeight.toKg(),
            isWeightPickerVisible = false
        ) }
    }

    private fun confirmSelectedHeight(selectedHeight: Height) {
        _state.update { it.copy(
            selectedHeightInCm = selectedHeight.toCm(),
            isHeightPickerVisible = false
        ) }
    }

    private fun toggleGenderDropdown() {
        _state.update { it.copy(
            isGenderDropdownExpanded = !_state.value.isGenderDropdownExpanded
        ) }
    }

    private fun selectGender(option: Gender) {
        _state.update { it.copy(
            selectedGender = option
        ) }
    }

    private fun toggleHeightPicker() {
        _state.update { it.copy(
            isHeightPickerVisible = !_state.value.isHeightPickerVisible
        ) }
    }

    private fun toggleWeightPicker() {
        _state.update { it.copy(
            isWeightPickerVisible = !_state.value.isWeightPickerVisible
        ) }
    }
}