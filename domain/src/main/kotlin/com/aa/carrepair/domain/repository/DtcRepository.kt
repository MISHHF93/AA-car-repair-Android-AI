package com.aa.carrepair.domain.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.DtcCode
import kotlinx.coroutines.flow.Flow

interface DtcRepository {
    suspend fun analyzeDtc(code: String, vehicleVin: String? = null): DataResult<DtcCode>
    fun searchDtc(query: String): Flow<List<DtcCode>>
    fun getRecentCodes(): Flow<List<DtcCode>>
    suspend fun saveCode(dtcCode: DtcCode): DataResult<Unit>
}
