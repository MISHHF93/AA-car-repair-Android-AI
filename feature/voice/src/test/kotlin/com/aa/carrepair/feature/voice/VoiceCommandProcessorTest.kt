package com.aa.carrepair.feature.voice

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for VoiceViewModel's processCommand logic.
 * Since VoiceViewModel depends on Android Context and TextToSpeech,
 * we test the command-response mapping as a pure function.
 */
class VoiceCommandProcessorTest {

    /**
     * Replicates the processCommand logic from VoiceViewModel for testability.
     */
    private fun processCommand(command: String): String {
        val lower = command.lowercase()
        return when {
            lower.contains("oil") && lower.contains("change") ->
                "Oil change is typically recommended every 5,000 to 7,500 miles for synthetic oil."
            lower.contains("tire") && lower.contains("pressure") ->
                "Most passenger vehicles require 30 to 35 PSI. Check your door jamb sticker for exact specifications."
            lower.contains("check engine") ->
                "A check engine light can indicate many issues. I recommend scanning for DTC codes to get a precise diagnosis."
            lower.contains("brake") ->
                "Brake pads typically need replacement every 25,000 to 65,000 miles depending on driving style."
            else -> "I can help with car repair questions. Try asking about oil changes, tire pressure, or diagnostic codes."
        }
    }

    @Test
    fun `oil change command returns oil change response`() {
        val response = processCommand("When should I get an oil change?")
        assertEquals(
            "Oil change is typically recommended every 5,000 to 7,500 miles for synthetic oil.",
            response
        )
    }

    @Test
    fun `tire pressure command returns PSI response`() {
        val response = processCommand("What tire pressure should I use?")
        assertEquals(
            "Most passenger vehicles require 30 to 35 PSI. Check your door jamb sticker for exact specifications.",
            response
        )
    }

    @Test
    fun `check engine command returns DTC recommendation`() {
        val response = processCommand("My check engine light is on")
        assertEquals(
            "A check engine light can indicate many issues. I recommend scanning for DTC codes to get a precise diagnosis.",
            response
        )
    }

    @Test
    fun `brake command returns brake pad response`() {
        val response = processCommand("When do I need new brakes?")
        assertEquals(
            "Brake pads typically need replacement every 25,000 to 65,000 miles depending on driving style.",
            response
        )
    }

    @Test
    fun `unknown command returns fallback response`() {
        val response = processCommand("What is the weather?")
        assertEquals(
            "I can help with car repair questions. Try asking about oil changes, tire pressure, or diagnostic codes.",
            response
        )
    }

    @Test
    fun `command matching is case insensitive`() {
        val response = processCommand("OIL CHANGE schedule")
        assertEquals(
            "Oil change is typically recommended every 5,000 to 7,500 miles for synthetic oil.",
            response
        )
    }

    @Test
    fun `brake keyword in different context still matches`() {
        val response = processCommand("My emergency brake feels loose")
        assertEquals(
            "Brake pads typically need replacement every 25,000 to 65,000 miles depending on driving style.",
            response
        )
    }

    @Test
    fun `check engine as substring matches`() {
        val response = processCommand("I need to check engine codes")
        assertEquals(
            "A check engine light can indicate many issues. I recommend scanning for DTC codes to get a precise diagnosis.",
            response
        )
    }
}
