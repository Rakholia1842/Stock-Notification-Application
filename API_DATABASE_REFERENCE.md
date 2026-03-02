# API & Database Reference (v2.0)

## Supported APIs Overview

### 1. Yahoo Finance API ✅ (Default)

**Status:** Active, No authentication required

#### Endpoint
```
GET https://query1.finance.yahoo.com/v8/finance/chart/{symbol}
```

#### Parameters
- `symbol`: Stock symbol with market suffix (e.g., "RELIANCE.NS" for NSE)
- `interval`: "1d", "1wk", "1mo" for historical data
- `range`: "1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "max"

#### Example Request
```
https://query1.finance.yahoo.com/v8/finance/chart/RELIANCE.NS
```

#### Response Sample
```json
{
  "chart": {
    "result": [{
      "meta": {
        "symbol": "RELIANCE.NS",
        "currency": "INR",
        "regularMarketPrice": 2850.50,
        "regularMarketDayHigh": 2895.00,
        "regularMarketDayLow": 2825.00,
        "regularMarketVolume": 15234567,
        "previousClose": 2830.00,
        "longName": "Reliance Industries Limited"
      },
      "timestamp": [1704067800],
      "indicators": {
        "quote": [{
          "close": [2850.50],
          "open": [2835.00],
          "high": [2895.00],
          "low": [2825.00],
          "volume": [15234567]
        }]
      }
    }],
    "error": null
  }
}
```

#### Data Mapping
| API Field | Domain Model | Type |
|-----------|--------------|------|
| `meta.symbol` | Stock.symbol | String |
| `meta.longName` | Stock.name | String |
| `meta.regularMarketPrice` | Stock.currentPrice | Double |
| `meta.regularMarketDayHigh` | Stock.dayHigh | Double |
| `meta.regularMarketDayLow` | Stock.dayLow | Double |
| `meta.regularMarketVolume` | Stock.volume | Long |
| `meta.previousClose` | Stock.previousClose | Double |
| Calculated | Stock.percentChange | Double |

---

### 2. Alpha Vantage API (Ready for Implementation)

**Status:** 🔄 Ready, Requires API Key

#### Base URL
```
https://www.alphavantage.co/query
```

#### Key Endpoints
```
GET /query?function=GLOBAL_QUOTE&symbol=RELIANCE.NS&apikey={api_key}
GET /query?function=TIME_SERIES_DAILY&symbol=RELIANCE.NS&apikey={api_key}
GET /query?function=TIME_SERIES_INTRADAY&symbol=RELIANCE.NS&interval=5min&apikey={api_key}
```

#### Features
- Real-time quotes
- Daily/Intraday historical data
- Technical indicators
- No per-request caching requirements

---

### 3. IEX Cloud API (Ready for Implementation)

**Status:** 🔄 Ready, Requires API Key

#### Base URL
```
https://cloud.iexapis.com/stable/
```

#### Key Endpoints
```
GET /stock/{symbol}/quote?token={api_key}
GET /stock/{symbol}/chart/1m?token={api_key}
GET /stock/{symbol}/chart/1y?token={api_key}
GET /stock/{symbol}/news/last/10?token={api_key}
```

#### Features
- Real-time data
- 1-month and 1-year historical charts
- News integration
- Fast API responses

---

### 4. Finnhub API (Ready for Implementation)

**Status:** 🔄 Ready, Requires API Key

#### Base URL
```
https://finnhub.io/api/v1/
```

#### Key Endpoints
```
GET /quote?symbol=RELIANCE&token={api_key}
GET /company-news?symbol=RELIANCE&from=2024-01-01&to=2024-01-31&token={api_key}
GET /sentiment/news?symbol=RELIANCE&token={api_key}
```

#### Features
- News sentiment analysis
- Company news feeds
- Real-time quotes
- Insider transactions

---

### 5. Polygon.io API (Ready for Implementation)

**Status:** 🔄 Ready, Requires API Key

#### Base URL
```
https://api.polygon.io/
```

#### Key Endpoints
```
GET /v1/open-close/{ticker}/{date}?adjusted=true&apiKey={api_key}
GET /v2/aggs/ticker/{ticker}/range/1/day/{from}/{to}?apiKey={api_key}
GET /v2/reference/news?query=RELIANCE&apiKey={api_key}
```

#### Features
- Aggregate data
- Historical OHLCV
- News search
- Multiple timeframes

---

### 6. News API (Ready for Implementation)

**Status:** 🔄 Ready, Requires API Key

#### Base URL
```
https://newsapi.org/v2/
```

