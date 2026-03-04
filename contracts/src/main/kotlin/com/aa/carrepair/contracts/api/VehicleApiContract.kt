package com.aa.carrepair.contracts.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VehicleDto(
    @Json(name = "vin") val vin: String,
    @Json(name = "year") val year: Int,
    @Json(name = "make") val make: String,
    @Json(name = "model") val model: String,
    @Json(name = "engine") val engine: String?,
    @Json(name = "trim") val trim: String?,
    @Json(name = "transmission") val transmission: String?,
    @Json(name = "drive_type") val driveType: String?,
    @Json(name = "fuel_type") val fuelType: String?,
    @Json(name = "body_style") val bodyStyle: String?
)

@JsonClass(generateAdapter = true)
data class VinDecodeResponse(
    @Json(name = "vin") val vin: String,
    @Json(name = "vehicle") val vehicle: VehicleDto,
    @Json(name = "is_valid") val isValid: Boolean,
    @Json(name = "error") val error: String? = null
)
