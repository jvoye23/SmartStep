package com.jvcodingsolutions.smartstep.core.presentation.util

import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import smartstep.composeapp.generated.resources.error_disk_full
import smartstep.composeapp.generated.resources.error_no_internet
import smartstep.composeapp.generated.resources.error_payload_too_large
import smartstep.composeapp.generated.resources.error_request_timeout
import smartstep.composeapp.generated.resources.error_serialization
import smartstep.composeapp.generated.resources.error_server_error
import smartstep.composeapp.generated.resources.error_too_many_requests
import smartstep.composeapp.generated.resources.error_unknown
import smartstep.composeapp.generated.resources.Res

fun DataError.asUiText(): UiText {
    return when(this) {
        DataError.Local.DISK_FULL -> UiText.Resource(
            Res.string.error_disk_full
        )
        DataError.Network.REQUEST_TIMEOUT -> UiText.Resource(
            Res.string.error_request_timeout
        )

        DataError.Network.TOO_MANY_REQUESTS -> UiText.Resource(
            Res.string.error_too_many_requests
        )
        DataError.Network.NO_INTERNET -> UiText.Resource(
            Res.string.error_no_internet
        )
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.Resource(
            Res.string.error_payload_too_large
        )
        DataError.Network.SERVER_ERROR -> UiText.Resource(
            Res.string.error_server_error
        )
        DataError.Network.SERIALIZATION -> UiText.Resource(
            Res.string.error_serialization
        )
        else -> UiText.Resource(
            Res.string.error_unknown
        )
    }

}