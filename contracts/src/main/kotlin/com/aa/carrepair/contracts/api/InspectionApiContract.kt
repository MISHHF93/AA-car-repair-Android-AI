package com.aa.carrepair.contracts.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InspectionResponse(
    @Json(name = "inspection_id") val inspectionId: String,
    @Json(name = "mode") val mode: String,
    @Json(name = "findings") val findings: List<InspectionFindingDto>,
    @Json(name = "severity_score") val severityScore: Double,
    @Json(name = "summary") val summary: String,
    @Json(name = "recommendations") val recommendations: List<String>
)

@JsonClass(generateAdapter = true)
data class InspectionFindingDto(
    @Json(name = "type") val type: String,
    @Json(name = "description") val description: String,
    @Json(name = "severity") val severity: String,
    @Json(name = "confidence") val confidence: Double,
    @Json(name = "bounding_box") val boundingBox: BoundingBoxDto?
)

@JsonClass(generateAdapter = true)
data class BoundingBoxDto(
    @Json(name = "left") val left: Float,
    @Json(name = "top") val top: Float,
    @Json(name = "right") val right: Float,
    @Json(name = "bottom") val bottom: Float
)
