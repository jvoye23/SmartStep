package com.jvcodingsolutions.smartstep.features.profile_setup

import com.jvcodingsolutions.smartstep.core.presentation.util.UiText

interface ProfileSetupEvent {

    data object ProfileIsSaved: ProfileSetupEvent
    data class Error(val error: UiText): ProfileSetupEvent

}