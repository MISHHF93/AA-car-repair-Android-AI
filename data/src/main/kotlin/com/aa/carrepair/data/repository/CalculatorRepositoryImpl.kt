package com.aa.carrepair.data.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.local.dao.CalculatorDao
import com.aa.carrepair.data.local.entity.CalculationEntity
import com.aa.carrepair.domain.model.CalculationResult
import com.aa.carrepair.domain.model.CalculatorType
import com.aa.carrepair.domain.repository.CalculatorRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculatorRepositoryImpl @Inject constructor(
    private val calculatorDao: CalculatorDao,
    private val moshi: Moshi
) : CalculatorRepository {

    private val mapAdapter = moshi.adapter<Map<String, Double>>(
        Types.newParameterizedType(Map::class.java, String::class.java, Double::class.javaObjectType)
    )

    override fun getSavedCalculations(type: CalculatorType?): Flow<List<CalculationResult>> {
        val flow = if (type != null) calculatorDao.getByType(type.name) else calculatorDao.getAll()
        return flow.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveCalculation(result: CalculationResult): DataResult<Unit> =
        safeApiCall { calculatorDao.insert(result.toEntity()) }

    override suspend fun deleteCalculation(id: String): DataResult<Unit> =
        safeApiCall { calculatorDao.deleteById(id) }

    override suspend fun clearHistory(): DataResult<Unit> =
        safeApiCall { calculatorDao.deleteAll() }

    private fun CalculationEntity.toDomain() = CalculationResult(
        id = id,
        type = runCatching { CalculatorType.valueOf(type) }.getOrDefault(CalculatorType.LABOR_TIME),
        inputs = mapAdapter.fromJson(inputsJson) ?: emptyMap(),
        outputs = mapAdapter.fromJson(outputsJson) ?: emptyMap(),
        notes = notes,
        savedAt = savedAt
    )

    private fun CalculationResult.toEntity() = CalculationEntity(
        id = id,
        type = type.name,
        inputsJson = mapAdapter.toJson(inputs),
        outputsJson = mapAdapter.toJson(outputs),
        notes = notes,
        savedAt = savedAt
    )
}
