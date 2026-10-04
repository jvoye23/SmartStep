package com.jvcodingsolutions.smartstep.core.data.networking

import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(DataError.Network.SERIALIZATION)
            } catch (e: Exception) {
                // Decoding failures surface as JsonConvertException, not NoTransformationFoundException
                coroutineContext.ensureActive()
                Result.Error(DataError.Network.SERIALIZATION)
            }
        }
        400 -> Result.Error(DataError.Network.BAD_REQUEST)
        401, 403 -> Result.Error(DataError.Network.UNAUTHORIZED)
        404 -> Result.Error(DataError.Network.NOT_FOUND)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}