#### Key Endpoints
```
GET /everything?q=RELIANCE&sortBy=publishedAt&apiKey={api_key}
GET /everything?q=stock+market&sortBy=publishedAt&language=en&apiKey={api_key}
```

#### Features
- News aggregation from 40+ sources
- Various sorting options
- Search functionality
- Real-time news

---

## Database Schema (v2.0)

### Core Entities

#### `stocks` Table
```sql
CREATE TABLE stocks (
    symbol TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    currentPrice REAL NOT NULL,
    dayHigh REAL NOT NULL,
    dayLow REAL NOT NULL,
    volume INTEGER NOT NULL,
    previousClose REAL NOT NULL,
    lastUpdated INTEGER DEFAULT (current_timestamp),
    dataSource TEXT DEFAULT 'YAHOO_FINANCE'
);

CREATE INDEX idx_stocks_symbol ON stocks(symbol);
```

#### `price_alerts` Table (Enhanced v2.0)
```sql
CREATE TABLE price_alerts (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    stockSymbol TEXT NOT NULL,
    stockName TEXT NOT NULL,
    targetPrice REAL NOT NULL,
    alertType TEXT NOT NULL,  -- ABOVE, BELOW, PERCENTAGE, VOLUME, MOVING_AVG
    status TEXT DEFAULT 'ACTIVE',  -- ACTIVE, TRIGGERED, SNOOZED
    createdAt INTEGER NOT NULL,
    triggeredAt INTEGER,
    percentageChange REAL,
    volumeThreshold INTEGER,
    movingAverageDays INTEGER,
    alertFrequency TEXT DEFAULT 'REAL_TIME',
    quietHourStart INTEGER,
    quietHourEnd INTEGER,
    portfolioId INTEGER,
    
    FOREIGN KEY(portfolioId) REFERENCES portfolios(id),
    FOREIGN KEY (stockSymbol) REFERENCES stocks(symbol)
);

CREATE INDEX idx_alerts_status ON price_alerts(status);
CREATE INDEX idx_alerts_stock ON price_alerts(stockSymbol);
CREATE INDEX idx_alerts_portfolio ON price_alerts(portfolioId);
```

### Portfolio Management Entities (NEW)

#### `portfolios` Table
```sql
CREATE TABLE portfolios (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL,
    totalValue REAL DEFAULT 0.0,
    isDefault INTEGER DEFAULT 0
);
```

#### `watchlists` Table
```sql
CREATE TABLE watchlists (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    createdAt INTEGER NOT NULL,
    portfolioId INTEGER,
    
    FOREIGN KEY(portfolioId) REFERENCES portfolios(id)
);
```

#### `watchlist_items` Table
```sql
CREATE TABLE watchlist_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    watchlistId INTEGER NOT NULL,
    stockSymbol TEXT NOT NULL,
    addedAt INTEGER NOT NULL,
    
    FOREIGN KEY(watchlistId) REFERENCES watchlists(id),
    FOREIGN KEY(stockSymbol) REFERENCES stocks(symbol)
);
```

### Historical Data Entities (NEW)

#### `price_history` Table
```sql
CREATE TABLE price_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    stockSymbol TEXT NOT NULL,
    price REAL NOT NULL,
    high REAL NOT NULL,
    low REAL NOT NULL,
    volume INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    dataSource TEXT DEFAULT 'YAHOO_FINANCE',
    
    FOREIGN KEY(stockSymbol) REFERENCES stocks(symbol)
);

CREATE INDEX idx_history_stock_time ON price_history(stockSymbol, timestamp DESC);
```

### Settings & Preferences Entities (NEW)

#### `user_settings` Table
```sql
CREATE TABLE user_settings (
    id INTEGER PRIMARY KEY DEFAULT 1,
    notificationsEnabled INTEGER DEFAULT 1,
    soundEnabled INTEGER DEFAULT 1,
    vibrationEnabled INTEGER DEFAULT 1,
    defaultAlertFrequency TEXT DEFAULT 'REAL_TIME',
    darkModeEnabled INTEGER DEFAULT 0,
    currencySymbol TEXT DEFAULT '₹',
    decimalPlaces INTEGER DEFAULT 2,
    refreshIntervalMinutes INTEGER DEFAULT 15,
    lastUpdated INTEGER
);
```

