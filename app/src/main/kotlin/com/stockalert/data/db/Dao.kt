package com.stockalert.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity): Long

    @Update
    suspend fun updateAlert(alert: PriceAlertEntity)

    @Delete
    suspend fun deleteAlert(alert: PriceAlertEntity)

    @Query("SELECT * FROM price_alerts WHERE id = :alertId")
    suspend fun getAlertById(alertId: Int): PriceAlertEntity?

    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveAlerts(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE status = 'ACTIVE'")
    suspend fun getActiveAlertsOnce(): List<PriceAlertEntity>

    @Query("SELECT * FROM price_alerts WHERE stockSymbol = :symbol AND status = 'ACTIVE'")
    suspend fun getActiveAlertsByStock(symbol: String): List<PriceAlertEntity>

    @Query("SELECT * FROM price_alerts WHERE alertType = :alertType")
    fun getAlertsByType(alertType: String): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE portfolioId = :portfolioId")
    fun getAlertsByPortfolio(portfolioId: Int): Flow<List<PriceAlertEntity>>

    @Query("UPDATE price_alerts SET status = 'TRIGGERED', triggeredAt = :triggeredTime WHERE id = :alertId")
    suspend fun markAlertAsTriggered(alertId: Int, triggeredTime: Long)

    @Query("UPDATE price_alerts SET status = 'ACTIVE', triggeredAt = NULL WHERE id = :alertId")
    suspend fun resetAlert(alertId: Int)

    @Query("UPDATE price_alerts SET status = 'SNOOZED' WHERE id = :alertId")
    suspend fun snoozeAlert(alertId: Int)

    @Query("DELETE FROM price_alerts WHERE triggeredAt < :cutoffTime")
    suspend fun deleteOldTriggeredAlerts(cutoffTime: Long)
}

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Update
    suspend fun updateStock(stock: StockEntity)

    @Query("SELECT * FROM stocks WHERE symbol = :symbol")
    suspend fun getStockBySymbol(symbol: String): StockEntity?

    @Query("SELECT * FROM stocks ORDER BY symbol ASC")
    suspend fun getAllStocks(): List<StockEntity>

    @Query("SELECT * FROM stocks ORDER BY symbol ASC")
    fun getAllStocksFlow(): Flow<List<StockEntity>>

    @Query("DELETE FROM stocks WHERE lastUpdated < :cutoffTime")
    suspend fun deleteOldStocks(cutoffTime: Long)
}

// Portfolio DAO
@Dao
interface PortfolioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEntity): Long

    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEntity)

    @Delete
    suspend fun deletePortfolio(portfolio: PortfolioEntity)

    @Query("SELECT * FROM portfolios WHERE id = :portfolioId")
    suspend fun getPortfolioById(portfolioId: Int): PortfolioEntity?

    @Query("SELECT * FROM portfolios ORDER BY createdAt DESC")
    fun getAllPortfolios(): Flow<List<PortfolioEntity>>

    @Query("SELECT * FROM portfolios WHERE isDefault = 1")
    suspend fun getDefaultPortfolio(): PortfolioEntity?

    @Query("UPDATE portfolios SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefaultPortfolio()

    @Query("UPDATE portfolios SET totalValue = :totalValue WHERE id = :portfolioId")
    suspend fun updatePortfolioValue(portfolioId: Int, totalValue: Double)
}

// Watchlist DAO
@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity): Long

    @Update
    suspend fun updateWatchlist(watchlist: WatchlistEntity)

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Query("SELECT * FROM watchlists WHERE id = :watchlistId")
    suspend fun getWatchlistById(watchlistId: Int): WatchlistEntity?

    @Query("SELECT * FROM watchlists ORDER BY createdAt DESC")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlists WHERE portfolioId = :portfolioId")
    fun getWatchlistsByPortfolio(portfolioId: Int): Flow<List<WatchlistEntity>>
}

// Watchlist Items DAO
@Dao
interface WatchlistItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItemEntity): Long

    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItemEntity)

    @Query("SELECT * FROM watchlist_items WHERE watchlistId = :watchlistId ORDER BY addedAt DESC")
    fun getWatchlistItems(watchlistId: Int): Flow<List<WatchlistItemEntity>>

    @Query("DELETE FROM watchlist_items WHERE watchlistId = :watchlistId")
    suspend fun clearWatchlist(watchlistId: Int)
}

// Price History DAO
@Dao
interface PriceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceHistory(priceHistory: PriceHistoryEntity)

    @Query("SELECT * FROM price_history WHERE stockSymbol = :symbol ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getPriceHistory(symbol: String, limit: Int = 100): List<PriceHistoryEntity>

    @Query("SELECT * FROM price_history WHERE stockSymbol = :symbol AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getPriceHistoryRange(symbol: String, startTime: Long, endTime: Long): List<PriceHistoryEntity>

    @Query("DELETE FROM price_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOldPriceHistory(cutoffTime: Long)
}

// User Settings DAO
@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettingsEntity)

    @Update
    suspend fun updateSettings(settings: UserSettingsEntity)

    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getSettingsOnce(): UserSettingsEntity?
}

// Notification Settings DAO
@Dao
interface NotificationSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationSettings(settings: NotificationSettingsEntity)

    @Update
    suspend fun updateNotificationSettings(settings: NotificationSettingsEntity)

    @Delete
    suspend fun deleteNotificationSettings(settings: NotificationSettingsEntity)

    @Query("SELECT * FROM notification_settings WHERE stockSymbol = :symbol")
    suspend fun getNotificationSettings(symbol: String): NotificationSettingsEntity?

    @Query("SELECT * FROM notification_settings ORDER BY stockSymbol ASC")
    fun getAllNotificationSettings(): Flow<List<NotificationSettingsEntity>>
}

// News DAO
@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsArticleEntity)

    @Delete
    suspend fun deleteNews(news: NewsArticleEntity)

    @Query("SELECT * FROM news_articles WHERE stockSymbol = :symbol ORDER BY publishedAt DESC LIMIT :limit")
    fun getNewsByStock(symbol: String, limit: Int = 20): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC LIMIT :limit")
    fun getLatestNews(limit: Int = 50): Flow<List<NewsArticleEntity>>

    @Query("DELETE FROM news_articles WHERE fetchedAt < :cutoffTime")
    suspend fun deleteOldNews(cutoffTime: Long)
}

// Price Predictions DAO
@Dao
interface PredictionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PricePredictionEntity)

    @Query("SELECT * FROM price_predictions WHERE stockSymbol = :symbol ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestPrediction(symbol: String): PricePredictionEntity?

    @Query("SELECT * FROM price_predictions WHERE stockSymbol = :symbol ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getPredictionHistory(symbol: String, limit: Int = 10): List<PricePredictionEntity>

    @Query("DELETE FROM price_predictions WHERE createdAt < :cutoffTime")
    suspend fun deleteOldPredictions(cutoffTime: Long)
}

// Alert Templates DAO
@Dao
interface AlertTemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: AlertTemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: AlertTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: AlertTemplateEntity)

    @Query("SELECT * FROM alert_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Int): AlertTemplateEntity?

    @Query("SELECT * FROM alert_templates ORDER BY createdAt DESC")
    fun getAllTemplates(): Flow<List<AlertTemplateEntity>>

    @Query("SELECT * FROM alert_templates WHERE alertType = :alertType")
    fun getTemplatesByType(alertType: String): Flow<List<AlertTemplateEntity>>
}
