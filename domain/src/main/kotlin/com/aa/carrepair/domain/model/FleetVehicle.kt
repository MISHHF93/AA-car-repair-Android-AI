package com.aa.carrepair.domain.model

import java.time.Instant

data class FleetVehicle(
    val id: String,
    val vehicle: Vehicle,
    val fleetId: String,
    val assignedDriver: String? = null,
    val department: String? = null,
    val status: FleetVehicleStatus = FleetVehicleStatus.ACTIVE,
    val maintenanceHistory: List<MaintenanceRecord> = emptyList(),
    val totalCostYtd: Double = 0.0,
    val nextMaintenanceDue: Instant? = null,
    val nextMaintenanceMileage: Int? = null
)

enum class FleetVehicleStatus {
    ACTIVE, INACTIVE, IN_MAINTENANCE, RETIRED
}

data class MaintenanceRecord(
    val id: String,
    val type: String,
    val description: String,
    val cost: Double,
    val performedAt: Instant,
    val mileageAtService: Int,
    val performedBy: String? = null
)
