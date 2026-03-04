package com.aa.carrepair.domain.model

import java.time.Instant

data class RepairEstimate(
    val id: String,
    val vehicle: Vehicle,
    val serviceCategory: String,
    val description: String,
    val parts: List<Part>,
    val laborItems: List<LaborItem>,
    val subtotalParts: Double,
    val subtotalLabor: Double,
    val fees: Double,
    val tax: Double,
    val total: Double,
    val confidence: Int,
    val isBinding: Boolean = false,
    val disclaimer: String,
    val preferOem: Boolean = true,
    val createdAt: Instant = Instant.now()
)

data class Part(
    val partNumber: String,
    val name: String,
    val oemPrice: Double,
    val aftermarketPrice: Double?,
    val availability: String,
    val isOem: Boolean = true
) {
    val selectedPrice: Double get() = if (isOem) oemPrice else aftermarketPrice ?: oemPrice
}

data class LaborItem(
    val description: String,
    val hours: Double,
    val rate: Double,
    val total: Double
)
