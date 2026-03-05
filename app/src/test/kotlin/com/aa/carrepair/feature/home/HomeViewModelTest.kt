package com.aa.carrepair.feature.home

import com.aa.carrepair.domain.model.UserPersona
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.VehicleRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val vehicleRepository: VehicleRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeVehicle(id: String) = Vehicle(
        id = id, vin = "VIN-$id", year = 2022, make = "Toyota", model = "Camry",
        savedAt = Instant.parse("2024-01-01T00:00:00Z")
    )

    @Test
    fun `init loads vehicle count`() = runTest {
        val vehicles = listOf(makeVehicle("1"), makeVehicle("2"), makeVehicle("3"))
        every { vehicleRepository.getVehicles() } returns flowOf(vehicles)

        val viewModel = HomeViewModel(vehicleRepository)
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.recentVehicleCount)
    }

    @Test
    fun `empty vehicles shows zero count`() = runTest {
        every { vehicleRepository.getVehicles() } returns flowOf(emptyList())

        val viewModel = HomeViewModel(vehicleRepository)
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.recentVehicleCount)
    }

    @Test
    fun `default persona is DIY_OWNER`() = runTest {
        every { vehicleRepository.getVehicles() } returns flowOf(emptyList())

        val viewModel = HomeViewModel(vehicleRepository)
        assertEquals(UserPersona.DIY_OWNER, viewModel.uiState.value.persona)
    }

    @Test
    fun `setPersona updates state`() = runTest {
        every { vehicleRepository.getVehicles() } returns flowOf(emptyList())

        val viewModel = HomeViewModel(vehicleRepository)
        viewModel.setPersona(UserPersona.FLEET_MANAGER)
        assertEquals(UserPersona.FLEET_MANAGER, viewModel.uiState.value.persona)
    }

    @Test
    fun `setPersona to PROFESSIONAL_TECHNICIAN`() = runTest {
        every { vehicleRepository.getVehicles() } returns flowOf(emptyList())

        val viewModel = HomeViewModel(vehicleRepository)
        viewModel.setPersona(UserPersona.PROFESSIONAL_TECHNICIAN)
        assertEquals(UserPersona.PROFESSIONAL_TECHNICIAN, viewModel.uiState.value.persona)
    }
}
