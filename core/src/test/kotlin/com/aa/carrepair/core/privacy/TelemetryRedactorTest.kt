package com.aa.carrepair.core.privacy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TelemetryRedactorTest {

    private lateinit var redactor: TelemetryRedactor

    @Before
    fun setUp() {
        redactor = TelemetryRedactor()
    }

    // ── VIN redaction ───────────────────────────────────────────────────

    @Test
    fun `redacts VIN from text`() {
        val input = "Vehicle VIN is 1HGBH41JXMN109186 needs service"
        val result = redactor.redact(input)
        assertTrue(result.contains("[VIN_REDACTED]"))
        assertFalse(result.contains("1HGBH41JXMN109186"))
    }

    @Test
    fun `redacts multiple VINs`() {
        val input = "Compare 1HGBH41JXMN109186 with 2T1BURHE5JC034461"
        val result = redactor.redact(input)
        assertFalse(result.contains("1HGBH41JXMN109186"))
        assertFalse(result.contains("2T1BURHE5JC034461"))
    }

    // ── Email redaction ─────────────────────────────────────────────────

    @Test
    fun `redacts email address`() {
        val input = "Contact user@example.com for details"
        val result = redactor.redact(input)
        assertTrue(result.contains("[EMAIL_REDACTED]"))
        assertFalse(result.contains("user@example.com"))
    }

    @Test
    fun `redacts complex email`() {
        val input = "Send to first.last+tag@company.co.uk"
        val result = redactor.redact(input)
        assertFalse(result.contains("first.last"))
    }

    // ── Phone redaction ─────────────────────────────────────────────────

    @Test
    fun `redacts US phone number`() {
        val input = "Call 555-123-4567 for service"
        val result = redactor.redact(input)
        assertTrue(result.contains("[PHONE_REDACTED]"))
        assertFalse(result.contains("555-123-4567"))
    }

    @Test
    fun `redacts phone with country code`() {
        val input = "Call +1-555-123-4567"
        val result = redactor.redact(input)
        assertFalse(result.contains("555-123-4567"))
    }

    @Test
    fun `redacts phone with parentheses`() {
        val input = "Call (555)123-4567"
        val result = redactor.redact(input)
        assertFalse(result.contains("123-4567"))
    }

    // ── Combined redaction ──────────────────────────────────────────────

    @Test
    fun `redacts all PII types in one string`() {
        val input = "VIN: 1HGBH41JXMN109186, email: a@b.com, phone: 555-111-2222"
        val result = redactor.redact(input)
        assertFalse(result.contains("1HGBH41JXMN109186"))
        assertFalse(result.contains("a@b.com"))
        assertFalse(result.contains("555-111-2222"))
    }

    @Test
    fun `leaves non-PII text untouched`() {
        val input = "Replace brake pads on the vehicle"
        val result = redactor.redact(input)
        assertEquals("Replace brake pads on the vehicle", result)
    }

    @Test
    fun `empty string returns empty`() {
        assertEquals("", redactor.redact(""))
    }
}
