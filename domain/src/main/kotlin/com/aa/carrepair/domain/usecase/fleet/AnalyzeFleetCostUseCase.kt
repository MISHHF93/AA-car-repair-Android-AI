package com.aa.carrepair.domain.usecase.fleet

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.repository.FleetRepository
import javax.inject.Inject

class AnalyzeFleetCostUseCase @Inject constructor(
    private val fleetRepository: FleetRepository
) {
    suspend operator fun invoke(): DataResult<Map<String, Double>> =
        fleetRepository.analyzeFleetCosts()
}
