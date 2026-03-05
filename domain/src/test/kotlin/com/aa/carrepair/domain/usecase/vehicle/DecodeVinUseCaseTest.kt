package com.aa.carrepair.domain.usecase.vehicle

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class DecodeVinUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk(relaxed = true)
    private lateinit var useCase: DecodeVinUseCase

    @Before
    fun setUp() {
        useCase = DecodeVinUseCase(vehicleRepository)
    }

    @Test
    fun `returns error for invalid VIN`() = runTest {
        val result = useCase("INVALID")
        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).exception is IllegalArgumentException)
    }

    @Test
    fun `returns error for VIN with invalid chars I O Q`() = runTest {
        val result = useCase("1HGBH41IXMN10918") // contains I
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `returns error for empty VIN`() = runTest {
        val result = useCase("")
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `calls repository with uppercased valid VIN`() = runTest {
        val vehicle = Vehicle(
            id = "v1", vin = "1HGBH41JXMN109186",
            year = 2021, make = "Honda", model = "Civic"
        )
        coEvery { vehicleRepository.decodeVin("1HGBH41JXMN109186") } returns DataResult.Success(vehicle)

        val result = useCase("1hgbh41jxmn109186")

        assertTrue(result is DataResult.Success)
        assertEquals(vehicle, (result as DataResult.Success).data)
        coVerify { vehicleRepository.decodeVin("1HGBH41JXMN109186") }
    }

    @Test
    fun `propagates repository error for valid VIN`() = runTest {
        coEvery { vehicleRepository.decodeVin(any()) } returns
            DataResult.Error(RuntimeException("Network error"))

        val result = useCase("1HGBH41JXMN109186")
        assertTrue(result is DataResult.Error)
    }

    @Test
    fun `does not call repository for short VIN`() = runTest {
        useCase("1HG")
        coVerify(exactly = 0) { vehicleRepository.decodeVin(any()) }
    }
}
