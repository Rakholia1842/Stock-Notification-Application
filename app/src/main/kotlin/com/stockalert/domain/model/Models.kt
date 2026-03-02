package com.stockalert.domain.model

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class Stock(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val previousClose: Double,
    val percentChange: Double,
    val dataSource: String = "YAHOO_FINANCE"
) {
    val formattedPrice: String get() = "₹${String.format("%.2f", currentPrice)}"
    val formattedChange: String get() = String.format("%.2f", percentChange)
    val isPositive: Boolean get() = percentChange >= 0.0
}

data class PriceAlert(
    val id: Int,
    val stockSymbol: String,
    val stockName: String,
    val targetPrice: Double,
    val alertType: AlertType,
    val status: AlertStatus,
    val createdAt: Long,
    val triggeredAt: Long? = null,
    val alertFrequency: AlertFrequency = AlertFrequency.REAL_TIME,
    val quietHourStart: Int? = null,
    val quietHourEnd: Int? = null,
    val percentageChange: Double? = null,
    val volumeThreshold: Long? = null,
    val portfolioId: Int? = null
) {
    val formattedTargetPrice: String get() = "₹${String.format("%.2f", targetPrice)}"
    val formattedCreatedAt: String get() {
        val date = Date(createdAt)
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
    }
    val isInQuietHour: Boolean get() {
        if (quietHourStart == null || quietHourEnd == null) return false
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (quietHourStart < quietHourEnd) {
            currentHour in quietHourStart until quietHourEnd
        } else {
            currentHour >= quietHourStart || currentHour < quietHourEnd
        }
    }
}

// Portfolio Management
data class Portfolio(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val totalValue: Double = 0.0,
    val stocks: List<Stock> = emptyList(),
    val isDefault: Boolean = false
) {
    val formattedValue: String get() = "₹${String.format("%.2f", totalValue)}"
}

data class Watchlist(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val createdAt: Long,
    val stocks: List<Stock> = emptyList(),
    val portfolioId: Int? = null
)

// Price History for Charts
data class PriceHistory(
    val stockSymbol: String,
    val prices: List<PricePoint> = emptyList(),
    val averagePrice: Double = 0.0,
    val movingAverage: List<Double> = emptyList()
)

data class PricePoint(
    val timestamp: Long,
    val price: Double,
    val high: Double,
    val low: Double,
    val volume: Long
)

// User Settings
data class UserSettings(
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultAlertFrequency: AlertFrequency = AlertFrequency.REAL_TIME,
    val darkModeEnabled: Boolean = false,
    val currencySymbol: String = "₹",
    val decimalPlaces: Int = 2,
    val refreshIntervalMinutes: Int = 15
)

data class NotificationSettings(
    val stockSymbol: String,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val emailNotifications: Boolean = false,
    val smsNotifications: Boolean = false,
    val quietHourStart: Int? = null,
    val quietHourEnd: Int? = null
)

// News
data class NewsArticle(
    val id: String,
    val stockSymbol: String,
    val title: String,
    val summary: String? = null,
    val source: String,
    val url: String,
    val imageUrl: String? = null,
    val publishedAt: Long
)

// Premium Features
data class PricePrediction(
    val stockSymbol: String,
    val predictedPrice: Double,
    val confidence: Double, // 0-1
    val predictionDate: Long,
    val modelVersion: String
) {
    val confidencePercentage: String get() = "${String.format("%.1f", confidence * 100)}%"
    val formattedPrice: String get() = "₹${String.format("%.2f", predictedPrice)}"
}

data class AlertTemplate(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val alertType: AlertType,
    val basePrice: Double? = null,
    val percentageChange: Double? = null,
    val volumeThreshold: Long? = null
)

// Enums
enum class AlertType(val displayName: String) {
    ABOVE("Price Goes Above"),
    BELOW("Price Falls Below"),
    PERCENTAGE("Percentage Change"),
    VOLUME("Volume Spike"),
    MOVING_AVERAGE("Moving Average Crossover")
}

enum class AlertStatus(val displayName: String) {
    ACTIVE("Active"),
    TRIGGERED("Triggered"),
    SNOOZED("Snoozed")
}

enum class AlertFrequency(val displayName: String, val intervalMinutes: Int) {
    REAL_TIME("Real-time", 1),
    HOURLY("Hourly", 60),
    DAILY("Daily", 1440),
    WEEKLY("Weekly", 10080),
    CUSTOM("Custom", 0)
}

import java.util.Calendar
