package com.aa.carrepair.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "estimates")
data class EstimateEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "vehicle_id") val vehicleId: String,
    @ColumnInfo(name = "service_category") val serviceCategory: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "subtotal_parts") val subtotalParts: Double,
    @ColumnInfo(name = "subtotal_labor") val subtotalLabor: Double,
    @ColumnInfo(name = "fees") val fees: Double,
    @ColumnInfo(name = "tax") val tax: Double,
    @ColumnInfo(name = "total") val total: Double,
    @ColumnInfo(name = "confidence") val confidence: Int,
    @ColumnInfo(name = "is_binding") val isBinding: Boolean,
    @ColumnInfo(name = "disclaimer") val disclaimer: String,
    @ColumnInfo(name = "parts_json") val partsJson: String,
    @ColumnInfo(name = "labor_json") val laborJson: String,
    @ColumnInfo(name = "prefer_oem") val preferOem: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Instant
)
