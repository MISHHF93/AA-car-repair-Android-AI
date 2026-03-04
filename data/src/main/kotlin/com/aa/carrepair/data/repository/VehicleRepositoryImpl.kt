package com.aa.carrepair.data.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.local.dao.VehicleDao
import com.aa.carrepair.data.local.entity.VehicleEntity
import com.aa.carrepair.data.remote.api.VehicleApi
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val vehicleApi: VehicleApi
) : VehicleRepository {

    override fun getVehicles(): Flow<List<Vehicle>> =
        vehicleDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getVehicleById(id: String): DataResult<Vehicle> {
        val entity = vehicleDao.getById(id)
            ?: return DataResult.Error(NoSuchElementException("Vehicle not found: $id"))
        return DataResult.Success(entity.toDomain())
    }

    override suspend fun getVehicleByVin(vin: String): DataResult<Vehicle> {
        val entity = vehicleDao.getByVin(vin)
            ?: return DataResult.Error(NoSuchElementException("Vehicle not found for VIN: $vin"))
        return DataResult.Success(entity.toDomain())
    }

    override suspend fun decodeVin(vin: String): DataResult<Vehicle> {
        // Check local cache first
        vehicleDao.getByVin(vin)?.let { return DataResult.Success(it.toDomain()) }
        // Fetch from API
        return safeApiCall {
            val response = vehicleApi.decodeVin(vin)
            val vehicle = response.vehicle
            Vehicle(
                id = UUID.randomUUID().toString(),
                vin = vehicle.vin,
                year = vehicle.year,
                make = vehicle.make,
                model = vehicle.model,
                engine = vehicle.engine,
                trim = vehicle.trim,
                transmission = vehicle.transmission,
                driveType = vehicle.driveType,
                fuelType = vehicle.fuelType,
                bodyStyle = vehicle.bodyStyle
            )
        }
    }

    override suspend fun saveVehicle(vehicle: Vehicle): DataResult<Unit> =
        safeApiCall { vehicleDao.insert(vehicle.toEntity()) }

    override suspend fun updateVehicle(vehicle: Vehicle): DataResult<Unit> =
        safeApiCall { vehicleDao.update(vehicle.toEntity()) }

    override suspend fun deleteVehicle(id: String): DataResult<Unit> =
        safeApiCall { vehicleDao.deleteById(id) }

    private fun VehicleEntity.toDomain() = Vehicle(
        id = id, vin = vin, year = year, make = make, model = model,
        engine = engine, trim = trim, transmission = transmission,
        driveType = driveType, fuelType = fuelType, bodyStyle = bodyStyle,
        mileage = mileage, color = color, licensePlate = licensePlate, savedAt = savedAt
    )

    private fun Vehicle.toEntity() = VehicleEntity(
        id = id, vin = vin, year = year, make = make, model = model,
        engine = engine, trim = trim, transmission = transmission,
        driveType = driveType, fuelType = fuelType, bodyStyle = bodyStyle,
        mileage = mileage, color = color, licensePlate = licensePlate, savedAt = savedAt
    )
}
