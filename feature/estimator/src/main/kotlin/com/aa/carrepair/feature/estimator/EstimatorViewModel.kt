package com.aa.carrepair.feature.estimator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.usecase.estimate.GenerateEstimateUseCase
import com.aa.carrepair.domain.usecase.vehicle.DecodeVinUseCase
import com.aa.carrepair.domain.usecase.vehicle.SaveVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EstimatorViewModel @Inject constructor(
    private val decodeVinUseCase: DecodeVinUseCase,
    private val saveVehicleUseCase: SaveVehicleUseCase,
    private val generateEstimateUseCase: GenerateEstimateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstimatorUiState())
    val uiState: StateFlow<EstimatorUiState> = _uiState.asStateFlow()

    fun onVinChanged(vin: String) {
        _uiState.update { it.copy(vinInput = vin, error = null) }
    }

    fun decodeVin() {
        val vin = _uiState.value.vinInput.trim()
        if (vin.isBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = decodeVinUseCase(vin)) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(selectedVehicle = result.data, isLoading = false) }
                }
                is DataResult.Error -> {
                    Timber.e(result.exception, "VIN decode failed")
                    _uiState.update { it.copy(isLoading = false, error = "Could not decode VIN. Please check and try again.") }
                }
                is DataResult.Loading -> Unit
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category, step = EstimatorStep.DIAGNOSTIC) }
    }

    fun onIssueDescriptionChanged(description: String) {
        _uiState.update { it.copy(issueDescription = description) }
    }

    fun onOemToggled(preferOem: Boolean) {
        _uiState.update { it.copy(preferOem = preferOem) }
    }

    fun generateEstimate() {
        val state = _uiState.value
        val vin = state.selectedVehicle?.vin ?: return
        if (state.issueDescription.isBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = generateEstimateUseCase(
                vehicleVin = vin,
                serviceCategory = state.selectedCategory,
                description = state.issueDescription,
                mileage = state.selectedVehicle?.mileage,
                preferOem = state.preferOem
            )) {
                is DataResult.Success -> {
                    _uiState.update {
                        it.copy(estimate = result.data, isLoading = false, step = EstimatorStep.RESULT)
                    }
                }
                is DataResult.Error -> {
                    Timber.e(result.exception, "Estimate generation failed")
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to generate estimate. Please try again.")
                    }
                }
                is DataResult.Loading -> Unit
            }
        }
    }

    fun proceedToCategory() {
        if (_uiState.value.selectedVehicle != null) {
            _uiState.update { it.copy(step = EstimatorStep.CATEGORY) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
