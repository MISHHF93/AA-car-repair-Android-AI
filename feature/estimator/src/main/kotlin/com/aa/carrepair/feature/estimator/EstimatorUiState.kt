package com.aa.carrepair.feature.estimator

import com.aa.carrepair.domain.model.RepairEstimate
import com.aa.carrepair.domain.model.Vehicle

data class EstimatorUiState(
    val step: EstimatorStep = EstimatorStep.VEHICLE,
    val selectedVehicle: Vehicle? = null,
    val vinInput: String = "",
    val selectedCategory: String = "",
    val issueDescription: String = "",
    val preferOem: Boolean = true,
    val estimate: RepairEstimate? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class EstimatorStep {
    VEHICLE, CATEGORY, DIAGNOSTIC, RESULT
}
