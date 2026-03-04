package com.aa.carrepair.contracts.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EstimateRequest(
    @Json(name = "vehicle_vin") val vehicleVin: String,
    @Json(name = "service_category") val serviceCategory: String,
    @Json(name = "description") val description: String,
    @Json(name = "mileage") val mileage: Int? = null,
    @Json(name = "prefer_oem") val preferOem: Boolean = true,
    @Json(name = "zip_code") val zipCode: String? = null
)

@JsonClass(generateAdapter = true)
data class EstimateResponse(
    @Json(name = "estimate_id") val estimateId: String,
    @Json(name = "vehicle") val vehicle: VehicleDto,
    @Json(name = "service_category") val serviceCategory: String,
    @Json(name = "parts") val parts: List<PartDto>,
    @Json(name = "labor_items") val laborItems: List<LaborItemDto>,
    @Json(name = "subtotal_parts") val subtotalParts: Double,
    @Json(name = "subtotal_labor") val subtotalLabor: Double,
    @Json(name = "fees") val fees: Double,
    @Json(name = "tax") val tax: Double,
    @Json(name = "total") val total: Double,
    @Json(name = "confidence") val confidence: Int,
    @Json(name = "is_binding") val isBinding: Boolean = false,
    @Json(name = "disclaimer") val disclaimer: String
)

@JsonClass(generateAdapter = true)
data class PartDto(
    @Json(name = "part_number") val partNumber: String,
    @Json(name = "name") val name: String,
    @Json(name = "oem_price") val oemPrice: Double,
    @Json(name = "aftermarket_price") val aftermarketPrice: Double?,
    @Json(name = "availability") val availability: String,
    @Json(name = "is_oem") val isOem: Boolean = true
)

@JsonClass(generateAdapter = true)
data class LaborItemDto(
    @Json(name = "description") val description: String,
    @Json(name = "hours") val hours: Double,
    @Json(name = "rate") val rate: Double,
    @Json(name = "total") val total: Double
)
