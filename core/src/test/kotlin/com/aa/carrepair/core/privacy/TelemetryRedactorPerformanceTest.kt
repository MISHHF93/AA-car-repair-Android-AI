package com.aa.carrepair.core.privacy

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Performance tests for TelemetryRedactor regex-based PII redaction.
 * Ensures redaction is fast enough for real-time telemetry processing.
 */
class TelemetryRedactorPerformanceTest {

    private lateinit var redactor: TelemetryRedactor

    @Before
    fun setUp() {
        redactor = TelemetryRedactor()
    }

    @Test
    fun `redacts 10_000 strings with mixed PII within 3 seconds`() {
        val inputs = listOf(
            "VIN: 1HGBH41JXMN109186, email: user@example.com, phone: 555-123-4567",
            "Vehicle 2T1BURHE5JC034461 reported an issue",
            "Contact tech@shop.org at (800) 555-0199",
            "No PII in this regular diagnostic log entry about brake pads",
            "Driver license ABC 1234 XYZ may match plate pattern",
            "Multiple VINs: 1HGBH41JXMN109186 and 2T1BURHE5JC034461 in fleet report"
        )
        val iterations = 10_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            inputs.forEach { redactor.redact(it) }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "Redaction of ${iterations * inputs.size} strings took ${elapsedMs}ms (expected < 3000ms)",
            elapsedMs < 3000
        )
    }

    @Test
    fun `redaction of large text block completes within acceptable time`() {
        val largeText = buildString {
            repeat(50) {
                append("Session log: VIN 1HGBH41JXMN109186 analyzed. ")
                append("Contact: user${it}@company.com, phone: 555-${String.format("%03d", it)}-4567. ")
                append("Diagnostic result: P0301 misfire detected. Parts ordered. ")
            }
        }

        // Warm up
        redactor.redact(largeText)

        val startTime = System.nanoTime()
        repeat(1_000) {
            redactor.redact(largeText)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "1k redactions of large text took ${elapsedMs}ms (expected < 15000ms)",
            elapsedMs < 15000
        )
    }

    @Test
    fun `average redaction latency under 200 microseconds for clean text`() {
        val cleanText = "Brake pads replaced on the front axle. No issues found during inspection."
        val iterations = 50_000

        repeat(100) { redactor.redact(cleanText) }

        val startTime = System.nanoTime()
        repeat(iterations) { redactor.redact(cleanText) }
        val avgMicros = (System.nanoTime() - startTime) / 1_000 / iterations

        assertTrue(
            "Average clean text redaction latency was ${avgMicros}μs (expected < 200μs)",
            avgMicros < 200
        )
    }

    @Test
    fun `average redaction latency under 500 microseconds for PII-heavy text`() {
        val piiText = "VIN: 1HGBH41JXMN109186, email: a@b.com, phone: 555-111-2222, plate: ABC 1234"
        val iterations = 50_000

        repeat(100) { redactor.redact(piiText) }

        val startTime = System.nanoTime()
        repeat(iterations) { redactor.redact(piiText) }
        val avgMicros = (System.nanoTime() - startTime) / 1_000 / iterations

        assertTrue(
            "Average PII-heavy redaction latency was ${avgMicros}μs (expected < 500μs)",
            avgMicros < 500
        )
    }
}
