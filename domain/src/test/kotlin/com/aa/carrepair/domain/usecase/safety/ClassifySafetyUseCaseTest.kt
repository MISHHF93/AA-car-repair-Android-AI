package com.aa.carrepair.domain.usecase.safety

import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.safety.SafetyClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClassifySafetyUseCaseTest {

    private lateinit var useCase: ClassifySafetyUseCase

    @Before
    fun setUp() {
        useCase = ClassifySafetyUseCase(SafetyClassifier())
    }

    @Test
    fun `delegates to classifier for critical issue`() {
        val result = useCase("brake failure detected")
        assertEquals(SafetyLevel.CRITICAL, result.level)
        assertFalse(result.isDrivable)
    }

    @Test
    fun `delegates to classifier for high issue`() {
        val result = useCase("abs fault")
        assertEquals(SafetyLevel.HIGH, result.level)
        assertTrue(result.isDrivable)
    }

    @Test
    fun `delegates to classifier for medium issue`() {
        val result = useCase("check engine light")
        assertEquals(SafetyLevel.MEDIUM, result.level)
    }

    @Test
    fun `delegates to classifier for low issue`() {
        val result = useCase("windshield wipers need replacing")
        assertEquals(SafetyLevel.LOW, result.level)
    }

    @Test
    fun `passes DTC codes through to classifier`() {
        val result = useCase("engine issue", listOf("P0301"))
        assertEquals(SafetyLevel.CRITICAL, result.level)
    }

    @Test
    fun `empty content returns low safety`() {
        val result = useCase("")
        assertEquals(SafetyLevel.LOW, result.level)
    }

    @Test
    fun `result includes non-empty recommended action`() {
        val result = useCase("brake failure")
        assertTrue(result.recommendedAction.isNotBlank())
    }
}
