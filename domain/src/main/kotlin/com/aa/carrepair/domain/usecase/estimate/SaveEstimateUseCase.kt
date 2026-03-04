package com.aa.carrepair.domain.usecase.estimate

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.RepairEstimate
import com.aa.carrepair.domain.repository.EstimateRepository
import javax.inject.Inject

class SaveEstimateUseCase @Inject constructor(
    private val estimateRepository: EstimateRepository
) {
    suspend operator fun invoke(estimate: RepairEstimate): DataResult<Unit> =
        estimateRepository.saveEstimate(estimate)
}
