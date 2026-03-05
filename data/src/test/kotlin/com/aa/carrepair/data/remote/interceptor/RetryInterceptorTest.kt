package com.aa.carrepair.data.remote.interceptor

import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RetryInterceptorTest {

    private lateinit var interceptor: RetryInterceptor

    @Before
    fun setUp() {
        interceptor = RetryInterceptor()
    }

    private fun buildResponse(request: Request, code: Int): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(code)
            .message("msg")
            .body("{}".toResponseBody())
            .build()

    @Test
    fun `returns response on first successful attempt`() {
        val request = Request.Builder().url("https://api.example.com/test").build()
        val chain = mockk<Interceptor.Chain>()

        every { chain.request() } returns request
        every { chain.proceed(request) } returns buildResponse(request, 200)

        val response = interceptor.intercept(chain)
        assertEquals(200, response.code)
    }

    @Test
    fun `returns 4xx response without retrying`() {
        val request = Request.Builder().url("https://api.example.com/test").build()
        val chain = mockk<Interceptor.Chain>()

        every { chain.request() } returns request
        every { chain.proceed(request) } returns buildResponse(request, 404)

        val response = interceptor.intercept(chain)
        assertEquals(404, response.code)
    }

    @Test(expected = IOException::class)
    fun `throws after max retries with IOException`() {
        val request = Request.Builder().url("https://api.example.com/test").build()
        val chain = mockk<Interceptor.Chain>()

        every { chain.request() } returns request
        every { chain.proceed(request) } throws IOException("Connection refused")

        interceptor.intercept(chain)
    }

    @Test
    fun `retries on 500 server error and succeeds`() {
        val request = Request.Builder().url("https://api.example.com/test").build()
        val chain = mockk<Interceptor.Chain>()

        var callCount = 0
        every { chain.request() } returns request
        every { chain.proceed(request) } answers {
            callCount++
            if (callCount < 3) buildResponse(request, 500)
            else buildResponse(request, 200)
        }

        val response = interceptor.intercept(chain)
        assertEquals(200, response.code)
    }
}
