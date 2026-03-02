package com.stockalert.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PriceAlertEntity::class,
        StockEntity::class,
        PortfolioEntity::class,
        WatchlistEntity::class,
        WatchlistItemEntity::class,
        PriceHistoryEntity::class,
        UserSettingsEntity::class,
        NotificationSettingsEntity::class,
        NewsArticleEntity::class,
        PricePredictionEntity::class,
        AlertTemplateEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StockAlertDatabase : RoomDatabase() {
    abstract fun priceAlertDao(): PriceAlertDao
    abstract fun stockDao(): StockDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun watchlistItemDao(): WatchlistItemDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    abstract fun newsDao(): NewsDao
    abstract fun predictionDao(): PredictionDao
    abstract fun alertTemplateDao(): AlertTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: StockAlertDatabase? = null

        fun getDatabase(context: Context): StockAlertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockAlertDatabase::class.java,
                    "stock_alert_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
