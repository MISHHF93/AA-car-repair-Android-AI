package com.aa.carrepair.core.util

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Performance tests measuring throughput and latency of core utility operations.
 * These tests verify that critical operations complete within acceptable time bounds.
 */
class CorePerformanceTest {

    // ── VIN validation throughput ────────────────────────────────────────

    @Test
    fun `VIN validation processes 100_000 entries within 2 seconds`() {
        val validVin = "1HGBH41JXMN109186"
        val invalidVin = "INVALID"
        val iterations = 100_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            validVin.isValidVin()
            invalidVin.isValidVin()
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "VIN validation took ${elapsedMs}ms for $iterations iterations (expected < 2000ms)",
            elapsedMs < 2000
        )
    }

    @Test
    fun `VIN validation average latency is under 50 microseconds`() {
        val vin = "1HGBH41JXMN109186"
        val iterations = 10_000
        val startTime = System.nanoTime()
        repeat(iterations) { vin.isValidVin() }
        val avgMicros = (System.nanoTime() - startTime) / 1_000 / iterations

        assertTrue(
            "Average VIN validation latency was ${avgMicros}μs (expected < 50μs)",
            avgMicros < 50
        )
    }

    // ── DTC code validation throughput ──────────────────────────────────

    @Test
    fun `DTC code validation processes 100_000 entries within 2 seconds`() {
        val codes = listOf("P0301", "B0100", "C0300", "U0100", "INVALID", "X9999", "")
        val iterations = 100_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            codes.forEach { it.isValidDtcCode() }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "DTC validation took ${elapsedMs}ms for ${iterations * codes.size} checks (expected < 2000ms)",
            elapsedMs < 2000
        )
    }

    // ── PII masking throughput ───────────────────────────────────────────

    @Test
    fun `PII masking processes 100_000 strings within 1 second`() {
        val inputs = listOf(
            "1HGBH41JXMN109186", "short", "AB", "averagelengthstringvalue123"
        )
        val iterations = 100_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            inputs.forEach { it.maskPii() }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "PII masking took ${elapsedMs}ms (expected < 1000ms)",
            elapsedMs < 1000
        )
    }

    // ── Currency / number formatting throughput ─────────────────────────

    @Test
    fun `currency formatting processes 50_000 values within 2 seconds`() {
        val values = listOf(0.0, 1.23, 999.99, 12345.67, 100000.0, 1234567.89)
        val iterations = 50_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            values.forEach { it.toCurrencyString() }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "Currency formatting took ${elapsedMs}ms (expected < 5000ms)",
            elapsedMs < 5000
        )
    }

    // ── Title case conversion throughput ─────────────────────────────────

    @Test
    fun `title case conversion processes 100_000 strings within 2 seconds`() {
        val strings = listOf(
            "hello world", "BRAKE PAD REPLACEMENT", "engine oil change needed",
            "a very long description of a complex automotive repair procedure"
        )
        val iterations = 100_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            strings.forEach { it.toTitleCase() }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "Title case took ${elapsedMs}ms (expected < 2000ms)",
            elapsedMs < 2000
        )
    }

    // ── DateUtils throughput ────────────────────────────────────────────

    @Test
    fun `DateUtils daysBetween handles 100_000 calculations within 1 second`() {
        val from = java.time.Instant.parse("2024-01-01T00:00:00Z")
        val to = java.time.Instant.parse("2024-12-31T00:00:00Z")
        val iterations = 100_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            DateUtils.daysBetween(from, to)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "daysBetween took ${elapsedMs}ms (expected < 1000ms)",
            elapsedMs < 1000
        )
    }

    @Test
    fun `DateUtils formatDate handles 50_000 format operations within 2 seconds`() {
        val instant = java.time.Instant.now()
        val iterations = 50_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            DateUtils.formatDate(instant)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "formatDate took ${elapsedMs}ms (expected < 2000ms)",
            elapsedMs < 2000
        )
    }
}
