package com.aa.carrepair.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class RetryInterceptor @Inject constructor() : Interceptor {
    private val maxRetries = 3
    private val baseDelayMs = 1000L

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        var lastResponse: Response? = null

        repeat(maxRetries) { attempt ->
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful || response.code < 500) {
                    lastResponse?.close()
                    return response
                }
                Timber.w("Server error %d (attempt %d/%d): %s", response.code, attempt + 1, maxRetries, request.url)
                lastResponse?.close()
                lastResponse = response
            } catch (e: IOException) {
                lastException = e
                Timber.w("Request failed (attempt ${attempt + 1}/$maxRetries): %s", e.message)
            }
            if (attempt < maxRetries - 1) {
                Thread.sleep(baseDelayMs * (attempt + 1))
            }
        }

        if (lastException != null) {
            lastResponse?.close()
            throw lastException!!
        }

        return lastResponse ?: throw IOException("Request failed after $maxRetries attempts")
    }
}
