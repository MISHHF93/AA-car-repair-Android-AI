package com.aa.carrepair.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionsTest {

    // ── VIN validation ──────────────────────────────────────────────────

    @Test
    fun `valid 17 character VIN passes validation`() {
        assertTrue("1HGBH41JXMN109186".isValidVin())
    }

    @Test
    fun `valid lowercase VIN passes validation`() {
        assertTrue("1hgbh41jxmn109186".isValidVin())
    }

    @Test
    fun `VIN shorter than 17 characters fails`() {
        assertFalse("1HGBH41JX".isValidVin())
    }

    @Test
    fun `VIN longer than 17 characters fails`() {
        assertFalse("1HGBH41JXMN109186A".isValidVin())
    }

    @Test
    fun `empty string fails VIN validation`() {
        assertFalse("".isValidVin())
    }

    @Test
    fun `VIN containing I is invalid`() {
        assertFalse("1HGBH41IXMN109186".isValidVin())
    }

    @Test
    fun `VIN containing O is invalid`() {
        assertFalse("1HGBH41OXMN109186".isValidVin())
    }

    @Test
    fun `VIN containing Q is invalid`() {
        assertFalse("1HGBH41QXMN109186".isValidVin())
    }

    @Test
    fun `VIN with special characters fails`() {
        assertFalse("1HGBH41JX@N109186".isValidVin())
    }

    // ── DTC code validation ─────────────────────────────────────────────

    @Test
    fun `valid powertrain DTC code passes`() {
        assertTrue("P0301".isValidDtcCode())
    }

    @Test
    fun `valid body DTC code passes`() {
        assertTrue("B0100".isValidDtcCode())
    }

    @Test
    fun `valid chassis DTC code passes`() {
        assertTrue("C0300".isValidDtcCode())
    }

    @Test
    fun `valid network DTC code passes`() {
        assertTrue("U0100".isValidDtcCode())
    }

    @Test
    fun `lowercase DTC code passes`() {
        assertTrue("p0301".isValidDtcCode())
    }

    @Test
    fun `invalid DTC code prefix fails`() {
        assertFalse("X0301".isValidDtcCode())
    }

    @Test
    fun `DTC code with wrong length fails`() {
        assertFalse("P03".isValidDtcCode())
    }

    @Test
    fun `DTC code with letters after prefix fails`() {
        assertFalse("PABCD".isValidDtcCode())
    }

    @Test
    fun `empty DTC code fails`() {
        assertFalse("".isValidDtcCode())
    }

    // ── maskPii ─────────────────────────────────────────────────────────

    @Test
    fun `maskPii masks all but last 4 characters`() {
        assertEquals("****9186", "1HGBH41JXMN109186".maskPii())
    }

    @Test
    fun `maskPii on short string returns all asterisks`() {
        assertEquals("****", "AB".maskPii())
    }

    @Test
    fun `maskPii on exactly 4 chars returns all asterisks`() {
        assertEquals("****", "ABCD".maskPii())
    }

    @Test
    fun `maskPii on 5 chars masks first, keeps last 4`() {
        assertEquals("****BCDE", "ABCDE".maskPii())
    }

    // ── toTitleCase ─────────────────────────────────────────────────────

    @Test
    fun `toTitleCase capitalises first letter of each word`() {
        assertEquals("Hello World", "hello world".toTitleCase())
    }

    @Test
    fun `toTitleCase handles all caps`() {
        assertEquals("Hello World", "HELLO WORLD".toTitleCase())
    }

    @Test
    fun `toTitleCase handles single word`() {
        assertEquals("Brake", "brake".toTitleCase())
    }

    @Test
    fun `toTitleCase handles empty string`() {
        assertEquals("", "".toTitleCase())
    }

    // ── toCurrencyString ────────────────────────────────────────────────

    @Test
    fun `toCurrencyString formats correctly`() {
        assertEquals("$1,234.56", 1234.56.toCurrencyString())
    }

    @Test
    fun `toCurrencyString formats zero`() {
        assertEquals("$0.00", 0.0.toCurrencyString())
    }

    @Test
    fun `toCurrencyString formats large number`() {
        assertEquals("$100,000.00", 100000.0.toCurrencyString())
    }

    // ── toPercentString ─────────────────────────────────────────────────

    @Test
    fun `toPercentString formats correctly`() {
        assertEquals("85.0%", 0.85.toPercentString())
    }

    @Test
    fun `toPercentString formats zero`() {
        assertEquals("0.0%", 0.0.toPercentString())
    }

    // ── toMileageString ─────────────────────────────────────────────────

    @Test
    fun `toMileageString formats imperial`() {
        assertEquals("50,000 miles", 50000.toMileageString())
    }

    @Test
    fun `toMileageString formats metric`() {
        assertEquals("80,000 km", 80000.toMileageString(metric = true))
    }
}
