package com.stockalert.data.api

import com.google.gson.annotations.SerializedName

// ===== Yahoo Finance Models =====
data class YahooFinanceResponse(
    @SerializedName("chart")
    val chart: Chart
)

data class Chart(
    @SerializedName("result")
    val result: List<ChartResult>?,
    @SerializedName("error")
    val error: String?
)

data class ChartResult(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("timestamp")
    val timestamp: List<Long>?,
    @SerializedName("indicators")
    val indicators: Indicators
)

data class Meta(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("regularMarketPrice")
    val regularMarketPrice: Double,
    @SerializedName("regularMarketDayHigh")
    val regularMarketDayHigh: Double,
    @SerializedName("regularMarketDayLow")
    val regularMarketDayLow: Double,
    @SerializedName("regularMarketVolume")
    val regularMarketVolume: Long?,
    @SerializedName("previousClose")
    val previousClose: Double,
    @SerializedName("longName")
    val longName: String?
)

data class Indicators(
    @SerializedName("quote")
    val quote: List<Quote>?
)

data class Quote(
    @SerializedName("close")
    val close: List<Double?>?,
    @SerializedName("open")
    val open: List<Double?>?,
    @SerializedName("high")
    val high: List<Double?>?,
    @SerializedName("low")
    val low: List<Double?>?,
    @SerializedName("volume")
    val volume: List<Long?>?
)

// ===== Alpha Vantage Models =====
data class AlphaVantageResponse(
    @SerializedName("Global Quote")
    val globalQuote: AlphaVantageQuote?
)

data class AlphaVantageQuote(
    @SerializedName("01. symbol")
    val symbol: String?,
    @SerializedName("05. price")
    val price: String?,
    @SerializedName("03. latest trading day")
    val latestTradingDay: String?,
    @SerializedName("09. change")
    val change: String?,
    @SerializedName("10. change percent")
    val changePercent: String?,
    @SerializedName("04. volume")
    val volume: String?
)

// ===== IEX Cloud Models =====
data class IexQuoteResponse(
    val symbol: String,
    val companyName: String,
    val latestPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val latestVolume: Long,
    val closePrice: Double,
    val changePercent: Double
)

// ===== News Models =====
data class NewsSourceResponse(
    val status: String,
    val articles: List<NewsSourceArticle>?
)

data class NewsSourceArticle(
    val source: ArticleSource,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

data class ArticleSource(
    val id: String?,
    val name: String
)

// ===== Prediction/ML Models =====
data class PredictionResponse(
    val symbol: String,
    val prediction: Double,
    val confidence: Double,
    val horizonDays: Int,
    val modelVersion: String,
    val timestamp: Long
)

// ===== Unified Request/Response models =====
data class StockPriceResponse(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val previousClose: Double,
    val percentChange: Double,
    val dataSource: String = "YAHOO_FINANCE"
)

data class HistoricalDataResponse(
    val symbol: String,
    val dates: List<Long>,
    val prices: List<Double>,
    val volumes: List<Long>,
    val highs: List<Double>,
    val lows: List<Double>
)

data class NewsListResponse(
    val symbol: String,
    val articles: List<NewsArticleResponse>
)

data class NewsArticleResponse(
    val id: String,
    val title: String,
    val summary: String?,
    val source: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: Long
)

// ===== Error Models =====
data class ApiErrorResponse(
    val code: Int,
    val message: String,
    val status: String
)

// ===== Pagination Models =====
data class PaginatedResponse<T>(
    val data: List<T>,
    val timestamp: Long,
    val count: Int,
    val total: Int,
    val hasMore: Boolean
)

