package com.stockalert.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PriceAlertEntity::class, StockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StockAlertDatabase : RoomDatabase() {
    abstract fun priceAlertDao(): PriceAlertDao
    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var INSTANCE: StockAlertDatabase? = null

        fun getDatabase(context: Context): StockAlertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockAlertDatabase::class.java,
                    "stock_alert_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
