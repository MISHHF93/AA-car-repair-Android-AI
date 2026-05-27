package com.aa.carrepair.domain.model

data class VehicleContext(
    val vin: String? = null,
    val year: Int? = null,
    val make: String? = null,
    val model: String? = null,
    val mileageKm: Int? = null
) {
    val hasAnyValue: Boolean
        get() = vin != null || year != null || make != null || model != null || mileageKm != null
}
