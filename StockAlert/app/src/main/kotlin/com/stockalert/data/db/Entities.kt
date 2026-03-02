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
    val alertType: String, // "ABOVE" or "BELOW"
    val status: String = "ACTIVE", // "ACTIVE" or "TRIGGERED"
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null
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
    val lastUpdated: Long = System.currentTimeMillis()
)
