package com.aa.carrepair.domain.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: String): DataResult<Vehicle>
    suspend fun getVehicleByVin(vin: String): DataResult<Vehicle>
    suspend fun decodeVin(vin: String): DataResult<Vehicle>
    suspend fun saveVehicle(vehicle: Vehicle): DataResult<Unit>
    suspend fun updateVehicle(vehicle: Vehicle): DataResult<Unit>
    suspend fun deleteVehicle(id: String): DataResult<Unit>
}
