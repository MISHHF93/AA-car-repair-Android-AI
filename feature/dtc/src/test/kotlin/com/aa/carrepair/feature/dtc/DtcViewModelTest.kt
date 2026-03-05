package com.aa.carrepair.feature.dtc

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.DtcCode
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.usecase.dtc.AnalyzeDtcUseCase
import com.aa.carrepair.domain.usecase.dtc.SearchDtcUseCase
import io.mockk.coEvery
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DtcViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val analyzeDtcUseCase: AnalyzeDtcUseCase = mockk()
    private val searchDtcUseCase: SearchDtcUseCase = mockk()

    private lateinit var viewModel: DtcViewModel

    private val sampleDtcCode = DtcCode(
        code = "P0301",
        definition = "Cylinder 1 Misfire Detected",
        system = "Powertrain",
        causes = emptyList(),
        symptoms = listOf("Rough idle", "Loss of power"),
        repairProcedures = listOf("Check spark plugs"),
        safetyLevel = SafetyLevel.MEDIUM,
        confidenceScore = 85
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { searchDtcUseCase(any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = DtcViewModel(analyzeDtcUseCase, searchDtcUseCase)

    // --- onSearchQueryChanged ---

    @Test
    fun `onSearchQueryChanged updates searchQuery in state`() {
        viewModel = createViewModel()
        viewModel.onSearchQueryChanged("P0301")
        assertEquals("P0301", viewModel.uiState.value.searchQuery)
    }

    // --- analyzeCode ---

    @Test
    fun `analyzeCode with blank code does nothing`() = runTest {
        viewModel = createViewModel()
        viewModel.analyzeCode("  ")
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.selectedCode)
    }

    @Test
    fun `analyzeCode success sets selectedCode`() = runTest {
        coEvery { analyzeDtcUseCase("P0301") } returns DataResult.Success(sampleDtcCode)

        viewModel = createViewModel()
        viewModel.analyzeCode("P0301")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(sampleDtcCode, viewModel.uiState.value.selectedCode)
    }

    @Test
    fun `analyzeCode error sets error message`() = runTest {
        coEvery { analyzeDtcUseCase("P9999") } returns
            DataResult.Error(RuntimeException("Not found"))

        viewModel = createViewModel()
        viewModel.analyzeCode("P9999")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Could not analyze code: P9999", viewModel.uiState.value.error)
    }

    @Test
    fun `analyzeCode sets isLoading true while processing`() = runTest {
        coEvery { analyzeDtcUseCase(any()) } returns DataResult.Success(sampleDtcCode)

        viewModel = createViewModel()
        viewModel.analyzeCode("P0301")

        // Before dispatcher processes, isLoading should be true
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // --- initWithCode ---

    @Test
    fun `initWithCode sets query and analyzes`() = runTest {
        coEvery { analyzeDtcUseCase("P0420") } returns DataResult.Success(sampleDtcCode)

        viewModel = createViewModel()
        viewModel.initWithCode("P0420")
        advanceUntilIdle()

        assertEquals("P0420", viewModel.uiState.value.searchQuery)
        assertEquals(sampleDtcCode, viewModel.uiState.value.selectedCode)
    }

    @Test
    fun `initWithCode with blank does nothing`() = runTest {
        viewModel = createViewModel()
        viewModel.initWithCode("")
        advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertNull(viewModel.uiState.value.selectedCode)
    }

    // --- clearError ---

    @Test
    fun `clearError resets error`() = runTest {
        coEvery { analyzeDtcUseCase("P0000") } returns
            DataResult.Error(RuntimeException("fail"))

        viewModel = createViewModel()
        viewModel.analyzeCode("P0000")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }
}
