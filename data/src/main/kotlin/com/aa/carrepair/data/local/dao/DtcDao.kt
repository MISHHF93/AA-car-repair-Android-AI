package com.aa.carrepair.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aa.carrepair.data.local.entity.DtcEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DtcDao {
    @Query("SELECT rowid, * FROM dtc_codes_fts WHERE dtc_codes_fts MATCH :query LIMIT 50")
    fun search(query: String): Flow<List<DtcEntity>>

    @Query("SELECT rowid, * FROM dtc_codes_fts WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): DtcEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(codes: List<DtcEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(code: DtcEntity)
}
