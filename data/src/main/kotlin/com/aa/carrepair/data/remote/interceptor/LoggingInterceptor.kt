package com.aa.carrepair.data.remote.interceptor

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

object LoggingInterceptor {
    fun create(isDebug: Boolean): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor.Logger { message ->
            Timber.tag("OkHttp").d(message)
        }
        return HttpLoggingInterceptor(logger).apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}
