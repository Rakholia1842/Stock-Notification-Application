package com.stockalert.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stockSymbol: String,
    val stockName: String,
    val targetPrice: Double,
    val alertType: String, // "ABOVE", "BELOW", "PERCENTAGE", "VOLUME", "MOVING_AVG"
    val status: String = "ACTIVE", // "ACTIVE" or "TRIGGERED"
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null,
    val percentageChange: Double? = null, // For percentage-based alerts
    val volumeThreshold: Long? = null, // For volume spike alerts
    val movingAverageDays: Int? = null, // For moving average crossovers
    val alertFrequency: String = "REAL_TIME", // "REAL_TIME", "HOURLY", "DAILY"
    val quietHourStart: Int? = null, // 0-23 hours
    val quietHourEnd: Int? = null, // 0-23 hours
    val portfolioId: Int? = null // Link to portfolio
)

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val previousClose: Double,
    val lastUpdated: Long = System.currentTimeMillis(),
    val dataSource: String = "YAHOO_FINANCE" // Source of data
)

// Portfolio Management
@Entity(tableName = "portfolios")
data class PortfolioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val totalValue: Double = 0.0,
    val isDefault: Boolean = false
)

@Entity(tableName = "watchlists")
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val portfolioId: Int? = null
)

@Entity(tableName = "watchlist_items")
data class WatchlistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val watchlistId: Int,
    val stockSymbol: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_history")
data class PriceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stockSymbol: String,
    val price: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val dataSource: String = "YAHOO_FINANCE"
)

// User Preferences & Settings
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Single row
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultAlertFrequency: String = "REAL_TIME",
    val darkModeEnabled: Boolean = false,
    val currencySymbol: String = "₹",
    val decimalPlaces: Int = 2,
    val refreshIntervalMinutes: Int = 15,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "notification_settings")
data class NotificationSettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stockSymbol: String,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val emailNotifications: Boolean = false,
    val smsNotifications: Boolean = false,
    val quietHourStart: Int? = null,
    val quietHourEnd: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// News & Media
@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey
    val id: String,
    val stockSymbol: String,
    val title: String,
    val summary: String? = null,
    val source: String,
    val url: String,
    val imageUrl: String? = null,
    val publishedAt: Long,
    val fetchedAt: Long = System.currentTimeMillis()
)

// Premium Features
@Entity(tableName = "price_predictions")
data class PricePredictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stockSymbol: String,
    val predictedPrice: Double,
    val confidence: Double, // 0-1
    val predictionDate: Long,
    val modelVersion: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "alert_templates")
data class AlertTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val alertType: String,
    val basePrice: Double? = null,
    val percentageChange: Double? = null,
    val volumeThreshold: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
