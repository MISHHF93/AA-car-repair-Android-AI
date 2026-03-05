package com.aa.carrepair.feature.voice

import org.junit.Assert.assertEquals
import org.junit.Test

class VoiceUiStateTest {

    @Test
    fun `default state is IDLE`() {
        val state = VoiceUiState()
        assertEquals(VoiceState.IDLE, state.state)
    }

    @Test
    fun `default transcript is empty`() {
        val state = VoiceUiState()
        assertEquals("", state.transcript)
    }

    @Test
    fun `default response is empty`() {
        val state = VoiceUiState()
        assertEquals("", state.response)
    }

    @Test
    fun `default isOffline is false`() {
        val state = VoiceUiState()
        assertEquals(false, state.isOffline)
    }

    @Test
    fun `VoiceState enum has correct values`() {
        assertEquals(4, VoiceState.entries.size)
        assertEquals(VoiceState.IDLE, VoiceState.valueOf("IDLE"))
        assertEquals(VoiceState.LISTENING, VoiceState.valueOf("LISTENING"))
        assertEquals(VoiceState.PROCESSING, VoiceState.valueOf("PROCESSING"))
        assertEquals(VoiceState.SPEAKING, VoiceState.valueOf("SPEAKING"))
    }
}
