package com.aa.carrepair.domain.model

import java.time.Instant

data class InspectionResult(
    val id: String,
    val mode: InspectionMode,
    val findings: List<InspectionFinding>,
    val severityScore: Double,
    val summary: String,
    val recommendations: List<String>,
    val annotatedImageUri: String? = null,
    val createdAt: Instant = Instant.now()
)

enum class InspectionMode {
    DAMAGE_ASSESSMENT,
    PARTS_IDENTIFICATION,
    WEAR_ANALYSIS
}

data class InspectionFinding(
    val type: String,
    val description: String,
    val severity: FindingSeverity,
    val confidence: Double,
    val boundingBox: BoundingBox? = null
)

enum class FindingSeverity {
    CRITICAL, HIGH, MEDIUM, LOW, INFO
}

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)