#### `notification_settings` Table
```sql
CREATE TABLE notification_settings (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    stockSymbol TEXT NOT NULL,
    notificationsEnabled INTEGER DEFAULT 1,
    soundEnabled INTEGER DEFAULT 1,
    vibrationEnabled INTEGER DEFAULT 1,
    emailNotifications INTEGER DEFAULT 0,
    smsNotifications INTEGER DEFAULT 0,
    quietHourStart INTEGER,
    quietHourEnd INTEGER,
    createdAt INTEGER NOT NULL,
    
    UNIQUE(stockSymbol),
    FOREIGN KEY(stockSymbol) REFERENCES stocks(symbol)
);
```

### News & Premium Entities (NEW)

#### `news_articles` Table
```sql
CREATE TABLE news_articles (
    id TEXT PRIMARY KEY NOT NULL,
    stockSymbol TEXT NOT NULL,
    title TEXT NOT NULL,
    summary TEXT,
    source TEXT NOT NULL,
    url TEXT NOT NULL,
    imageUrl TEXT,
    publishedAt INTEGER NOT NULL,
    fetchedAt INTEGER NOT NULL,
    
    FOREIGN KEY(stockSymbol) REFERENCES stocks(symbol)
);

CREATE INDEX idx_news_stock_date ON news_articles(stockSymbol, publishedAt DESC);
```

#### `price_predictions` Table
```sql
CREATE TABLE price_predictions (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    stockSymbol TEXT NOT NULL,
    predictedPrice REAL NOT NULL,
    confidence REAL NOT NULL,
    predictionDate INTEGER NOT NULL,
    modelVersion TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    
    FOREIGN KEY(stockSymbol) REFERENCES stocks(symbol)
);

CREATE INDEX idx_predictions_stock_date ON price_predictions(stockSymbol, createdAt DESC);
```

#### `alert_templates` Table
```sql
CREATE TABLE alert_templates (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    alertType TEXT NOT NULL,
    basePrice REAL,
    percentageChange REAL,
    volumeThreshold INTEGER,
    createdAt INTEGER NOT NULL
);
```

---

## DAO Query Examples

### Get Active Alerts
```kotlin
@Query("SELECT * FROM price_alerts WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
fun getActiveAlerts(): Flow<List<PriceAlertEntity>>
```

### Get Price History for Charts
```kotlin
@Query("SELECT * FROM price_history WHERE stockSymbol = :symbol ORDER BY timestamp DESC LIMIT :limit")
suspend fun getPriceHistory(symbol: String, limit: Int = 100): List<PriceHistoryEntity>
```

### Get Portfolio with Watchlists
```kotlin
@Query("SELECT * FROM portfolios WHERE id = :portfolioId")
suspend fun getPortfolioById(portfolioId: Int): PortfolioEntity?
```

### Get Notification Settings for Stock
```kotlin
@Query("SELECT * FROM notification_settings WHERE stockSymbol = :symbol")
suspend fun getNotificationSettings(symbol: String): NotificationSettingsEntity?
```

### Update Alert Status
```kotlin
@Query("UPDATE price_alerts SET status = 'TRIGGERED', triggeredAt = :triggeredTime WHERE id = :alertId")
suspend fun markAlertAsTriggered(alertId: Int, triggeredTime: Long)
```

---

## Supported Indian Stocks

### NSE Stocks (Sample List)
```
RELIANCE, TCS, INFY, HDFC, ICICIBANK, AXISBANK, LT, ITC, HCLTECH, 
BAJAJFINSV, MARUTI, SUNPHARMA, SBIN, KOTAKBANK, WIPRO, ASIANPAINT, 
DMART, NESTLEIND, POWERGRID, DRREDDY
```

### Stock Symbol Format
- **NSE:** `SYMBOL.NS` (e.g., `RELIANCE.NS`)
- **BSE:** `SYMBOL.BO` (e.g., `RELIANCE.BO`)

---

## Response Codes & Error Handling

### HTTP Status Codes
- `200 OK` - Successful request
- `400 Bad Request` - Invalid parameters
- `401 Unauthorized` - Invalid/missing API key
- `403 Forbidden` - API limit exceeded
- `404 Not Found` - Stock symbol not found
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

### Retry Strategy
```kotlin
val backoffPolicy = BackoffPolicy.EXPONENTIAL
val initialDelay = 15  // minutes
val maxDelay = 2  // hours

// Automatic retry with exponential backoff
WorkRequest.setBackoffPolicy(backoffPolicy, initialDelay, TimeUnit.MINUTES)
```

---

## Performance Metrics

### Database Performance
- Stock lookup: < 1ms (indexed)
- Alert retrieval: < 10ms
- Price history query: < 50ms (for 100 records)
- Portfolio aggregation: < 100ms

