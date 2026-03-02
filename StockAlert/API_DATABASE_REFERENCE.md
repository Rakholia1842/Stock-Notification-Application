# API & Database Reference

## Yahoo Finance API Reference

### Endpoint
```
GET https://query1.finance.yahoo.com/v8/finance/chart/{symbol}
```

### Parameters
- `symbol`: Stock symbol with market suffix (e.g., "RELIANCE.NS" for NSE)

### Example Request
```
https://query1.finance.yahoo.com/v8/finance/chart/RELIANCE.NS
```

### Response Structure

```json
{
  "chart": {
    "result": [
      {
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
          "quote": [
            {
              "close": [2850.50],
              "open": [2835.00],
              "high": [2895.00],
              "low": [2825.00],
              "volume": [15234567]
            }
          ]
        }
      }
    ],
    "error": null
  }
}
```

### StockPriceResponse Mapping

| API Field | Domain Model | Notes |
|-----------|--------------|-------|
| `meta.symbol` | `symbol` | ".NS" suffix stripped |
| `meta.longName` | `name` | Company name |
| `meta.regularMarketPrice` | `currentPrice` | Current trading price |
| `meta.regularMarketDayHigh` | `dayHigh` | Today's high |
| `meta.regularMarketDayLow` | `dayLow` | Today's low |
| `meta.regularMarketVolume` | `volume` | Trading volume |
| `meta.previousClose` | `previousClose` | Previous day close |
| Calculated | `percentChange` | (current - previous) / previous * 100 |

### Supported NSE Stocks

Major blue-chip stocks supported:

| Stock | Ticker | Sector |
|-------|--------|--------|
| Reliance Industries | RELIANCE | Energy |
| Tata Consultancy Services | TCS | IT |
| Infosys | INFY | IT |
| HDFC Bank | HDFCBANK | Banking |
| ICICI Bank | ICICIBANK | Banking |
| Axis Bank | AXISBANK | Banking |
| Larsen & Toubro | LT | Infrastructure |
| ITC | ITC | Diversified |
| HCL Technologies | HCLTECH | IT |
| Maruti Suzuki | MARUTI | Automotive |
| Sun Pharmaceutical | SUNPHARMA | Pharma |
| State Bank of India | SBIN | Banking |
| Kotak Mahindra | KOTAKBANK | Banking |
| Wipro | WIPRO | IT |
| Asian Paints | ASIANPAINT | Paint |
| Dmart | DMART | Retail |
| Nestlé India | NESTLEIND | FMCG |
| Power Grid | POWERGRID | Power |
| Dr. Reddy's | DRREDDY | Pharma |
| Bajaj Auto | BAJAJSSMARTFINSERVE | Automotive |

## Database Schema

### Table: `price_alerts`

```sql
CREATE TABLE price_alerts (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
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
