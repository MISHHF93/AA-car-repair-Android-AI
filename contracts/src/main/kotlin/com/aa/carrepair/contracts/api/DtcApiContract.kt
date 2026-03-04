package com.aa.carrepair.contracts.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DtcAnalysisResponse(
    @Json(name = "code") val code: String,
    @Json(name = "definition") val definition: String,
    @Json(name = "system") val system: String,
    @Json(name = "causes") val causes: List<DtcCauseDto>,
    @Json(name = "symptoms") val symptoms: List<String>,
    @Json(name = "repair_procedures") val repairProcedures: List<String>,
    @Json(name = "safety_level") val safetyLevel: String,
    @Json(name = "confidence_score") val confidenceScore: Int,
    @Json(name = "related_codes") val relatedCodes: List<String> = emptyList(),
    @Json(name = "repair_history") val repairHistory: List<RepairHistoryDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DtcCauseDto(
    @Json(name = "cause") val cause: String,
    @Json(name = "probability") val probability: Double,
    @Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class RepairHistoryDto(
    @Json(name = "repair") val repair: String,
    @Json(name = "success_rate") val successRate: Double,
    @Json(name = "avg_cost") val avgCost: Double,
    @Json(name = "occurrences") val occurrences: Int
)
