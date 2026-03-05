package com.aa.carrepair.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PredictiveInsightsTest {

    private lateinit var insights: PredictiveInsights

    @Before
    fun setUp() {
        insights = PredictiveInsights()
    }

    // --- predictMaintenanceCost ---

    @Test
    fun `predictMaintenanceCost returns positive values`() {
        val result = insights.predictMaintenanceCost(
            vehicleAge = 5,
            mileage = 50000,
            historicalCosts = listOf(500.0, 600.0, 700.0)
        )
        assertTrue(result.predictedValue > 0)
        assertTrue(result.lowerBound > 0)
        assertTrue(result.upperBound > 0)
    }

    @Test
    fun `lowerBound is less than predictedValue and upperBound`() {
        val result = insights.predictMaintenanceCost(3, 30000, listOf(400.0, 450.0))
        assertTrue(result.lowerBound < result.predictedValue)
        assertTrue(result.predictedValue < result.upperBound)
    }

    @Test
    fun `upperBound is 2x lowerBound`() {
        val result = insights.predictMaintenanceCost(3, 30000, listOf(400.0))
        assertEquals(result.lowerBound * 2.0, result.upperBound, 0.01)
    }

    @Test
    fun `empty historical costs uses zero average`() {
        val result = insights.predictMaintenanceCost(1, 10000, emptyList())
        assertTrue(result.predictedValue > 0)
        assertEquals("stable", result.trend)
    }

    @Test
    fun `increasing costs show increasing trend`() {
        val costs = listOf(100.0, 200.0, 300.0, 400.0, 500.0, 600.0)
        val result = insights.predictMaintenanceCost(5, 50000, costs)
        assertEquals("increasing", result.trend)
    }

    @Test
    fun `decreasing costs show decreasing trend`() {
        val costs = listOf(600.0, 500.0, 400.0, 300.0, 200.0, 100.0)
        val result = insights.predictMaintenanceCost(5, 50000, costs)
        assertEquals("decreasing", result.trend)
    }

    @Test
    fun `confidence with many data points is high`() {
        val costs = (1..10).map { it * 100.0 }
        val result = insights.predictMaintenanceCost(5, 50000, costs)
        assertEquals(0.9, result.confidence, 0.01)
    }

    @Test
    fun `confidence with few data points is low`() {
        val result = insights.predictMaintenanceCost(1, 10000, listOf(100.0))
        assertEquals(0.4, result.confidence, 0.01)
    }

    @Test
    fun `confidence with 5 data points is medium`() {
        val result = insights.predictMaintenanceCost(3, 30000, (1..5).map { it * 100.0 })
        assertEquals(0.75, result.confidence, 0.01)
    }

    @Test
    fun `confidence with 3 data points is 0_6`() {
        val result = insights.predictMaintenanceCost(2, 20000, listOf(100.0, 200.0, 300.0))
        assertEquals(0.6, result.confidence, 0.01)
    }

    // --- identifyMaintenancePattern ---

    @Test
    fun `identifyMaintenancePattern with single date returns defaults`() {
        val pattern = insights.identifyMaintenancePattern(listOf(1000000L), listOf(5000))
        assertEquals(90, pattern.avgIntervalDays)
        assertEquals(5000, pattern.avgIntervalMiles)
        assertFalse(pattern.isRegular)
    }

    @Test
    fun `identifyMaintenancePattern with empty lists returns defaults`() {
        val pattern = insights.identifyMaintenancePattern(emptyList(), emptyList())
        assertEquals(90, pattern.avgIntervalDays)
        assertFalse(pattern.isRegular)
    }

    @Test
    fun `identifyMaintenancePattern with regular intervals is regular`() {
        val dayMs = 86400000L
        val dates = listOf(0L, 90 * dayMs, 180 * dayMs, 270 * dayMs)
        val mileages = listOf(0, 5000, 10000, 15000)

        val pattern = insights.identifyMaintenancePattern(dates, mileages)
        assertEquals(90, pattern.avgIntervalDays)
        assertEquals(5000, pattern.avgIntervalMiles)
        assertTrue(pattern.isRegular)
        assertEquals(90, pattern.nextDueDays)
    }

    @Test
    fun `identifyMaintenancePattern with irregular intervals is not regular`() {
        val dayMs = 86400000L
        val dates = listOf(0L, 30 * dayMs, 200 * dayMs, 210 * dayMs)
        val mileages = listOf(0, 3000, 15000, 16000)

        val pattern = insights.identifyMaintenancePattern(dates, mileages)
        assertFalse(pattern.isRegular)
    }

    @Test
    fun `identifyMaintenancePattern calculates average mileage intervals`() {
        val dayMs = 86400000L
        val dates = listOf(0L, 60 * dayMs, 120 * dayMs)
        val mileages = listOf(0, 4000, 8000)

        val pattern = insights.identifyMaintenancePattern(dates, mileages)
        assertEquals(4000, pattern.avgIntervalMiles)
    }
}
