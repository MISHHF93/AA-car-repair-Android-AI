package com.aa.carrepair.domain.usecase.dtc

import com.aa.carrepair.domain.model.DtcCode
import com.aa.carrepair.domain.repository.DtcRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchDtcUseCase @Inject constructor(
    private val dtcRepository: DtcRepository
) {
    operator fun invoke(query: String): Flow<List<DtcCode>> =
        dtcRepository.searchDtc(query)
}
