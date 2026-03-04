package com.aa.carrepair.domain.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.CalculationResult
import com.aa.carrepair.domain.model.CalculatorType
import kotlinx.coroutines.flow.Flow

interface CalculatorRepository {
    fun getSavedCalculations(type: CalculatorType? = null): Flow<List<CalculationResult>>
    suspend fun saveCalculation(result: CalculationResult): DataResult<Unit>
    suspend fun deleteCalculation(id: String): DataResult<Unit>
    suspend fun clearHistory(): DataResult<Unit>
}
