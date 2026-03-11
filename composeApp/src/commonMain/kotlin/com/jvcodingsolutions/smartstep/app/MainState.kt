package com.jvcodingsolutions.smartstep.app

import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo

data class MainState(
    val isProfileSet: Boolean = false,
    val isCheckingProfile: Boolean = true,
)