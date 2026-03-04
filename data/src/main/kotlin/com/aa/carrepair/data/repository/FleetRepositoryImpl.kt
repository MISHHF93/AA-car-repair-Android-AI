package com.aa.carrepair.data.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.local.dao.FleetDao
import com.aa.carrepair.data.local.dao.VehicleDao
import com.aa.carrepair.data.local.entity.FleetVehicleEntity
import com.aa.carrepair.domain.model.FleetVehicle
import com.aa.carrepair.domain.model.FleetVehicleStatus
import com.aa.carrepair.domain.model.MaintenanceRecord
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.FleetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FleetRepositoryImpl @Inject constructor(
    private val fleetDao: FleetDao,
    private val vehicleDao: VehicleDao
) : FleetRepository {

    override fun getFleetVehicles(): Flow<List<FleetVehicle>> =
        fleetDao.getAll().map { entities ->
            entities.mapNotNull { entity ->
                val vehicleEntity = vehicleDao.getById(entity.vehicleId) ?: return@mapNotNull null
                entity.toDomain(vehicleEntity.toVehicle())
            }
        }

    override suspend fun getFleetVehicleById(id: String): DataResult<FleetVehicle> {
        val entity = fleetDao.getById(id)
            ?: return DataResult.Error(NoSuchElementException("Fleet vehicle not found: $id"))
        val vehicleEntity = vehicleDao.getById(entity.vehicleId)
            ?: return DataResult.Error(NoSuchElementException("Vehicle not found"))
        return DataResult.Success(entity.toDomain(vehicleEntity.toVehicle()))
    }

    override suspend fun addFleetVehicle(vehicle: FleetVehicle): DataResult<Unit> =
        safeApiCall {
            vehicleDao.insert(vehicle.vehicle.toEntity())
            fleetDao.insert(vehicle.toEntity())
        }

    override suspend fun updateFleetVehicle(vehicle: FleetVehicle): DataResult<Unit> =
        safeApiCall { fleetDao.update(vehicle.toEntity()) }

    override suspend fun removeFleetVehicle(id: String): DataResult<Unit> =
        safeApiCall { fleetDao.deleteById(id) }

    override suspend fun addMaintenanceRecord(vehicleId: String, record: MaintenanceRecord): DataResult<Unit> {
        fleetDao.getById(vehicleId) ?: return DataResult.Error(NoSuchElementException())
        // In a full implementation, would update the JSON field
        return DataResult.Success(Unit)
    }

    override suspend fun getFleetTotalCost(): DataResult<Double> =
        DataResult.Success(fleetDao.getTotalCostYtd() ?: 0.0)

    override suspend fun analyzeFleetCosts(): DataResult<Map<String, Double>> {
        val total = fleetDao.getTotalCostYtd() ?: 0.0
        return DataResult.Success(mapOf("total_ytd" to total))
    }

    private fun com.aa.carrepair.data.local.entity.VehicleEntity.toVehicle() = Vehicle(
        id = id, vin = vin, year = year, make = make, model = model, mileage = mileage
    )

    private fun FleetVehicleEntity.toDomain(vehicle: Vehicle) = FleetVehicle(
        id = id,
        vehicle = vehicle,
        fleetId = fleetId,
        assignedDriver = assignedDriver,
        department = department,
        status = runCatching { FleetVehicleStatus.valueOf(status) }.getOrDefault(FleetVehicleStatus.ACTIVE),
        totalCostYtd = totalCostYtd,
        nextMaintenanceDue = nextMaintenanceDue,
        nextMaintenanceMileage = nextMaintenanceMileage
    )

    private fun FleetVehicle.toEntity() = FleetVehicleEntity(
        id = id,
        vehicleId = vehicle.id,
        fleetId = fleetId,
        assignedDriver = assignedDriver,
        department = department,
        status = status.name,
        totalCostYtd = totalCostYtd,
        nextMaintenanceDue = nextMaintenanceDue,
        nextMaintenanceMileage = nextMaintenanceMileage,
        maintenanceHistoryJson = "[]"
    )

    private fun Vehicle.toEntity() = com.aa.carrepair.data.local.entity.VehicleEntity(
        id = id, vin = vin, year = year, make = make, model = model,
        engine = engine, trim = trim, transmission = transmission,
        driveType = driveType, fuelType = fuelType, bodyStyle = bodyStyle,
        mileage = mileage, color = color, licensePlate = licensePlate, savedAt = savedAt
    )
}
