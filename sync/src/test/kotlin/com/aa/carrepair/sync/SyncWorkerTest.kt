package com.aa.carrepair.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncWorkerTest {

    @Test
    fun `MAX_RETRY_COUNT is 3`() {
        assertEquals(3, SyncWorker.MAX_RETRY_COUNT)
    }
}
