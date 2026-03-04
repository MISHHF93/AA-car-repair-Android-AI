package com.aa.carrepair.feature.dtc

import com.aa.carrepair.domain.model.DtcCode

data class DtcUiState(
    val searchQuery: String = "",
    val searchResults: List<DtcCode> = emptyList(),
    val selectedCode: DtcCode? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
