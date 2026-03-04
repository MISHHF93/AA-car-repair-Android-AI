package com.aa.carrepair.domain.model

import java.time.Instant

data class Vehicle(
    val id: String,
    val vin: String,
    val year: Int,
    val make: String,
    val model: String,
    val engine: String? = null,
    val trim: String? = null,
    val transmission: String? = null,
    val driveType: String? = null,
    val fuelType: String? = null,
    val bodyStyle: String? = null,
    val mileage: Int? = null,
    val color: String? = null,
    val licensePlate: String? = null,
    val savedAt: Instant = Instant.now()
) {
    val displayName: String get() = "$year $make $model"
}
