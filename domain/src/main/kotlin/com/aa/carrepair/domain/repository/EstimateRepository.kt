package com.aa.carrepair.domain.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.RepairEstimate
import kotlinx.coroutines.flow.Flow

interface EstimateRepository {
    fun getEstimates(): Flow<List<RepairEstimate>>
    suspend fun getEstimateById(id: String): DataResult<RepairEstimate>
    suspend fun generateEstimate(
        vehicleVin: String,
        serviceCategory: String,
        description: String,
        mileage: Int?,
        preferOem: Boolean
    ): DataResult<RepairEstimate>
    suspend fun saveEstimate(estimate: RepairEstimate): DataResult<Unit>
    suspend fun deleteEstimate(id: String): DataResult<Unit>
}
