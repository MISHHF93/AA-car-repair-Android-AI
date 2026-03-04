package com.aa.carrepair.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "vehicles", indices = [Index("vin", unique = true)])
data class VehicleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "vin") val vin: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "make") val make: String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "engine") val engine: String?,
    @ColumnInfo(name = "trim") val trim: String?,
    @ColumnInfo(name = "transmission") val transmission: String?,
    @ColumnInfo(name = "drive_type") val driveType: String?,
    @ColumnInfo(name = "fuel_type") val fuelType: String?,
    @ColumnInfo(name = "body_style") val bodyStyle: String?,
    @ColumnInfo(name = "mileage") val mileage: Int?,
    @ColumnInfo(name = "color") val color: String?,
    @ColumnInfo(name = "license_plate") val licensePlate: String?,
    @ColumnInfo(name = "saved_at") val savedAt: Instant = Instant.now()
)
