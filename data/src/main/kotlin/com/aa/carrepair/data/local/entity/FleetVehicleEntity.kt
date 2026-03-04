package com.aa.carrepair.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "fleet_vehicles")
data class FleetVehicleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "vehicle_id") val vehicleId: String,
    @ColumnInfo(name = "fleet_id") val fleetId: String,
    @ColumnInfo(name = "assigned_driver") val assignedDriver: String?,
    @ColumnInfo(name = "department") val department: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "total_cost_ytd") val totalCostYtd: Double,
    @ColumnInfo(name = "next_maintenance_due") val nextMaintenanceDue: Instant?,
    @ColumnInfo(name = "next_maintenance_mileage") val nextMaintenanceMileage: Int?,
    @ColumnInfo(name = "maintenance_history_json") val maintenanceHistoryJson: String
)
