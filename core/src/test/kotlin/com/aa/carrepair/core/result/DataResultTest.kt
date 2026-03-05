package com.aa.carrepair.core.result

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataResultTest {

    // ── Success ─────────────────────────────────────────────────────────

    @Test
    fun `Success isSuccess returns true`() {
        assertTrue(DataResult.Success("data").isSuccess)
    }

    @Test
    fun `Success isError returns false`() {
        assertFalse(DataResult.Success("data").isError)
    }

    @Test
    fun `Success isLoading returns false`() {
        assertFalse(DataResult.Success("data").isLoading)
    }

    @Test
    fun `Success getOrNull returns value`() {
        assertEquals("data", DataResult.Success("data").getOrNull())
    }

    @Test
    fun `Success getOrThrow returns value`() {
        assertEquals("data", DataResult.Success("data").getOrThrow())
    }

    // ── Error ───────────────────────────────────────────────────────────

    @Test
    fun `Error isError returns true`() {
        assertTrue(DataResult.Error(RuntimeException("fail")).isError)
    }

    @Test
    fun `Error isSuccess returns false`() {
        assertFalse(DataResult.Error(RuntimeException("fail")).isSuccess)
    }

    @Test
    fun `Error getOrNull returns null`() {
        assertNull(DataResult.Error(RuntimeException("fail")).getOrNull())
    }

    @Test(expected = RuntimeException::class)
    fun `Error getOrThrow throws original exception`() {
        DataResult.Error(RuntimeException("fail")).getOrThrow()
    }

    @Test
    fun `Error preserves message`() {
        val result = DataResult.Error(RuntimeException("oops"), "custom message")
        assertEquals("custom message", result.message)
    }

    // ── Loading ─────────────────────────────────────────────────────────

    @Test
    fun `Loading isLoading returns true`() {
        assertTrue(DataResult.Loading.isLoading)
    }

    @Test
    fun `Loading isSuccess returns false`() {
        assertFalse(DataResult.Loading.isSuccess)
    }

    @Test
    fun `Loading getOrNull returns null`() {
        assertNull(DataResult.Loading.getOrNull())
    }

    @Test(expected = IllegalStateException::class)
    fun `Loading getOrThrow throws IllegalStateException`() {
        DataResult.Loading.getOrThrow()
    }

    // ── map ─────────────────────────────────────────────────────────────

    @Test
    fun `map transforms Success value`() {
        val result = DataResult.Success(5).map { it * 3 }
        assertEquals(15, result.getOrNull())
    }

    @Test
    fun `map preserves Error`() {
        val error: DataResult<Int> = DataResult.Error(RuntimeException("err"))
        val mapped = error.map { it * 2 }
        assertTrue(mapped.isError)
    }

    @Test
    fun `map preserves Loading`() {
        val loading: DataResult<Int> = DataResult.Loading
        val mapped = loading.map { it * 2 }
        assertTrue(mapped.isLoading)
    }

    @Test
    fun `map can change type`() {
        val result = DataResult.Success(42).map { it.toString() }
        assertEquals("42", result.getOrNull())
    }

    // ── onSuccess / onError ─────────────────────────────────────────────

    @Test
    fun `onSuccess invoked for Success`() {
        var captured = ""
        DataResult.Success("hello").onSuccess { captured = it }
        assertEquals("hello", captured)
    }

    @Test
    fun `onSuccess not invoked for Error`() {
        var invoked = false
        DataResult.Error(RuntimeException()).onSuccess { invoked = true }
        assertFalse(invoked)
    }

    @Test
    fun `onError invoked for Error`() {
        var msg = ""
        DataResult.Error(RuntimeException("oops")).onError { msg = it.message ?: "" }
        assertEquals("oops", msg)
    }

    @Test
    fun `onError not invoked for Success`() {
        var invoked = false
        DataResult.Success("ok").onError { invoked = true }
        assertFalse(invoked)
    }

    @Test
    fun `onSuccess returns same result for chaining`() {
        val result = DataResult.Success("abc")
        val returned = result.onSuccess { }
        assertTrue(returned === result)
    }

    // ── safeApiCall ─────────────────────────────────────────────────────

    @Test
    fun `safeApiCall wraps success`() = runTest {
        val result = safeApiCall { "ok" }
        assertEquals("ok", result.getOrNull())
    }

    @Test
    fun `safeApiCall catches exception`() = runTest {
        val result = safeApiCall<String> { throw IllegalArgumentException("bad") }
        assertTrue(result.isError)
    }

    @Test
    fun `safeApiCall preserves exception message`() = runTest {
        val result = safeApiCall<String> { throw RuntimeException("network fail") }
        val error = result as DataResult.Error
        assertEquals("network fail", error.message)
    }
}
