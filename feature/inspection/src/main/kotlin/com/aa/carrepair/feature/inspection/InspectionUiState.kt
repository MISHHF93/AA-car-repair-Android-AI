package com.aa.carrepair.feature.inspection

import com.aa.carrepair.domain.model.InspectionMode
import com.aa.carrepair.domain.model.InspectionResult

data class InspectionUiState(
    val selectedMode: InspectionMode = InspectionMode.DAMAGE_ASSESSMENT,
    val capturedImageUri: android.net.Uri? = null,
    val result: InspectionResult? = null,
    val isAnalyzing: Boolean = false,
    val error: String? = null
)
