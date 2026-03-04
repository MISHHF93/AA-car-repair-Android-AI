package com.aa.carrepair.feature.fleet

import com.aa.carrepair.domain.model.FleetVehicle

data class FleetUiState(
    val vehicles: List<FleetVehicle> = emptyList(),
    val totalCostYtd: Double = 0.0,
    val avgCostPerVehicle: Double = 0.0,
    val maintenanceDueCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
