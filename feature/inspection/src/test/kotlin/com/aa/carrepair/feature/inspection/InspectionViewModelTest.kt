package com.aa.carrepair.feature.inspection

import android.net.Uri
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.FindingSeverity
import com.aa.carrepair.domain.model.InspectionFinding
import com.aa.carrepair.domain.model.InspectionMode
import com.aa.carrepair.domain.model.InspectionResult
import com.aa.carrepair.domain.usecase.inspection.AnalyzeImageUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class InspectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val analyzeImageUseCase: AnalyzeImageUseCase = mockk()

    private lateinit var viewModel: InspectionViewModel
    private val mockUri: Uri = mockk()

    private val sampleResult = InspectionResult(
        id = "insp1",
        mode = InspectionMode.DAMAGE_ASSESSMENT,
        findings = listOf(
            InspectionFinding(
                type = "Scratch",
                description = "Deep scratch on front bumper",
                severity = FindingSeverity.MEDIUM,
                confidence = 0.92
            )
        ),
        severityScore = 5.5,
        summary = "Minor damage detected on front bumper",
        recommendations = listOf("Repaint bumper"),
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

    private fun createViewModel() = InspectionViewModel(analyzeImageUseCase)

    // --- onModeSelected ---

    @Test
    fun `default mode is DAMAGE_ASSESSMENT`() {
        viewModel = createViewModel()
        assertEquals(InspectionMode.DAMAGE_ASSESSMENT, viewModel.uiState.value.selectedMode)
    }

    @Test
    fun `onModeSelected updates mode`() {
        viewModel = createViewModel()
        viewModel.onModeSelected(InspectionMode.PARTS_IDENTIFICATION)
        assertEquals(InspectionMode.PARTS_IDENTIFICATION, viewModel.uiState.value.selectedMode)
    }

    @Test
    fun `onModeSelected to WEAR_ANALYSIS`() {
        viewModel = createViewModel()
        viewModel.onModeSelected(InspectionMode.WEAR_ANALYSIS)
        assertEquals(InspectionMode.WEAR_ANALYSIS, viewModel.uiState.value.selectedMode)
    }

    // --- onImageCaptured ---

    @Test
    fun `onImageCaptured stores uri and starts analysis`() = runTest {
        coEvery { analyzeImageUseCase(mockUri, InspectionMode.DAMAGE_ASSESSMENT) } returns
            DataResult.Success(sampleResult)

        viewModel = createViewModel()
        viewModel.onImageCaptured(mockUri)
        advanceUntilIdle()

        assertEquals(mockUri, viewModel.uiState.value.capturedImageUri)
        assertNotNull(viewModel.uiState.value.result)
        assertFalse(viewModel.uiState.value.isAnalyzing)
    }

    @Test
    fun `onImageCaptured success sets result`() = runTest {
        coEvery { analyzeImageUseCase(mockUri, InspectionMode.DAMAGE_ASSESSMENT) } returns
            DataResult.Success(sampleResult)

        viewModel = createViewModel()
        viewModel.onImageCaptured(mockUri)
        advanceUntilIdle()

        val result = viewModel.uiState.value.result!!
        assertEquals(1, result.findings.size)
        assertEquals("Scratch", result.findings[0].type)
        assertEquals(5.5, result.severityScore, 0.01)
    }

    @Test
    fun `onImageCaptured error sets error message`() = runTest {
        coEvery { analyzeImageUseCase(mockUri, InspectionMode.DAMAGE_ASSESSMENT) } returns
            DataResult.Error(RuntimeException("ML model failed"))

        viewModel = createViewModel()
        viewModel.onImageCaptured(mockUri)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAnalyzing)
        assertEquals("Failed to analyze image. Please try again.", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun `analysis uses currently selected mode`() = runTest {
        coEvery { analyzeImageUseCase(mockUri, InspectionMode.WEAR_ANALYSIS) } returns
            DataResult.Success(sampleResult.copy(mode = InspectionMode.WEAR_ANALYSIS))

        viewModel = createViewModel()
        viewModel.onModeSelected(InspectionMode.WEAR_ANALYSIS)
        viewModel.onImageCaptured(mockUri)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.result)
    }

    // --- clearError ---

    @Test
    fun `clearError resets error`() = runTest {
        coEvery { analyzeImageUseCase(mockUri, any()) } returns
            DataResult.Error(RuntimeException("err"))

        viewModel = createViewModel()
        viewModel.onImageCaptured(mockUri)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }
}
