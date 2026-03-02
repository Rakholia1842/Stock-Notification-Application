package com.stockalert.data.api

import com.google.gson.annotations.SerializedName

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

// Request/Response models
data class StockPriceResponse(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val previousClose: Double,
    val percentChange: Double
)
