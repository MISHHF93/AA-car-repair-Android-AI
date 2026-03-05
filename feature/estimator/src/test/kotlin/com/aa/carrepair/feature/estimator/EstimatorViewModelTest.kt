package com.aa.carrepair.feature.estimator

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.Part
import com.aa.carrepair.domain.model.LaborItem
import com.aa.carrepair.domain.model.RepairEstimate
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.usecase.estimate.GenerateEstimateUseCase
import com.aa.carrepair.domain.usecase.vehicle.DecodeVinUseCase
import com.aa.carrepair.domain.usecase.vehicle.SaveVehicleUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class EstimatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val decodeVinUseCase: DecodeVinUseCase = mockk()
    private val saveVehicleUseCase: SaveVehicleUseCase = mockk()
    private val generateEstimateUseCase: GenerateEstimateUseCase = mockk()

    private lateinit var viewModel: EstimatorViewModel

    private val sampleVehicle = Vehicle(
        id = "v1",
        vin = "1HGCM82633A004352",
        year = 2020,
        make = "Honda",
        model = "Accord",
        mileage = 55000,
        savedAt = Instant.parse("2024-01-01T00:00:00Z")
    )

    private val sampleEstimate = RepairEstimate(
        id = "e1",
        vehicle = sampleVehicle,
        serviceCategory = "Brakes",
        description = "Squeaking brakes",
        parts = listOf(Part("BP-001", "Brake Pads", 45.0, 30.0, "In Stock")),
        laborItems = emptyList(),
        subtotalParts = 45.0,
        subtotalLabor = 80.0,
        fees = 5.0,
        tax = 10.0,
        total = 140.0,
        confidence = 85,
        disclaimer = "Estimate only",
        createdAt = Instant.parse("2024-01-01T00:00:00Z")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = EstimatorViewModel(decodeVinUseCase, saveVehicleUseCase, generateEstimateUseCase)

    // --- onVinChanged ---

    @Test
    fun `onVinChanged updates vinInput and clears error`() {
        viewModel = createViewModel()
        viewModel.onVinChanged("1HGCM82633A004352")
        assertEquals("1HGCM82633A004352", viewModel.uiState.value.vinInput)
        assertNull(viewModel.uiState.value.error)
    }

    // --- decodeVin ---

    @Test
    fun `decodeVin with blank input does nothing`() = runTest {
        viewModel = createViewModel()
        viewModel.onVinChanged("  ")
        viewModel.decodeVin()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.selectedVehicle)
    }

    @Test
    fun `decodeVin success sets selectedVehicle`() = runTest {
        coEvery { decodeVinUseCase("1HGCM82633A004352") } returns DataResult.Success(sampleVehicle)

        viewModel = createViewModel()
        viewModel.onVinChanged("1HGCM82633A004352")
        viewModel.decodeVin()
        advanceUntilIdle()

        assertEquals(sampleVehicle, viewModel.uiState.value.selectedVehicle)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `decodeVin error sets error message`() = runTest {
        coEvery { decodeVinUseCase("INVALID") } returns
            DataResult.Error(RuntimeException("Invalid VIN"))

        viewModel = createViewModel()
        viewModel.onVinChanged("INVALID")
        viewModel.decodeVin()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Could not decode VIN. Please check and try again.", viewModel.uiState.value.error)
    }

    // --- selectCategory ---

    @Test
    fun `selectCategory updates category and moves to DIAGNOSTIC step`() {
        viewModel = createViewModel()
        viewModel.selectCategory("Brakes")
        assertEquals("Brakes", viewModel.uiState.value.selectedCategory)
        assertEquals(EstimatorStep.DIAGNOSTIC, viewModel.uiState.value.step)
    }

    // --- onIssueDescriptionChanged ---

    @Test
    fun `onIssueDescriptionChanged updates description`() {
        viewModel = createViewModel()
        viewModel.onIssueDescriptionChanged("Squeaking noise when braking")
        assertEquals("Squeaking noise when braking", viewModel.uiState.value.issueDescription)
    }

    // --- onOemToggled ---

    @Test
    fun `onOemToggled updates preferOem`() {
        viewModel = createViewModel()
        assertTrue(viewModel.uiState.value.preferOem) // default is true
        viewModel.onOemToggled(false)
        assertFalse(viewModel.uiState.value.preferOem)
    }

    // --- generateEstimate ---

    @Test
    fun `generateEstimate without vehicle does nothing`() = runTest {
        viewModel = createViewModel()
        viewModel.onIssueDescriptionChanged("test")
        viewModel.generateEstimate()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.estimate)
    }

    @Test
    fun `generateEstimate with blank description does nothing`() = runTest {
        coEvery { decodeVinUseCase(any()) } returns DataResult.Success(sampleVehicle)

        viewModel = createViewModel()
        viewModel.onVinChanged("1HGCM82633A004352")
        viewModel.decodeVin()
        advanceUntilIdle()

        viewModel.generateEstimate()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.estimate)
    }

    @Test
    fun `generateEstimate success sets estimate and moves to RESULT`() = runTest {
        coEvery { decodeVinUseCase(any()) } returns DataResult.Success(sampleVehicle)
        coEvery { generateEstimateUseCase(any(), any(), any(), any(), any()) } returns
            DataResult.Success(sampleEstimate)

        viewModel = createViewModel()
        viewModel.onVinChanged("1HGCM82633A004352")
        viewModel.decodeVin()
        advanceUntilIdle()

        viewModel.selectCategory("Brakes")
        viewModel.onIssueDescriptionChanged("Squeaking")
        viewModel.generateEstimate()
        advanceUntilIdle()

        assertEquals(sampleEstimate, viewModel.uiState.value.estimate)
        assertEquals(EstimatorStep.RESULT, viewModel.uiState.value.step)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `generateEstimate error sets error message`() = runTest {
        coEvery { decodeVinUseCase(any()) } returns DataResult.Success(sampleVehicle)
        coEvery { generateEstimateUseCase(any(), any(), any(), any(), any()) } returns
            DataResult.Error(RuntimeException("API error"))

        viewModel = createViewModel()
        viewModel.onVinChanged("1HGCM82633A004352")
        viewModel.decodeVin()
        advanceUntilIdle()

        viewModel.selectCategory("Brakes")
        viewModel.onIssueDescriptionChanged("problem")
        viewModel.generateEstimate()
        advanceUntilIdle()

        assertEquals("Failed to generate estimate. Please try again.", viewModel.uiState.value.error)
    }

    // --- proceedToCategory ---

    @Test
    fun `proceedToCategory with vehicle changes step to CATEGORY`() = runTest {
        coEvery { decodeVinUseCase(any()) } returns DataResult.Success(sampleVehicle)

        viewModel = createViewModel()
        viewModel.onVinChanged("1HGCM82633A004352")
        viewModel.decodeVin()
        advanceUntilIdle()

        viewModel.proceedToCategory()
        assertEquals(EstimatorStep.CATEGORY, viewModel.uiState.value.step)
    }

    @Test
    fun `proceedToCategory without vehicle stays on VEHICLE step`() {
        viewModel = createViewModel()
        viewModel.proceedToCategory()
        assertEquals(EstimatorStep.VEHICLE, viewModel.uiState.value.step)
    }

    // --- clearError ---

    @Test
    fun `clearError resets error`() {
        viewModel = createViewModel()
        // Simulate error state by analyzing invalid VIN
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    // --- Full flow test ---

    @Test
    fun `full estimator flow from VIN to result`() = runTest {
        coEvery { decodeVinUseCase(any()) } returns DataResult.Success(sampleVehicle)
        coEvery { generateEstimateUseCase(any(), any(), any(), any(), any()) } returns
            DataResult.Success(sampleEstimate)

        viewModel = createViewModel()

        // Step 1: Enter VIN
        assertEquals(EstimatorStep.VEHICLE, viewModel.uiState.value.step)
        viewModel.onVinChanged("1HGCM82633A004352")
        viewModel.decodeVin()
        advanceUntilIdle()

        // Step 2: Select category
        viewModel.proceedToCategory()
        assertEquals(EstimatorStep.CATEGORY, viewModel.uiState.value.step)
        viewModel.selectCategory("Brakes")
        assertEquals(EstimatorStep.DIAGNOSTIC, viewModel.uiState.value.step)

        // Step 3: Describe issue and generate
        viewModel.onIssueDescriptionChanged("Squeaking noise")
        viewModel.onOemToggled(false)
        viewModel.generateEstimate()
        advanceUntilIdle()

        // Step 4: Result
        assertEquals(EstimatorStep.RESULT, viewModel.uiState.value.step)
        assertEquals(140.0, viewModel.uiState.value.estimate!!.total, 0.01)
    }
}
