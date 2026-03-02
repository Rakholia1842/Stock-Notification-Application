package com.stockalert.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface YahooFinanceApi {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getStockData(@Path("symbol") symbol: String): YahooFinanceResponse
}
