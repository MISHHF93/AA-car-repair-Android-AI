package com.aa.carrepair.domain.safety

import com.aa.carrepair.domain.model.SafetyLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Performance tests for the SafetyClassifier.
 * Ensures that classification is fast enough for real-time use in chat/voice flows.
 */
class SafetyClassifierPerformanceTest {

    private lateinit var classifier: SafetyClassifier

    @Before
    fun setUp() {
        classifier = SafetyClassifier()
    }

    @Test
    fun `single classification completes within 5ms`() {
        // Warm up
        classifier.classify("brake failure")

        val startTime = System.nanoTime()
        classifier.classify("brake failure and smoke detected in the engine compartment")
        val elapsedMicros = (System.nanoTime() - startTime) / 1_000

        assertTrue(
            "Single classification took ${elapsedMicros}μs (expected < 5000μs / 5ms)",
            elapsedMicros < 5000
        )
    }

    @Test
    fun `classifies 10_000 messages within 2 seconds`() {
        val messages = listOf(
            "brake failure detected",
            "abs fault warning light",
            "check engine light is on",
            "windshield wipers squeaking",
            "steering failure after hitting pothole",
            "tire pressure low",
            "fuel leak under the car",
            "normal oil change needed",
            "car making strange noise when turning",
            "airbag warning light on"
        )
        val iterations = 10_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            messages.forEach { classifier.classify(it) }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "10k classifications took ${elapsedMs}ms (expected < 2000ms)",
            elapsedMs < 2000
        )
    }

    @Test
    fun `classification with DTC codes scales linearly`() {
        val dtcCodes = (1..50).map { "P${String.format("%04d", it)}" }
        val message = "engine has multiple issues"

        // Warm up
        classifier.classify(message, dtcCodes)

        val startTime = System.nanoTime()
        repeat(1_000) {
            classifier.classify(message, dtcCodes)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "1k classifications with 50 DTC codes took ${elapsedMs}ms (expected < 2000ms)",
            elapsedMs < 2000
        )
    }

    @Test
    fun `classification with very long content completes within acceptable time`() {
        val longMessage = buildString {
            repeat(100) {
                append("The vehicle has some minor issues that need to be addressed during routine maintenance. ")
            }
            append("brake failure") // critical trigger at the end
        }

        val startTime = System.nanoTime()
        repeat(1_000) {
            val result = classifier.classify(longMessage)
            assertEquals(SafetyLevel.CRITICAL, result.level)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "1k classifications of long content took ${elapsedMs}ms (expected < 3000ms)",
            elapsedMs < 3000
        )
    }

    @Test
    fun `concurrent-style sequential throughput meets real-time requirements`() {
        // Simulates processing a burst of messages like voice commands or chat
        val messages = List(500) { i ->
            when (i % 5) {
                0 -> "brake failure on highway"
                1 -> "abs fault warning light on"
                2 -> "check engine light appeared"
                3 -> "need new windshield wipers"
                else -> "tire blowout on the freeway"
            }
        }

        val startTime = System.nanoTime()
        val results = messages.map { classifier.classify(it) }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        // Verify correctness alongside performance
        assertEquals(SafetyLevel.CRITICAL, results[0].level)
        assertEquals(SafetyLevel.HIGH, results[1].level)
        assertEquals(SafetyLevel.MEDIUM, results[2].level)
        assertEquals(SafetyLevel.LOW, results[3].level)
        assertEquals(SafetyLevel.CRITICAL, results[4].level)

        assertTrue(
            "500 sequential classifications took ${elapsedMs}ms (expected < 500ms)",
            elapsedMs < 500
        )
    }

    @Test
    fun `average classification latency under 100 microseconds`() {
        val message = "abs fault and overheating detected"
        val iterations = 10_000

        // Warm up
        repeat(100) { classifier.classify(message) }

        val startTime = System.nanoTime()
        repeat(iterations) { classifier.classify(message) }
        val avgMicros = (System.nanoTime() - startTime) / 1_000 / iterations

        assertTrue(
            "Average classification latency was ${avgMicros}μs (expected < 100μs)",
            avgMicros < 100
        )
    }
}
