package com.aa.carrepair.data.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.local.dao.DtcDao
import com.aa.carrepair.data.remote.api.DtcApi
import com.aa.carrepair.domain.model.DtcCode
import com.aa.carrepair.domain.model.DtcCause
import com.aa.carrepair.domain.model.RepairHistoryEntry
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.repository.DtcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DtcRepositoryImpl @Inject constructor(
    private val dtcDao: DtcDao,
    private val dtcApi: DtcApi
) : DtcRepository {

    override suspend fun analyzeDtc(code: String, vehicleVin: String?): DataResult<DtcCode> {
        return safeApiCall {
            val response = dtcApi.analyzeDtc(code, vehicleVin)
            DtcCode(
                code = response.code,
                definition = response.definition,
                system = response.system,
                causes = response.causes.map {
                    DtcCause(it.cause, it.probability, it.description)
                },
                symptoms = response.symptoms,
                repairProcedures = response.repairProcedures,
                safetyLevel = SafetyLevel.valueOf(response.safetyLevel.uppercase()),
                confidenceScore = response.confidenceScore,
                relatedCodes = response.relatedCodes,
                repairHistory = response.repairHistory.map {
                    RepairHistoryEntry(it.repair, it.successRate, it.avgCost, it.occurrences)
                }
            )
        }
    }

    override fun searchDtc(query: String): Flow<List<DtcCode>> {
        if (query.isBlank()) return flowOf(emptyList())
        return dtcDao.search(query).map { entities ->
            entities.map { entity ->
                DtcCode(
                    code = entity.code,
                    definition = entity.definition,
                    system = entity.system,
                    causes = emptyList(),
                    symptoms = emptyList(),
                    repairProcedures = emptyList(),
                    safetyLevel = runCatching { SafetyLevel.valueOf(entity.safetyLevel.uppercase()) }
                        .getOrDefault(SafetyLevel.LOW),
                    confidenceScore = entity.confidenceScore
                )
            }
        }
    }

    override fun getRecentCodes(): Flow<List<DtcCode>> = flowOf(emptyList())

    override suspend fun saveCode(dtcCode: DtcCode): DataResult<Unit> = DataResult.Success(Unit)
}
