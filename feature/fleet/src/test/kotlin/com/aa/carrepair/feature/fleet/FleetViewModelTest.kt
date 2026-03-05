package com.aa.carrepair.feature.fleet

import com.aa.carrepair.domain.model.FleetVehicle
import com.aa.carrepair.domain.model.FleetVehicleStatus
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.FleetRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class FleetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fleetRepository: FleetRepository = mockk()

    private fun makeVehicle(id: String, vin: String) = Vehicle(
        id = id, vin = vin, year = 2022, make = "Ford", model = "Transit",
        savedAt = Instant.parse("2024-01-01T00:00:00Z")
    )

    private fun makeFleetVehicle(
        id: String,
        vin: String,
        cost: Double,
        status: FleetVehicleStatus = FleetVehicleStatus.ACTIVE,
        nextMaintenance: Instant? = null
    ) = FleetVehicle(
        id = id,
        vehicle = makeVehicle(id, vin),
        fleetId = "fleet-1",
        totalCostYtd = cost,
        status = status,
        nextMaintenanceDue = nextMaintenance
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads fleet and calculates totals`() = runTest {
        val vehicles = listOf(
            makeFleetVehicle("1", "VIN1", 1000.0),
            makeFleetVehicle("2", "VIN2", 2000.0),
            makeFleetVehicle("3", "VIN3", 3000.0)
        )
        every { fleetRepository.getFleetVehicles() } returns flowOf(vehicles)

        val viewModel = FleetViewModel(fleetRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.vehicles.size)
        assertEquals(6000.0, state.totalCostYtd, 0.01)
        assertEquals(2000.0, state.avgCostPerVehicle, 0.01)
        assertFalse(state.isLoading)
    }

    @Test
    fun `empty fleet has zero totals`() = runTest {
        every { fleetRepository.getFleetVehicles() } returns flowOf(emptyList())

        val viewModel = FleetViewModel(fleetRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.vehicles.size)
        assertEquals(0.0, state.totalCostYtd, 0.01)
        assertEquals(0.0, state.avgCostPerVehicle, 0.01)
        assertEquals(0, state.maintenanceDueCount)
    }

    @Test
    fun `maintenanceDueCount includes overdue and IN_MAINTENANCE`() = runTest {
        val pastDate = Instant.now().minusSeconds(86400) // yesterday
        val futureDate = Instant.now().plusSeconds(86400 * 30) // 30 days from now
        val vehicles = listOf(
            makeFleetVehicle("1", "VIN1", 500.0, nextMaintenance = pastDate), // overdue
            makeFleetVehicle("2", "VIN2", 500.0, FleetVehicleStatus.IN_MAINTENANCE), // in maintenance
            makeFleetVehicle("3", "VIN3", 500.0, nextMaintenance = futureDate) // not due
        )
        every { fleetRepository.getFleetVehicles() } returns flowOf(vehicles)

        val viewModel = FleetViewModel(fleetRepository)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.maintenanceDueCount)
    }

    @Test
    fun `clearError resets error to null`() = runTest {
        every { fleetRepository.getFleetVehicles() } returns flowOf(emptyList())

        val viewModel = FleetViewModel(fleetRepository)
        advanceUntilIdle()

        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `single vehicle fleet has same total and average`() = runTest {
        val vehicles = listOf(makeFleetVehicle("1", "VIN1", 4500.0))
        every { fleetRepository.getFleetVehicles() } returns flowOf(vehicles)

        val viewModel = FleetViewModel(fleetRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(4500.0, state.totalCostYtd, 0.01)
        assertEquals(4500.0, state.avgCostPerVehicle, 0.01)
    }
}
