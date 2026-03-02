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

    @Query("UPDATE price_alerts SET status = 'TRIGGERED', triggeredAt = :triggeredTime WHERE id = :alertId")
    suspend fun markAlertAsTriggered(alertId: Int, triggeredTime: Long)

    @Query("UPDATE price_alerts SET status = 'ACTIVE', triggeredAt = NULL WHERE id = :alertId")
    suspend fun resetAlert(alertId: Int)
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
}
