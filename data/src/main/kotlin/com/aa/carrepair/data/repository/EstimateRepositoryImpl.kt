package com.aa.carrepair.data.repository

import com.aa.carrepair.contracts.api.EstimateRequest
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.local.dao.EstimateDao
import com.aa.carrepair.data.local.dao.VehicleDao
import com.aa.carrepair.data.local.entity.EstimateEntity
import com.aa.carrepair.data.remote.api.EstimatorApi
import com.aa.carrepair.domain.model.LaborItem
import com.aa.carrepair.domain.model.Part
import com.aa.carrepair.domain.model.RepairEstimate
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.EstimateRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class EstimateRepositoryImpl @Inject constructor(
    private val estimateDao: EstimateDao,
    private val vehicleDao: VehicleDao,
    private val estimatorApi: EstimatorApi,
    private val moshi: Moshi
) : EstimateRepository {

    override fun getEstimates(): Flow<List<RepairEstimate>> =
        estimateDao.getAll().map { entities ->
            entities.mapNotNull { entity ->
                val vehicleEntity = vehicleDao.getById(entity.vehicleId) ?: return@mapNotNull null
                entity.toDomain(vehicleEntity.toVehicle())
            }
        }

    override suspend fun getEstimateById(id: String): DataResult<RepairEstimate> {
        val entity = estimateDao.getById(id)
            ?: return DataResult.Error(NoSuchElementException("Estimate not found: $id"))
        val vehicleEntity = vehicleDao.getById(entity.vehicleId)
            ?: return DataResult.Error(NoSuchElementException("Vehicle not found"))
        return DataResult.Success(entity.toDomain(vehicleEntity.toVehicle()))
    }

    override suspend fun generateEstimate(
        vehicleVin: String,
        serviceCategory: String,
        description: String,
        mileage: Int?,
        preferOem: Boolean
    ): DataResult<RepairEstimate> {
        val vehicleEntity = vehicleDao.getByVin(vehicleVin)
        return safeApiCall {
            val response = estimatorApi.generateEstimate(
                EstimateRequest(
                    vehicleVin = vehicleVin,
                    serviceCategory = serviceCategory,
                    description = description,
                    mileage = mileage,
                    preferOem = preferOem
                )
            )
            val vehicle = vehicleEntity?.toVehicle() ?: Vehicle(
                id = UUID.randomUUID().toString(),
                vin = vehicleVin,
                year = response.vehicle.year,
                make = response.vehicle.make,
                model = response.vehicle.model
            )
            RepairEstimate(
                id = response.estimateId,
                vehicle = vehicle,
                serviceCategory = response.serviceCategory,
                description = description,
                parts = response.parts.map {
                    Part(it.partNumber, it.name, it.oemPrice, it.aftermarketPrice, it.availability, it.isOem)
                },
                laborItems = response.laborItems.map {
                    LaborItem(it.description, it.hours, it.rate, it.total)
                },
                subtotalParts = response.subtotalParts,
                subtotalLabor = response.subtotalLabor,
                fees = response.fees,
                tax = response.tax,
                total = response.total,
                confidence = response.confidence,
                isBinding = response.isBinding,
                disclaimer = response.disclaimer,
                preferOem = preferOem
            )
        }
    }

    override suspend fun saveEstimate(estimate: RepairEstimate): DataResult<Unit> =
        safeApiCall { estimateDao.insert(estimate.toEntity()) }

    override suspend fun deleteEstimate(id: String): DataResult<Unit> =
        safeApiCall { estimateDao.deleteById(id) }

    private fun com.aa.carrepair.data.local.entity.VehicleEntity.toVehicle() = Vehicle(
        id = id, vin = vin, year = year, make = make, model = model,
        engine = engine, trim = trim, mileage = mileage
    )

    private fun EstimateEntity.toDomain(vehicle: Vehicle): RepairEstimate {
        val partsType = Types.newParameterizedType(List::class.java, Map::class.java)
        return RepairEstimate(
            id = id,
            vehicle = vehicle,
            serviceCategory = serviceCategory,
            description = description,
            parts = emptyList(),
            laborItems = emptyList(),
            subtotalParts = subtotalParts,
            subtotalLabor = subtotalLabor,
            fees = fees,
            tax = tax,
            total = total,
            confidence = confidence,
            isBinding = isBinding,
            disclaimer = disclaimer,
            preferOem = preferOem,
            createdAt = createdAt
        )
    }

    private fun RepairEstimate.toEntity() = EstimateEntity(
        id = id,
        vehicleId = vehicle.id,
        serviceCategory = serviceCategory,
        description = description,
        subtotalParts = subtotalParts,
        subtotalLabor = subtotalLabor,
        fees = fees,
        tax = tax,
        total = total,
        confidence = confidence,
        isBinding = isBinding,
        disclaimer = disclaimer,
        partsJson = "[]",
        laborJson = "[]",
        preferOem = preferOem,
        createdAt = createdAt
    )
}