### API Response Times
- Yahoo Finance: ~500ms
- Alpha Vantage: ~1000ms
- IEX Cloud: ~300ms
- Finnhub: ~400ms

### Caching Strategy
```kotlin
// Cache Configuration (v2.0)
const val CACHE_DURATION_STOCK_DATA_MINUTES = 15
const val CACHE_DURATION_SEARCH_RESULTS_MINUTES = 30
const val CACHE_DURATION_NEWS_MINUTES = 60
const val CACHE_DURATION_PREDICTIONS_HOURS = 24
```

---

## Data Retention Policies

```
Price History: 90 days (automatic cleanup)
News Articles: 30 days (automatic cleanup)
Price Predictions: 7 days (automatic cleanup)
User Settings: Indefinite
Alerts: Until manually deleted
Portfolio Data: Until manually deleted
```
    stockSymbol TEXT NOT NULL,
    stockName TEXT NOT NULL,
    targetPrice REAL NOT NULL,
    alertType TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    createdAt INTEGER NOT NULL,
    triggeredAt INTEGER
)
```

#### Fields Description

| Field | Type | Description |
|-------|------|-------------|
| `id` | INTEGER | Primary key, auto-increment |
| `stockSymbol` | TEXT | Stock ticker (e.g., "RELIANCE") |
| `stockName` | TEXT | Company name (e.g., "Reliance Industries") |
| `targetPrice` | REAL | Target price in ₹ |
| `alertType` | TEXT | "ABOVE" or "BELOW" |
| `status` | TEXT | "ACTIVE" or "TRIGGERED" |
| `createdAt` | LONG | Unix timestamp of alert creation |
| `triggeredAt` | LONG | Unix timestamp when alert was triggered (NULL if not triggered) |

#### Example Records

```sql
-- Alert: Notify when RELIANCE rises above ₹3000
INSERT INTO price_alerts (
    stockSymbol, stockName, targetPrice, alertType, status, createdAt
) VALUES (
    'RELIANCE', 'Reliance Industries Limited', 3000.00, 'ABOVE', 'ACTIVE', 1704067800
);

-- Alert: Notify when TCS falls below ₹3500
INSERT INTO price_alerts (
    stockSymbol, stockName, targetPrice, alertType, status, createdAt
) VALUES (
    'TCS', 'Tata Consultancy Services', 3500.00, 'BELOW', 'ACTIVE', 1704067900
);

-- Already triggered alert (historic)
INSERT INTO price_alerts (
    stockSymbol, stockName, targetPrice, alertType, status, createdAt, triggeredAt
) VALUES (
    'INFY', 'Infosys Limited', 1500.00, 'ABOVE', 'TRIGGERED', 1704067700, 1704074000
);
```

#### Useful Queries

```sql
-- Get all active alerts
SELECT * FROM price_alerts WHERE status = 'ACTIVE';

-- Get alerts for a specific stock
SELECT * FROM price_alerts WHERE stockSymbol = 'RELIANCE';

-- Get triggers that happened today (last 24 hours)
SELECT * FROM price_alerts 
WHERE status = 'TRIGGERED' 
AND triggeredAt > (strftime('%s','now') - 86400) * 1000;

-- Count alerts by type
SELECT alertType, COUNT(*) as count 
FROM price_alerts 
GROUP BY alertType;

-- Get oldest active alert
SELECT * FROM price_alerts 
WHERE status = 'ACTIVE' 
ORDER BY createdAt ASC 
LIMIT 1;
```

### Table: `stocks`

```sql
CREATE TABLE stocks (
    symbol TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    currentPrice REAL NOT NULL,
    dayHigh REAL NOT NULL,
    dayLow REAL NOT NULL,
    volume INTEGER NOT NULL,
    previousClose REAL NOT NULL,
    lastUpdated INTEGER NOT NULL
)
```

#### Fields Description

| Field | Type | Description |
|-------|------|-------------|
| `symbol` | TEXT | Stock ticker (primary key) |
| `name` | TEXT | Company name |
| `currentPrice` | REAL | Current market price in ₹ |
| `dayHigh` | REAL | Today's high price |
| `dayLow` | REAL | Today's low price |
| `volume` | LONG | Trading volume |
| `previousClose` | REAL | Previous close price |
| `lastUpdated` | LONG | Unix timestamp of last update |

#### Purpose
- **Caching**: Store recently fetched stock data
- **Offline Access**: Show cached data when network unavailable
- **Performance**: Reduce API calls for frequently searched stocks

#### Example Records

```sql
-- Recently fetched stock data
INSERT INTO stocks (
    symbol, name, currentPrice, dayHigh, dayLow, volume, previousClose, lastUpdated
) VALUES (
    'RELIANCE', 'Reliance Industries Limited', 
    2850.50, 2895.00, 2825.00, 15234567, 2830.00, 1704074000
);

