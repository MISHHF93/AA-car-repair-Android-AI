package com.aa.carrepair.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aa.carrepair.data.local.entity.EstimateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {
    @Query("SELECT * FROM estimates ORDER BY created_at DESC")
    fun getAll(): Flow<List<EstimateEntity>>

    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getById(id: String): EstimateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estimate: EstimateEntity)

    @Query("DELETE FROM estimates WHERE id = :id")
    suspend fun deleteById(id: String)
}
