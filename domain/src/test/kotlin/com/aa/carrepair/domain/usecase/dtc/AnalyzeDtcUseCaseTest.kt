package com.aa.carrepair.domain.usecase.dtc

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.DtcCode
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.repository.DtcRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnalyzeDtcUseCaseTest {

    private val dtcRepository: DtcRepository = mockk(relaxed = true)
    private lateinit var useCase: AnalyzeDtcUseCase

    private val sampleDtcCode = DtcCode(
        code = "P0301",
        definition = "Cylinder 1 Misfire Detected",
        system = "Powertrain",
        causes = emptyList(),
        symptoms = listOf("Rough idle", "Loss of power"),
        repairProcedures = listOf("Check spark plug"),
        safetyLevel = SafetyLevel.HIGH,
        confidenceScore = 85
    )

    @Before
    fun setUp() {
        useCase = AnalyzeDtcUseCase(dtcRepository)
    }

    @Test
    fun `returns error for invalid DTC code format`() = runTest {
        val result = useCase("INVALID")
        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).exception is IllegalArgumentException)
    }

    @Test
    fun `returns error for empty code`() = runTest {
        val result = useCase("")
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `returns error for DTC code with wrong prefix`() = runTest {
        val result = useCase("X0301")
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `calls repository with uppercased valid DTC code`() = runTest {
        coEvery { dtcRepository.analyzeDtc("P0301", null) } returns DataResult.Success(sampleDtcCode)

        val result = useCase("p0301")

        assertTrue(result is DataResult.Success)
        assertEquals(sampleDtcCode, (result as DataResult.Success).data)
        coVerify { dtcRepository.analyzeDtc("P0301", null) }
    }

    @Test
    fun `passes vehicle VIN to repository`() = runTest {
        coEvery { dtcRepository.analyzeDtc("P0301", "VIN123") } returns DataResult.Success(sampleDtcCode)

        useCase("P0301", "VIN123")

        coVerify { dtcRepository.analyzeDtc("P0301", "VIN123") }
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { dtcRepository.analyzeDtc(any(), any()) } returns
            DataResult.Error(RuntimeException("API error"))

        val result = useCase("P0301")
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `does not call repository for invalid code`() = runTest {
        useCase("BADCODE")
        coVerify(exactly = 0) { dtcRepository.analyzeDtc(any(), any()) }
    }

    @Test
    fun `B-type DTC code is valid`() = runTest {
        coEvery { dtcRepository.analyzeDtc(any(), any()) } returns DataResult.Success(sampleDtcCode)
        val result = useCase("B0100")
        assertTrue(result is DataResult.Success)
    }

    @Test
    fun `C-type DTC code is valid`() = runTest {
        coEvery { dtcRepository.analyzeDtc(any(), any()) } returns DataResult.Success(sampleDtcCode)
        val result = useCase("C0300")
        assertTrue(result is DataResult.Success)
    }

    @Test
    fun `U-type DTC code is valid`() = runTest {
        coEvery { dtcRepository.analyzeDtc(any(), any()) } returns DataResult.Success(sampleDtcCode)
        val result = useCase("U0100")
        assertTrue(result is DataResult.Success)
    }
}
