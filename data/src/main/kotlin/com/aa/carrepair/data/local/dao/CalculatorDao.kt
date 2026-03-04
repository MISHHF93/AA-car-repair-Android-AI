package com.aa.carrepair.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aa.carrepair.data.local.entity.CalculationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM calculations ORDER BY saved_at DESC")
    fun getAll(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculations WHERE type = :type ORDER BY saved_at DESC")
    fun getByType(type: String): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculation: CalculationEntity)

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM calculations")
    suspend fun deleteAll()
}
