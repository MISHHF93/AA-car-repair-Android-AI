package com.aa.carrepair.domain.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.FleetVehicle
import com.aa.carrepair.domain.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

interface FleetRepository {
    fun getFleetVehicles(): Flow<List<FleetVehicle>>
    suspend fun getFleetVehicleById(id: String): DataResult<FleetVehicle>
    suspend fun addFleetVehicle(vehicle: FleetVehicle): DataResult<Unit>
    suspend fun updateFleetVehicle(vehicle: FleetVehicle): DataResult<Unit>
    suspend fun removeFleetVehicle(id: String): DataResult<Unit>
    suspend fun addMaintenanceRecord(vehicleId: String, record: MaintenanceRecord): DataResult<Unit>
    suspend fun getFleetTotalCost(): DataResult<Double>
    suspend fun analyzeFleetCosts(): DataResult<Map<String, Double>>
}
