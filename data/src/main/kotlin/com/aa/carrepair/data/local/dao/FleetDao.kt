package com.aa.carrepair.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aa.carrepair.data.local.entity.FleetVehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FleetDao {
    @Query("SELECT * FROM fleet_vehicles ORDER BY id ASC")
    fun getAll(): Flow<List<FleetVehicleEntity>>

    @Query("SELECT * FROM fleet_vehicles WHERE id = :id")
    suspend fun getById(id: String): FleetVehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: FleetVehicleEntity)

    @Update
    suspend fun update(vehicle: FleetVehicleEntity)

    @Query("DELETE FROM fleet_vehicles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT SUM(total_cost_ytd) FROM fleet_vehicles")
    suspend fun getTotalCostYtd(): Double?
}
