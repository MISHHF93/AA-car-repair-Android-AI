package com.aa.carrepair.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "inputs_json") val inputsJson: String,
    @ColumnInfo(name = "outputs_json") val outputsJson: String,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "saved_at") val savedAt: Instant
)
