package com.aa.carrepair.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    @Named("api_key") private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Client-Platform", "android")
            .build()
        return chain.proceed(request)
    }
}
