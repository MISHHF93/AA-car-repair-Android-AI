package com.aa.carrepair.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "dtc_codes_fts")
data class DtcEntity(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "definition") val definition: String,
    @ColumnInfo(name = "system") val system: String,
    @ColumnInfo(name = "causes_json") val causesJson: String,
    @ColumnInfo(name = "symptoms_json") val symptomsJson: String,
    @ColumnInfo(name = "repair_procedures_json") val repairProceduresJson: String,
    @ColumnInfo(name = "safety_level") val safetyLevel: String,
    @ColumnInfo(name = "confidence_score") val confidenceScore: Int,
    @ColumnInfo(name = "related_codes_json") val relatedCodesJson: String,
    @ColumnInfo(name = "repair_history_json") val repairHistoryJson: String
)
