package com.stockalert.di

import android.content.Context
import com.stockalert.data.api.YahooFinanceApi
import com.stockalert.data.db.StockAlertDatabase
import com.stockalert.data.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val YAHOO_FINANCE_BASE_URL = "https://query1.finance.yahoo.com/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(YAHOO_FINANCE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideYahooFinanceApi(retrofit: Retrofit): YahooFinanceApi {
        return retrofit.create(YahooFinanceApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideStockAlertDatabase(
        @ApplicationContext context: Context
    ): StockAlertDatabase {
        return StockAlertDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun providePriceAlertDao(database: StockAlertDatabase) =
        database.priceAlertDao()

    @Singleton
    @Provides
    fun provideStockDao(database: StockAlertDatabase) =
        database.stockDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideStockRepository(
        api: YahooFinanceApi,
        alertDao: com.stockalert.data.db.PriceAlertDao,
        stockDao: com.stockalert.data.db.StockDao
    ): StockRepository {
        return StockRepository(api, alertDao, stockDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Singleton
    @Provides
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): com.stockalert.notification.NotificationHelper {
        return com.stockalert.notification.NotificationHelper(context)
    }
}
