package com.aa.carrepair.domain.usecase.vehicle

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.VehicleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveVehicleUseCaseTest {

    private val vehicleRepository: VehicleRepository = mockk(relaxed = true)
    private lateinit var useCase: SaveVehicleUseCase

    private val testVehicle = Vehicle(
        id = "v1",
        vin = "1HGBH41JXMN109186",
        year = 2021,
        make = "Honda",
        model = "Civic",
        mileage = 30000
    )

    @Before
    fun setUp() {
        useCase = SaveVehicleUseCase(vehicleRepository)
    }

    @Test
    fun `delegates to repository`() = runTest {
        coEvery { vehicleRepository.saveVehicle(testVehicle) } returns DataResult.Success(Unit)

        val result = useCase(testVehicle)

        assertTrue(result is DataResult.Success)
        coVerify { vehicleRepository.saveVehicle(testVehicle) }
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { vehicleRepository.saveVehicle(any()) } returns
            DataResult.Error(RuntimeException("DB error"))

        val result = useCase(testVehicle)
        assertTrue(result is DataResult.Error)
    }
}
