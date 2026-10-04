package com.jvcodingsolutions.smartstep.features.ai_coach.data

import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import com.jvcodingsolutions.smartstep.BuildKonfig
import com.jvcodingsolutions.smartstep.core.data.networking.safeCall
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiRequest
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.delay

class GeminiRemoteDataSource(
    private val httpClient: HttpClient
) {

    suspend fun generateContent(request: GeminiRequest): Result<GeminiResponse, DataError.Network> {
        // Without a configured key every call would be rejected; don't send (and retry) it
        if (BuildKonfig.GEMINI_API_KEY.isBlank()) {
            return Result.Error(DataError.Network.UNAUTHORIZED)
        }
        // The free tier occasionally returns transient 404/503/429 blips under load; a couple
        // of short retries smooth those over so they don't surface as a user-facing error.
        var lastError: Result.Error<DataError.Network>? = null
        repeat(MAX_ATTEMPTS) { attempt ->
            val result = safeCall<GeminiResponse> {
                httpClient.post("$BASE_URL/models/$MODEL:generateContent") {
                    header("x-goog-api-key", BuildKonfig.GEMINI_API_KEY)
                    setBody(request)
                }
            }
            when (result) {
                is Result.Success -> return result
                is Result.Error -> {
                    if (result.error == DataError.Network.NO_INTERNET) return result
                    lastError = result
                    if (attempt < MAX_ATTEMPTS - 1) delay(RETRY_DELAY_MILLIS)
                }
            }
        }
        return lastError ?: Result.Error(DataError.Network.UNKNOWN)
    }

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "gemini-3.5-flash"
        private const val MAX_ATTEMPTS = 3
        private const val RETRY_DELAY_MILLIS = 800L
    }
}
