package com.aa.carrepair.domain.safety

import com.aa.carrepair.domain.model.SafetyLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SafetyClassifierTest {

    private lateinit var classifier: SafetyClassifier

    @Before
    fun setUp() {
        classifier = SafetyClassifier()
    }

    @Test
    fun `classifies critical safety issue when brake failure mentioned`() {
        val result = classifier.classify("My car has brake failure and no brakes")
        assertEquals(SafetyLevel.CRITICAL, result.level)
        assertFalse(result.isDrivable)
    }

    @Test
    fun `classifies high safety issue when abs fault mentioned`() {
        val result = classifier.classify("ABS fault warning light is on")
        assertEquals(SafetyLevel.HIGH, result.level)
        assertTrue(result.isDrivable)
    }

    @Test
    fun `classifies medium safety issue for check engine`() {
        val result = classifier.classify("Check engine light is on")
        assertEquals(SafetyLevel.MEDIUM, result.level)
        assertTrue(result.isDrivable)
    }

    @Test
    fun `classifies low safety for unrelated content`() {
        val result = classifier.classify("I need to replace my windshield wipers")
        assertEquals(SafetyLevel.LOW, result.level)
        assertTrue(result.isDrivable)
    }

    @Test
    fun `detects critical DTC codes`() {
        val result = classifier.classify("Engine issue", listOf("P0301", "P0302"))
        assertEquals(SafetyLevel.CRITICAL, result.level)
    }

    @Test
    fun `critical classification has non-empty recommended action`() {
        val result = classifier.classify("brake failure detected")
        assertTrue(result.recommendedAction.isNotBlank())
    }

    @Test
    fun `critical is higher priority than high`() {
        val result = classifier.classify("brake failure and abs fault")
        assertEquals(SafetyLevel.CRITICAL, result.level)
    }
}
