package com.aa.carrepair.data.remote.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var interceptor: AuthInterceptor
    private val testApiKey = "test-api-key-12345"

    @Before
    fun setUp() {
        interceptor = AuthInterceptor(testApiKey)
    }

    @Test
    fun `adds Authorization bearer header`() {
        val originalRequest = Request.Builder().url("https://api.example.com/v1/test").build()
        val chain = mockk<Interceptor.Chain>()
        val expectedResponse = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns expectedResponse

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.header("Authorization") == "Bearer $testApiKey"
            })
        }
    }

    @Test
    fun `adds Content-Type json header`() {
        val originalRequest = Request.Builder().url("https://api.example.com/v1/test").build()
        val chain = mockk<Interceptor.Chain>()
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.header("Content-Type") == "application/json"
            })
        }
    }

    @Test
    fun `adds X-Client-Platform android header`() {
        val originalRequest = Request.Builder().url("https://api.example.com/v1/test").build()
        val chain = mockk<Interceptor.Chain>()
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.header("X-Client-Platform") == "android"
            })
        }
    }

    @Test
    fun `preserves original URL`() {
        val url = "https://api.example.com/v1/vehicle/vin/1HGBH41JXMN109186"
        val originalRequest = Request.Builder().url(url).build()
        val chain = mockk<Interceptor.Chain>()
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody())
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns response

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.url.toString() == url
            })
        }
    }
}
