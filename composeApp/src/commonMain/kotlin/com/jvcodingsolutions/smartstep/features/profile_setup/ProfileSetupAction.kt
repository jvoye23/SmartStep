package com.jvcodingsolutions.smartstep.features.profile_setup

import com.jvcodingsolutions.smartstep.core.domain.Height
import com.jvcodingsolutions.smartstep.core.domain.Weight
import com.jvcodingsolutions.smartstep.core.domain.model.Gender

sealed interface ProfileSetupAction {
    data object OnStartClick: ProfileSetupAction
    data object OnGenderDropdownClick: ProfileSetupAction
    data object OnToggleHeightDropdownClick: ProfileSetupAction
    data object OnToggleWeightDropdownClick: ProfileSetupAction
    data object OnSkipClick: ProfileSetupAction

    data class OnGenderOptionSelected(val option: Gender): ProfileSetupAction
    data class OnConfirmSelectedHeight(val height: Height): ProfileSetupAction
    data class OnConfirmSelectedWeight(val weight: Weight): ProfileSetupAction

}