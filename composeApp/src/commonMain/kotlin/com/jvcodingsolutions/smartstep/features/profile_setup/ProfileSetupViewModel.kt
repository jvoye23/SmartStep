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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProfileSetupViewModel(
    private val profileStorage: ProfileStorage
): ViewModel() {

    private val _state = MutableStateFlow(ProfileSetupState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ProfileSetupEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ProfileSetupAction) {
        when (action) {
            ProfileSetupAction.OnStartClick -> {finishProfileSetup()}
            ProfileSetupAction.OnGenderDropdownClick -> {toggleGenderDropdown()}
            ProfileSetupAction.OnToggleHeightDropdownClick -> {toggleHeightPicker()}
            ProfileSetupAction.OnToggleWeightDropdownClick -> {toggleWeightPicker()}
            ProfileSetupAction.OnSkipClick -> {}
            is ProfileSetupAction.OnGenderOptionSelected -> {selectGender(option = action.option)}
            is ProfileSetupAction.OnConfirmSelectedHeight -> {confirmSelectedHeight(selectedHeight = action.height)}
            is ProfileSetupAction.OnConfirmSelectedWeight -> {confirmSelectedWeight(selectedWeight = action.weight)}

        }
    }

    private fun finishProfileSetup() {
        viewModelScope.launch {
            val newProfileInfo = ProfileInfo(
                id = Uuid.random().toString(),
                gender = state.value.selectedGender,
                weight = state.value.selectedWeight.toKg(),
                height = state.value.selectedHeight.toCm()
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
            selectedWeight = selectedWeight,
            isWeightPickerVisible = false
        ) }
    }

    private fun confirmSelectedHeight(selectedHeight: Height) {
        _state.update { it.copy(
            selectedHeight = selectedHeight,
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