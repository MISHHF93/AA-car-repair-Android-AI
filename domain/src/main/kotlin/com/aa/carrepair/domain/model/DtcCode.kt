package com.aa.carrepair.domain.model

data class DtcCode(
    val code: String,
    val definition: String,
    val system: String,
    val causes: List<DtcCause>,
    val symptoms: List<String>,
    val repairProcedures: List<String>,
    val safetyLevel: SafetyLevel,
    val confidenceScore: Int,
    val relatedCodes: List<String> = emptyList(),
    val repairHistory: List<RepairHistoryEntry> = emptyList()
)

data class DtcCause(
    val cause: String,
    val probability: Double,
    val description: String
)

data class RepairHistoryEntry(
    val repair: String,
    val successRate: Double,
    val avgCost: Double,
    val occurrences: Int
)