INSERT INTO stocks (
    symbol, name, currentPrice, dayHigh, dayLow, volume, previousClose, lastUpdated
) VALUES (
    'TCS', 'Tata Consultancy Services', 
    3920.75, 3950.00, 3880.00, 8456234, 3910.00, 1704074000
);
```

## Data Flow Diagram

```
┌─────────────┐
│   User      │
│  (UI Layer) │
└──────┬──────┘
       │
       ↓
┌─────────────────────────────────────┐
│  ViewModel (HomeViewModel)          │
│  - StateFlow for UI State           │
│  - Handle user interactions         │
└──────┬──────────────────────────────┘
       │
       ↓
┌─────────────────────────────────────┐
│  Repository (StockRepository)       │
│  - Business logic                   │
│  - Data source coordination         │
└──────┬──────────────────────────────┘
       │
       ├─────────────────┬─────────────────┐
       ↓                 ↓                 ↓
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ YahooFinance │  │ Room DB      │  │ Local Cache  │
│ API          │  │ (SQLite)     │  │ (Disk)       │
│              │  │              │  │              │
│ HTTP Request │  │ Persistent   │  │ Runtime      │
│ JSON Response│  │ Storage      │  │ Storage      │
└──────────────┘  └──────────────┘  └──────────────┘
```

## WorkManager Data Flow

```
┌──────────────────────────────────────────┐
│  Device Boot / 15-minute Timer           │
└──────────────┬───────────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  PriceCheckWorker.doWork()          │
│  1. Fetch active alerts from DB     │
│  2. Group by stock symbol           │
│  3. For each stock:                 │
│     a. Fetch current price via API  │
│     b. Compare with target prices   │
│     c. If match → Trigger alert     │
└──────────┬────────────────────────────┘
           ↓
┌──────────────────────────────────────────┐
│  Database Updates                        │
│  - Mark alert as TRIGGERED               │
│  - Set triggeredAt timestamp             │
└──────────┬───────────────────────────────┘
           ↓
┌──────────────────────────────────────────┐
│  Notification Display                    │
│  - Show notification to user             │
│  - Set pending intent to open app        │
└──────────────────────────────────────────┘
```

## Error Responses

### API Error Example

```json
{
  "chart": {
    "result": null,
    "error": {
      "code": "No data in response",
      "description": "The stock symbol appears to be invalid"
    }
  }
}
```

### Handling in Code

```kotlin
// In StockRepository.getStockPrice()
if (response.chart.error != null) {
    return Result.failure(Exception("API Error: ${response.chart.error}"))
}
```

## Performance Metrics

### Database Query Performance

| Query | Indexed | Avg Time |
|-------|---------|----------|
| Get all alerts | Yes | < 5ms |
| Get active alerts | Yes | < 5ms |
| Get alerts by stock | Yes | < 5ms |
| Insert alert | - | < 10ms |
| Update status | - | < 10ms |

### API Response Time

| Condition | Time |
|-----------|------|
| Good network | 200-500ms |
| Slow network | 1-3s |
| Timeout | 30s |

## Migration Guide (if adding columns)

To add a new field to `price_alerts` table:

```kotlin
// 1. Update Entity
data class PriceAlertEntity(
    // ... existing fields
    val newField: String = ""  // Add new field with default
)

// 2. Create migration (in real app)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE price_alerts ADD COLUMN newField TEXT DEFAULT ''"
        )
    }
}

// 3. Update database version
@Database(version = 2, entities = [...], migrations = [MIGRATION_1_2])

// 4. Update DAO queries if needed
```

## Testing Data

### Test Alert Scenarios

```sql
-- Test 1: Alert should trigger (current >= target)
-- Current price of RELIANCE: 2850.50, Target: 2800.00, Type: ABOVE
-- Result: Should trigger immediately

-- Test 2: Alert should not trigger (current < target)
-- Current price of TCS: 3920.75, Target: 4000.00, Type: ABOVE
-- Result: Should not trigger

-- Test 3: Multiple alerts on same stock
-- INFY with 3 ABOVE and 2 BELOW alerts
-- Result: All matching alerts should trigger
```

---

**API Endpoint**: https://query1.finance.yahoo.com/v8/finance/chart/{symbol}.NS

**Database**: SQLite (Room Database)

**Version**: 1.0.0
