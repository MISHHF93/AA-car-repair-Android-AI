package com.aa.carrepair.domain.usecase.estimate

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.RepairEstimate
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.EstimateRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GenerateEstimateUseCaseTest {

    private val estimateRepository: EstimateRepository = mockk(relaxed = true)
    private lateinit var useCase: GenerateEstimateUseCase

    private val vehicle = Vehicle(
        id = "v1", vin = "1HGBH41JXMN109186",
        year = 2021, make = "Honda", model = "Civic"
    )

    private val sampleEstimate = RepairEstimate(
        id = "e1",
        vehicle = vehicle,
        serviceCategory = "Brakes",
        description = "Front brake pad replacement",
        parts = emptyList(),
        laborItems = emptyList(),
        subtotalParts = 150.0,
        subtotalLabor = 200.0,
        fees = 10.0,
        tax = 28.8,
        total = 388.8,
        confidence = 90,
        disclaimer = "Estimate only"
    )

    @Before
    fun setUp() {
        useCase = GenerateEstimateUseCase(estimateRepository)
    }

    @Test
    fun `delegates all params to repository`() = runTest {
        coEvery {
            estimateRepository.generateEstimate("VIN1", "Brakes", "Pads worn", 50000, true)
        } returns DataResult.Success(sampleEstimate)

        val result = useCase("VIN1", "Brakes", "Pads worn", 50000, true)

        assertTrue(result is DataResult.Success)
        assertEquals(sampleEstimate, (result as DataResult.Success).data)
        coVerify { estimateRepository.generateEstimate("VIN1", "Brakes", "Pads worn", 50000, true) }
    }

    @Test
    fun `default preferOem is true`() = runTest {
        coEvery {
            estimateRepository.generateEstimate(any(), any(), any(), any(), eq(true))
        } returns DataResult.Success(sampleEstimate)

        useCase("VIN1", "Brakes", "Pads")

        coVerify { estimateRepository.generateEstimate("VIN1", "Brakes", "Pads", null, true) }
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery {
            estimateRepository.generateEstimate(any(), any(), any(), any(), any())
        } returns DataResult.Error(RuntimeException("Timeout"))

        val result = useCase("VIN1", "Brakes", "Pads")
        assertTrue(result is DataResult.Error)
    }
}
