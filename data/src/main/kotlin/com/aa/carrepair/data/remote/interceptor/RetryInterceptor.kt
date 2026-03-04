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

        repeat(maxRetries) { attempt ->
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful || response.code < 500) return response
                response.close()
            } catch (e: IOException) {
                lastException = e
                Timber.w("Request failed (attempt ${attempt + 1}/$maxRetries): %s", e.message)
                if (attempt < maxRetries - 1) {
                    Thread.sleep(baseDelayMs * (attempt + 1))
                }
            }
        }

        throw lastException ?: IOException("Request failed after $maxRetries attempts")
    }
}
