package com.jvcodingsolutions.smartstep.features.profile_setup

import com.jvcodingsolutions.smartstep.core.domain.model.Gender
import com.jvcodingsolutions.smartstep.core.domain.toFeetInches
import com.jvcodingsolutions.smartstep.core.domain.toLbs

data class ProfileSetupState(
    val isHeightPickerVisible: Boolean = false,
    val isWeightPickerVisible: Boolean = false,
    val isGenderDropdownExpanded: Boolean = false,
    val genderOptions: List<String> = listOf("Female", "Male"),
    val profileId: String = "",
    val selectedGender: Gender = Gender.FEMALE,
    val selectedHeightInCm: Int? = 170,
    val selectedHeightInFeetInches: Pair<Int?, Int?> = Pair(first = 5, second = 10),
    val selectedWeightInKg: Int? = 60,
    val selectedWeightInLbs: Int? = 145,
    val isMetricSystem: Boolean = true
) {
    val formattedFeetInches: String = selectedHeightInCm?.toFeetInches()?.first.toString() + "'" +
            selectedHeightInCm?.toFeetInches()?.second.toString() + "\""

    val formattedLbs: String = selectedWeightInKg?.toLbs().toString() + " lbs"
}
