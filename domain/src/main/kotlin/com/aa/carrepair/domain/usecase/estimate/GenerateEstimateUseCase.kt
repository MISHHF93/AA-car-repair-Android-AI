package com.aa.carrepair.domain.usecase.estimate

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.RepairEstimate
import com.aa.carrepair.domain.repository.EstimateRepository
import javax.inject.Inject

class GenerateEstimateUseCase @Inject constructor(
    private val estimateRepository: EstimateRepository
) {
    suspend operator fun invoke(
        vehicleVin: String,
        serviceCategory: String,
        description: String,
        mileage: Int? = null,
        preferOem: Boolean = true
    ): DataResult<RepairEstimate> =
        estimateRepository.generateEstimate(vehicleVin, serviceCategory, description, mileage, preferOem)
}
