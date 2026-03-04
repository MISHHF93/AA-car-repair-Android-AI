package com.aa.carrepair.feature.inspection

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.InspectionMode
import com.aa.carrepair.domain.usecase.inspection.AnalyzeImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InspectionViewModel @Inject constructor(
    private val analyzeImageUseCase: AnalyzeImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InspectionUiState())
    val uiState: StateFlow<InspectionUiState> = _uiState.asStateFlow()

    fun onModeSelected(mode: InspectionMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun onImageCaptured(uri: Uri) {
        _uiState.update { it.copy(capturedImageUri = uri) }
        analyzeImage(uri)
    }

    private fun analyzeImage(uri: Uri) {
        _uiState.update { it.copy(isAnalyzing = true, error = null) }
        viewModelScope.launch {
            when (val result = analyzeImageUseCase(uri, _uiState.value.selectedMode)) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(result = result.data, isAnalyzing = false) }
                }
                is DataResult.Error -> {
                    Timber.e(result.exception, "Image analysis failed")
                    _uiState.update {
                        it.copy(isAnalyzing = false, error = "Failed to analyze image. Please try again.")
                    }
                }
                is DataResult.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
