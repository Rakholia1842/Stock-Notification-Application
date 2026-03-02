package com.stockalert.data.repository

import com.stockalert.data.db.*
import com.stockalert.domain.model.PriceHistory
import com.stockalert.domain.model.PricePoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing price history and technical indicators
 */
class PriceHistoryRepository(
    private val priceHistoryDao: PriceHistoryDao
) {

    suspend fun insertPriceHistory(
        symbol: String,
        price: Double,
        high: Double,
        low: Double,
        volume: Long,
        dataSource: String = "YAHOO_FINANCE"
    ) {
        priceHistoryDao.insertPriceHistory(
            PriceHistoryEntity(
                stockSymbol = symbol,
                price = price,
                high = high,
                low = low,
                volume = volume,
                dataSource = dataSource
            )
        )
    }

    suspend fun getPriceHistory(
        symbol: String,
        limit: Int = 100
    ): PriceHistory {
        val history = priceHistoryDao.getPriceHistory(symbol, limit)
        val points = history.map { entity ->
            PricePoint(
                timestamp = entity.timestamp,
                price = entity.price,
                high = entity.high,
                low = entity.low,
                volume = entity.volume
            )
        }.sortedBy { it.timestamp }

        val averagePrice = if (points.isNotEmpty()) {
            points.map { it.price }.average()
        } else 0.0

        return PriceHistory(
            stockSymbol = symbol,
            prices = points,
            averagePrice = averagePrice,
            movingAverage = calculateMovingAverages(points)
        )
    }

    suspend fun getPriceHistoryRange(
        symbol: String,
        startTime: Long,
        endTime: Long
    ): PriceHistory {
        val history = priceHistoryDao.getPriceHistoryRange(symbol, startTime, endTime)
        val points = history.map { entity ->
            PricePoint(
                timestamp = entity.timestamp,
                price = entity.price,
                high = entity.high,
                low = entity.low,
                volume = entity.volume
            )
        }.sortedBy { it.timestamp }

        val averagePrice = if (points.isNotEmpty()) {
            points.map { it.price }.average()
        } else 0.0

        return PriceHistory(
            stockSymbol = symbol,
            prices = points,
            averagePrice = averagePrice,
            movingAverage = calculateMovingAverages(points)
        )
    }

    suspend fun deleteOldPriceHistory(retentionDays: Int = 90) {
        val cutoffTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
        priceHistoryDao.deleteOldPriceHistory(cutoffTime)
    }

    suspend fun calculateMovingAverage(
        symbol: String,
        period: Int
    ): List<Double> {
        val history = priceHistoryDao.getPriceHistory(symbol, period * 2)
        val prices = history.map { it.price }

        if (prices.size < period) return emptyList()

        return prices.windowed(period).map { window ->
            window.average()
        }
    }

    private fun calculateMovingAverages(points: List<PricePoint>): List<Double> {
        if (points.size < 5) return emptyList()

        val prices = points.map { it.price }
        val ma5 = prices.takeLast(5).average()
        val ma10 = prices.takeLast(10).average()
        val ma20 = prices.takeLast(20).average()

        return listOf(ma5, ma10, ma20)
    }

    suspend fun getPriceVolatility(
        symbol: String,
        period: Int = 20
    ): Double {
        val history = priceHistoryDao.getPriceHistory(symbol, period)
        val prices = history.map { it.price }

        if (prices.size < 2) return 0.0

        val mean = prices.average()
        val variance = prices.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance)
    }

    suspend fun get52WeekHigh(symbol: String): Double {
        val oneYearAgo = System.currentTimeMillis() - (365 * 24 * 60 * 60 * 1000L)
        val history = priceHistoryDao.getPriceHistoryRange(
            symbol,
            oneYearAgo,
            System.currentTimeMillis()
        )
        return history.maxOfOrNull { it.high } ?: 0.0
    }

    suspend fun get52WeekLow(symbol: String): Double {
        val oneYearAgo = System.currentTimeMillis() - (365 * 24 * 60 * 60 * 1000L)
        val history = priceHistoryDao.getPriceHistoryRange(
            symbol,
            oneYearAgo,
            System.currentTimeMillis()
        )
        return history.minOfOrNull { it.low } ?: 0.0
    }
}
