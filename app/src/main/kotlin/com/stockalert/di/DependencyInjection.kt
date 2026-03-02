package com.stockalert.di

import android.content.Context
import com.stockalert.data.api.*
import com.stockalert.data.db.*
import com.stockalert.data.preferences.SettingsManager
import com.stockalert.data.preferences.NotificationPreferencesRepository
import com.stockalert.data.repository.*
import com.stockalert.notification.NotificationHelper
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
    private const val ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/"
    private const val IEX_CLOUD_BASE_URL = "https://cloud.iexapis.com/stable/"
    private const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
    private const val POLYGON_BASE_URL = "https://api.polygon.io/"
    private const val NEWS_API_BASE_URL = "https://newsapi.org/v2/"

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
    fun provideYahooFinanceRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(YAHOO_FINANCE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideAlphaVantageRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ALPHA_VANTAGE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideIexCloudRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(IEX_CLOUD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideFinnhubRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FINNHUB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providePolygonRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(POLYGON_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideNewsApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NEWS_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideYahooFinanceApi(retrofit: Retrofit): YahooFinanceApi {
        return retrofit.create(YahooFinanceApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAlphaVantageApi(retrofit: Retrofit): AlphaVantageApi {
        return retrofit.create(AlphaVantageApi::class.java)
    }

    @Singleton
    @Provides
    fun provideIexCloudApi(retrofit: Retrofit): IexCloudApi {
        return retrofit.create(IexCloudApi::class.java)
    }

    @Singleton
    @Provides
    fun provideFinnhubApi(retrofit: Retrofit): FinnhubApi {
        return retrofit.create(FinnhubApi::class.java)
    }

    @Singleton
    @Provides
    fun providePolygonApi(retrofit: Retrofit): PolygonApi {
        return retrofit.create(PolygonApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNewsApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
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

    @Singleton
    @Provides
    fun providePortfolioDao(database: StockAlertDatabase) =
        database.portfolioDao()

    @Singleton
    @Provides
    fun provideWatchlistDao(database: StockAlertDatabase) =
        database.watchlistDao()

    @Singleton
    @Provides
    fun provideWatchlistItemDao(database: StockAlertDatabase) =
        database.watchlistItemDao()

    @Singleton
    @Provides
    fun providePriceHistoryDao(database: StockAlertDatabase) =
        database.priceHistoryDao()

    @Singleton
    @Provides
    fun provideUserSettingsDao(database: StockAlertDatabase) =
        database.userSettingsDao()

    @Singleton
    @Provides
    fun provideNotificationSettingsDao(database: StockAlertDatabase) =
        database.notificationSettingsDao()

    @Singleton
    @Provides
    fun provideNewsDao(database: StockAlertDatabase) =
        database.newsDao()

    @Singleton
    @Provides
    fun providePredictionDao(database: StockAlertDatabase) =
        database.predictionDao()

    @Singleton
    @Provides
    fun provideAlertTemplateDao(database: StockAlertDatabase) =
        database.alertTemplateDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Singleton
    @Provides
    fun provideStockRepository(
        api: YahooFinanceApi,
        alertDao: PriceAlertDao,
        stockDao: StockDao
    ): StockRepository {
        return StockRepository(api, alertDao, stockDao)
    }

    @Singleton
    @Provides
    fun providePortfolioRepository(
        portfolioDao: PortfolioDao,
        watchlistDao: WatchlistDao,
        watchlistItemDao: WatchlistItemDao,
        stockDao: StockDao
    ): PortfolioRepository {
        return PortfolioRepository(portfolioDao, watchlistDao, watchlistItemDao, stockDao)
    }

    @Singleton
    @Provides
    fun providePriceHistoryRepository(
        priceHistoryDao: PriceHistoryDao
    ): PriceHistoryRepository {
        return PriceHistoryRepository(priceHistoryDao)
    }

    @Singleton
    @Provides
    fun provideNewsRepository(
        newsDao: NewsDao
    ): NewsRepository {
        return NewsRepository(newsDao)
    }

    @Singleton
    @Provides
    fun providePredictionRepository(
        predictionDao: PredictionDao
    ): PredictionRepository {
        return PredictionRepository(predictionDao)
    }

    @Singleton
    @Provides
    fun provideAlertTemplateRepository(
        alertTemplateDao: AlertTemplateDao
    ): AlertTemplateRepository {
        return AlertTemplateRepository(alertTemplateDao)
    }

    @Singleton
    @Provides
    fun provideAlertConditionService(
        stockRepository: StockRepository,
        priceHistoryRepository: PriceHistoryRepository
    ): AlertConditionService {
        return AlertConditionService(stockRepository, priceHistoryRepository)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    
    @Singleton
    @Provides
    fun provideSettingsManager(
        @ApplicationContext context: Context
    ): SettingsManager {
        return SettingsManager(context)
    }

    @Singleton
    @Provides
    fun provideNotificationPreferencesRepository(
        notificationSettingsDao: NotificationSettingsDao
    ): NotificationPreferencesRepository {
        return NotificationPreferencesRepository(notificationSettingsDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Singleton
    @Provides
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper {
        return NotificationHelper(context)
    }
}
