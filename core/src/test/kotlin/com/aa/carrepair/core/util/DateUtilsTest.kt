package com.aa.carrepair.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class DateUtilsTest {

    @Test
    fun `formatDate returns correct format`() {
        // Use a fixed instant: 2024-06-15T12:00:00Z
        val instant = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        val formatted = DateUtils.formatDate(instant)
        assertEquals("Jun 15, 2024", formatted)
    }

    @Test
    fun `formatDateTime includes time`() {
        val instant = ZonedDateTime.of(2024, 6, 15, 14, 30, 0, 0, ZoneId.systemDefault()).toInstant()
        val formatted = DateUtils.formatDateTime(instant)
        assertEquals("Jun 15, 2024 14:30", formatted)
    }

    @Test
    fun `formatShortDate returns MM dd yy format`() {
        val instant = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.systemDefault()).toInstant()
        val formatted = DateUtils.formatShortDate(instant)
        assertEquals("06/15/24", formatted)
    }

    @Test
    fun `formatTime returns HH mm format`() {
        val instant = ZonedDateTime.of(2024, 6, 15, 9, 5, 0, 0, ZoneId.systemDefault()).toInstant()
        val formatted = DateUtils.formatTime(instant)
        assertEquals("09:05", formatted)
    }

    @Test
    fun `daysBetween same day returns zero`() {
        val instant = Instant.now()
        assertEquals(0, DateUtils.daysBetween(instant, instant))
    }

    @Test
    fun `daysBetween counts correctly`() {
        val from = Instant.parse("2024-01-01T00:00:00Z")
        val to = Instant.parse("2024-01-11T00:00:00Z")
        assertEquals(10, DateUtils.daysBetween(from, to))
    }

    @Test
    fun `daysBetween negative when reversed`() {
        val from = Instant.parse("2024-01-11T00:00:00Z")
        val to = Instant.parse("2024-01-01T00:00:00Z")
        assertEquals(-10, DateUtils.daysBetween(from, to))
    }

    @Test
    fun `isOverdue returns true for past date`() {
        val pastDate = Instant.now().minusSeconds(86400) // 1 day ago
        assertTrue(DateUtils.isOverdue(pastDate))
    }

    @Test
    fun `isOverdue returns false for future date`() {
        val futureDate = Instant.now().plusSeconds(86400) // 1 day from now
        assertFalse(DateUtils.isOverdue(futureDate))
    }

    @Test
    fun `daysUntil returns positive for future date`() {
        val futureDate = Instant.now().plusSeconds(86400 * 5) // 5 days
        assertTrue(DateUtils.daysUntil(futureDate) >= 4)
    }

    @Test
    fun `daysUntil returns negative for past date`() {
        val pastDate = Instant.now().minusSeconds(86400 * 5)
        assertTrue(DateUtils.daysUntil(pastDate) < 0)
    }

    @Test
    fun `monthsAgo returns instant in the past`() {
        val threeMonthsAgo = DateUtils.monthsAgo(3)
        assertTrue(threeMonthsAgo.isBefore(Instant.now()))
    }

    @Test
    fun `currentYear returns a reasonable year`() {
        val year = DateUtils.currentYear()
        assertTrue(year >= 2024)
        assertTrue(year <= 2030)
    }
}
