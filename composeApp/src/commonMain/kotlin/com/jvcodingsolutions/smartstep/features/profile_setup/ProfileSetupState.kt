package com.jvcodingsolutions.smartstep.features.profile_setup

import com.jvcodingsolutions.smartstep.core.domain.Height
import com.jvcodingsolutions.smartstep.core.domain.Weight
import com.jvcodingsolutions.smartstep.core.domain.model.Gender

data class ProfileSetupState(
    val isHeightPickerVisible: Boolean = false,
    val isWeightPickerVisible: Boolean = false,
    val isGenderDropdownExpanded: Boolean = false,
    val genderOptions: List<String> = listOf("Female", "Male"),
    val selectedGender: Gender = Gender.FEMALE,
    val selectedHeight: Height = Height(cm = 170),
    val selectedWeight: Weight = Weight(kg = 60)
)
