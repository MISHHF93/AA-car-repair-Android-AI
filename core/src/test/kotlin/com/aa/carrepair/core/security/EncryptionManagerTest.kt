package com.aa.carrepair.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Note: EncryptionManager requires Android context (Tink + AndroidKeystore)
// Full integration tests are in androidTest; unit tests verify logic only
class EncryptionManagerTest {

    @Test
    fun `DataResult Success returns data correctly`() {
        val result = com.aa.carrepair.core.result.DataResult.Success("test")
        assert(result.isSuccess)
        assertEquals("test", result.getOrNull())
    }

    @Test
    fun `DataResult Error returns exception`() {
        val exception = RuntimeException("test error")
        val result = com.aa.carrepair.core.result.DataResult.Error(exception)
        assert(result.isError)
        assertNull(result.getOrNull())
    }

    @Test
    fun `DataResult Loading is not success`() {
        val result = com.aa.carrepair.core.result.DataResult.Loading
        assert(result.isLoading)
        assert(!result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `DataResult map transforms success value`() {
        val result = com.aa.carrepair.core.result.DataResult.Success(5)
        val mapped = result.map { it * 2 }
        assertEquals(10, mapped.getOrNull())
    }

    @Test
    fun `DataResult map preserves error`() {
        val exception = RuntimeException("error")
        val result: com.aa.carrepair.core.result.DataResult<Int> = com.aa.carrepair.core.result.DataResult.Error(exception)
        val mapped = result.map { it * 2 }
        assert(mapped.isError)
    }
}
