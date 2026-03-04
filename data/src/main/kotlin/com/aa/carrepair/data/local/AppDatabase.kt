package com.aa.carrepair.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aa.carrepair.data.local.dao.CalculatorDao
import com.aa.carrepair.data.local.dao.ChatDao
import com.aa.carrepair.data.local.dao.DtcDao
import com.aa.carrepair.data.local.dao.EstimateDao
import com.aa.carrepair.data.local.dao.FleetDao
import com.aa.carrepair.data.local.dao.VehicleDao
import com.aa.carrepair.data.local.entity.CalculationEntity
import com.aa.carrepair.data.local.entity.ChatMessageEntity
import com.aa.carrepair.data.local.entity.DtcEntity
import com.aa.carrepair.data.local.entity.EstimateEntity
import com.aa.carrepair.data.local.entity.FleetVehicleEntity
import com.aa.carrepair.data.local.entity.VehicleEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        VehicleEntity::class,
        ChatMessageEntity::class,
        EstimateEntity::class,
        DtcEntity::class,
        FleetVehicleEntity::class,
        CalculationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun chatDao(): ChatDao
    abstract fun estimateDao(): EstimateDao
    abstract fun dtcDao(): DtcDao
    abstract fun fleetDao(): FleetDao
    abstract fun calculatorDao(): CalculatorDao

    companion object {
        fun create(context: Context, passphrase: ByteArray): AppDatabase {
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(context, AppDatabase::class.java, "aa_carrepair.db")
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
