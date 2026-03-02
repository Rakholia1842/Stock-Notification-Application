package com.stockalert.data.repository

import com.stockalert.data.api.YahooFinanceApi
import com.stockalert.data.api.StockPriceResponse
import com.stockalert.data.db.PriceAlertDao
import com.stockalert.data.db.PriceAlertEntity
import com.stockalert.data.db.StockDao
import com.stockalert.data.db.StockEntity
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

class StockRepository(
    private val api: YahooFinanceApi,
    private val alertDao: PriceAlertDao,
    private val stockDao: StockDao
) {
    // ===== Stock API Operations =====
    suspend fun getStockPrice(symbol: String): Result<StockPriceResponse> {
        return try {
            val ticker = if (symbol.endsWith(".NS", ignoreCase = true)) {
                symbol
            } else {
                "$symbol.NS"
            }

            val response = api.getStockData(ticker)
            
            if (response.chart.error != null) {
                return Result.failure(Exception("API Error: ${response.chart.error}"))
            }

            val chartResult = response.chart.result?.firstOrNull()
                ?: return Result.failure(Exception("No data available"))

            val meta = chartResult.meta
            val previousClose = meta.previousClose
            val currentPrice = meta.regularMarketPrice
            val percentChange = ((currentPrice - previousClose) / previousClose) * 100

            val stockData = StockPriceResponse(
                symbol = ticker.replace(".NS", ""),
                name = meta.longName ?: symbol,
                currentPrice = currentPrice,
                dayHigh = meta.regularMarketDayHigh,
                dayLow = meta.regularMarketDayLow,
                volume = meta.regularMarketVolume ?: 0L,
                previousClose = previousClose,
                percentChange = percentChange
            )

            // Cache the stock data
            stockDao.insertStock(
                StockEntity(
                    symbol = stockData.symbol,
                    name = stockData.name,
                    currentPrice = stockData.currentPrice,
                    dayHigh = stockData.dayHigh,
                    dayLow = stockData.dayLow,
                    volume = stockData.volume,
                    previousClose = stockData.previousClose
                )
            )

            Result.success(stockData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchStocks(query: String): Result<List<StockPriceResponse>> {
        return try {
            // Support searching by company name or ticker
            val commonStocks = listOf(
                "RELIANCE", "TCS", "INFY", "HDFC", "ICICIBANK", "AXISBANK",
                "LT", "ITC", "HCLTECH", "BAJAJFINSV", "MARUTI", "SUNPHARMA",
                "SBIN", "KOTAKBANK", "WIPRO", "ASIANPAINT", "BAJAJSSMARTFINSERVE",
                "DMART", "NESTLEIND", "POWERGRID", "DRREDDY"
            )

            val matchedStocks = if (query.isEmpty()) {
                commonStocks.take(10)
            } else {
                commonStocks.filter {
                    it.contains(query, ignoreCase = true)
                }.take(10)
            }

            val results = mutableListOf<StockPriceResponse>()
            for (symbol in matchedStocks) {
                when (val stockResult = getStockPrice(symbol)) {
                    is Result -> {
                        val data = stockResult.getOrNull()
                        if (data != null) {
                            results.add(data)
                        }
                    }
                }
            }

            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===== Alert Operations =====
    suspend fun createAlert(
        stockSymbol: String,
        stockName: String,
        targetPrice: Double,
        alertType: String
    ): Result<Long> {
        return try {
            val alert = PriceAlertEntity(
                stockSymbol = stockSymbol,
                stockName = stockName,
                targetPrice = targetPrice,
                alertType = alertType
            )
            val id = alertDao.insertAlert(alert)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAlert(
        alertId: Int,
        targetPrice: Double,
        alertType: String
    ): Result<Unit> {
        return try {
            val alert = alertDao.getAlertById(alertId)
            if (alert != null) {
                alertDao.updateAlert(alert.copy(targetPrice = targetPrice, alertType = alertType))
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAlert(alertId: Int): Result<Unit> {
        return try {
            val alert = alertDao.getAlertById(alertId)
            if (alert != null) {
                alertDao.deleteAlert(alert)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllAlerts(): Flow<List<PriceAlertEntity>> = alertDao.getAllAlerts()

    fun getActiveAlerts(): Flow<List<PriceAlertEntity>> = alertDao.getActiveAlerts()

    suspend fun getActiveAlertsOnce(): List<PriceAlertEntity> = alertDao.getActiveAlertsOnce()

    suspend fun getActiveAlertsByStock(symbol: String): List<PriceAlertEntity> {
        return alertDao.getActiveAlertsByStock(symbol)
    }

    suspend fun markAlertAsTriggered(alertId: Int) {
        alertDao.markAlertAsTriggered(alertId, System.currentTimeMillis())
    }

    suspend fun resetAlert(alertId: Int) {
        alertDao.resetAlert(alertId)
    }

    // Check if alert condition is met
    fun checkAlertCondition(currentPrice: Double, targetPrice: Double, alertType: String): Boolean {
        return when (alertType) {
            "ABOVE" -> currentPrice >= targetPrice
            "BELOW" -> currentPrice <= targetPrice
            else -> false
        }
    }
}
