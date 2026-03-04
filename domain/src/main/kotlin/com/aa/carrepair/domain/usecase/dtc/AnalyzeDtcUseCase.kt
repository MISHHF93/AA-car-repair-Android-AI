package com.aa.carrepair.domain.usecase.dtc

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.util.isValidDtcCode
import com.aa.carrepair.domain.model.DtcCode
import com.aa.carrepair.domain.repository.DtcRepository
import javax.inject.Inject

class AnalyzeDtcUseCase @Inject constructor(
    private val dtcRepository: DtcRepository
) {
    suspend operator fun invoke(code: String, vehicleVin: String? = null): DataResult<DtcCode> {
        if (!code.isValidDtcCode()) {
            return DataResult.Error(IllegalArgumentException("Invalid DTC code: $code"))
        }
        return dtcRepository.analyzeDtc(code.uppercase(), vehicleVin)
    }
}
