package com.stockalert.data.api

import retrofit2.http.*

// ===== Yahoo Finance API =====
interface YahooFinanceApi {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getStockData(@Path("symbol") symbol: String): YahooFinanceResponse

    @GET("v8/finance/chart/{symbol}")
    suspend fun getHistoricalData(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String = "1d",
        @Query("range") range: String = "1y"
    ): YahooFinanceResponse

    @GET("v10/finance/quoteSummary/{symbol}")
    suspend fun getQuoteSummary(@Path("symbol") symbol: String): Map<String, Any>
}

// ===== Alpha Vantage API =====
interface AlphaVantageApi {
    @GET("query")
    suspend fun getQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): AlphaVantageResponse

    @GET("query")
    suspend fun getTimeSeries(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String,
        @Query("outputsize") outputSize: String = "full"
    ): Map<String, Any>

    @GET("query")
    suspend fun getIntraday(
        @Query("function") function: String = "TIME_SERIES_INTRADAY",
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "5min",
        @Query("apikey") apiKey: String
    ): Map<String, Any>
}

// ===== IEX Cloud API =====
interface IexCloudApi {
    @GET("stock/{symbol}/quote")
    suspend fun getQuote(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): IexQuoteResponse

    @GET("stock/{symbol}/chart/1m")
    suspend fun getMonthlyChart(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): List<Map<String, Any>>

    @GET("stock/{symbol}/chart/1y")
    suspend fun getYearlyChart(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): List<Map<String, Any>>

    @GET("stock/{symbol}/news/last/10")
    suspend fun getNews(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): List<Map<String, Any>>
}

// ===== Finnhub API =====
interface FinnhubApi {
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Map<String, Any>

    @GET("company-news")
    suspend fun getNews(
        @Query("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("token") token: String
    ): List<Map<String, Any>>

    @GET("sentiment/news")
    suspend fun getNewsSentiment(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Map<String, Any>
}

// ===== Polygon.io API =====
interface PolygonApi {
    @GET("v1/open-close/{stocksTicker}/{date}")
    suspend fun getOpenClose(
        @Path("stocksTicker") symbol: String,
        @Path("date") date: String,
        @Query("adjusted") adjusted: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Map<String, Any>

    @GET("v2/aggs/ticker/{stocksTicker}/range/1/day/{from}/{to}")
    suspend fun getAggregates(
        @Path("stocksTicker") symbol: String,
        @Path("from") from: String,
        @Path("to") to: String,
        @Query("apiKey") apiKey: String
    ): Map<String, Any>

    @GET("v2/reference/news")
    suspend fun getNews(
        @Query("query") query: String,
        @Query("apiKey") apiKey: String
    ): Map<String, Any>
}

// ===== News API =====
interface NewsApi {
    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 50,
        @Query("apiKey") apiKey: String
    ): NewsSourceResponse

    @GET("top-headlines")
    suspend fun getHeadlines(
        @Query("category") category: String = "business",
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 30,
        @Query("apiKey") apiKey: String
    ): NewsSourceResponse
}

// ===== Generic Stock API Service Interface =====
interface StockDataService {
    suspend fun getStockPrice(symbol: String): Result<StockPriceResponse>
    suspend fun getHistoricalData(symbol: String, days: Int = 365): Result<HistoricalDataResponse>
    suspend fun searchStocks(query: String): Result<List<StockPriceResponse>>
    suspend fun getNews(symbol: String): Result<NewsListResponse>
}
