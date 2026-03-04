package com.aa.carrepair.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.time.Instant

class Converters {
    private val moshi = Moshi.Builder().build()
    private val listStringAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )
    private val mapAdapter = moshi.adapter<Map<String, Double>>(
        Types.newParameterizedType(Map::class.java, String::class.java, Double::class.javaObjectType)
    )

    @TypeConverter
    fun fromStringList(value: List<String>?): String? =
        value?.let { listStringAdapter.toJson(it) }

    @TypeConverter
    fun toStringList(value: String?): List<String>? =
        value?.let { listStringAdapter.fromJson(it) }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromMap(value: Map<String, Double>?): String? =
        value?.let { mapAdapter.toJson(it) }

    @TypeConverter
    fun toMap(value: String?): Map<String, Double>? =
        value?.let { mapAdapter.fromJson(it) }
}
