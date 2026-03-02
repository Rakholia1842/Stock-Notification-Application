# StockAlert - Advanced Indian Stock Price Tracking App

A complete, feature-rich Android application built with Kotlin and Jetpack Compose that allows users to track Indian stock prices from NSE/BSE with advanced portfolio management, sophisticated alert conditions, historical analysis, and push notifications.

## Version 2.0 - Enhanced Features

### Core Features (v1)

#### 1. **Stock Search** 
- Search Indian stocks by company name or NSE ticker symbol
- Real-time stock data from Yahoo Finance API (no API key required)
- Display results with current price in INR, percentage change (color-coded)
- Support for 20+ major Indian stocks (RELIANCE, TCS, INFY, HDFC, etc.)

#### 2. **Stock Details View**
- Comprehensive stock information:
  - Current market price in ₹
  - Day High/Low
  - Trading Volume
  - Previous Close
  - Daily percentage change
  - Historical price data
  - Moving averages and volatility indicators
  - Related news articles
- One-click "Set Price Alert" button

#### 3. **Basic Price Alert System**
- Create custom price alerts with:
  - Target price specification
  - Alert type: "Price Goes Above" or "Price Falls Below"
  - Persistent local storage via Room Database
  - Triggered/Active status tracking

#### 4. **Background Price Monitoring**
- Android WorkManager scheduled every 15 minutes
- Automatically fetches latest prices for all active alerts
- Condition evaluation: checks if current price meets target criteria
- WorkManager persists across device reboots (BOOT_COMPLETED receiver)

#### 5. **Push Notifications**
- Local notifications triggered when alert conditions are met
- Rich notification content with stock name, current price, and target
- Tap notification to open app and view stock details
- Per-stock notification preferences with quiet hours

#### 6. **My Alerts Screen**
- View all active and triggered alerts
- Display: stock name, target price, current price, alert type, status
- Delete, reset, or snooze alerts
- Manage multiple alerts efficiently

---

### New Features (v2) - High Priority

#### 7. **Advanced Alert Conditions** ✨
- **Multiple alert types:**
  - Price Above/Below threshold
  - Percentage change alerts (e.g., ±5%)
  - Volume spike detection (unusual trading activity)
  - Moving average crossovers (technical analysis)
- **Alert frequency options:**
  - Real-time (immediate notification)
  - Hourly batching
  - Daily digest
  - Weekly summary
- **Quiet hours scheduling:**
  - Customize silence periods (e.g., 10 PM - 8 AM)
  - Per-stock notification control

#### 8. **Portfolio Management** 💼
- **Multiple portfolios:**
  - Create unlimited portfolios
  - Name and organize by strategy/sector
  - Set default portfolio
  - Track portfolio value
- **Watchlists:**
  - Create watchlists within portfolios
  - Organize stocks by theme (Tech, Banking, Index, etc.)
  - Quick access to favorite stocks
  - Share portfolio insights
- **Portfolio analytics:**
  - Total portfolio value calculation
  - Sector allocation
  - Performance tracking

#### 9. **Advanced User Preferences** ⚙️
- **Global settings:**
  - Dark mode support
  - Currency selection (INR, USD, EUR, etc.)
  - Decimal places for precision
  - App refresh intervals (5, 15, 30, 60 min)
  - Default data source selection
- **Notification preferences:**
  - Toggle notifications per stock
  - Sound and vibration control
  - Email notification option (ready for backend)
  - SMS notification option (ready for backend)
  - Quiet hours per stock
- **Data persistence:**
  - DataStore-based local storage
  - Automatic synchronization
  - Cloud sync ready (Firebase integration)

#### 10. **Price History & Technical Analysis** 📊
- **Historical data tracking:**
  - 90+ days of price history per stock
  - Price points with OHLCV data
  - Automatic data cleanup
- **Technical indicators:**
  - Moving averages (5, 10, 20, 50, 200-day)
  - Price volatility calculation
  - 52-week high/low tracking
  - Trend analysis
- **Chart support:**
  - Ready for MPAndroidChart integration
  - Date range queries
  - Multi-timeframe analysis

---

### New Features (v2) - Medium Priority

#### 11. **Multi-Source Data Integration** 🔗
- **Supported data sources:**
  - Yahoo Finance (Primary)
  - Alpha Vantage
  - IEX Cloud
  - Finnhub
  - Polygon.io
- **Features:**
  - Automatic source switching
  - Fallback mechanisms
  - Data source configuration
  - API key management

#### 12. **News Integration** 📰
- **News feeds:**
  - Stock-specific news articles
  - Latest market news
  - Multi-source aggregation
  - News API integration ready
- **Features:**
  - Article summaries with links
  - Source attribution
  - Publication date/time
  - Image thumbnails
  - Search within news

#### 13. **Real-time Price Updates** ⚡
- **WebSocket support (ready for implementation):**
  - Live price streaming
  - Minimal latency updates
  - Connection management
  - Fallback to polling

---

### New Features (v2) - Premium

#### 14. **AI-Powered Price Predictions** 🤖
- **ML model integration:**
  - 7-day price forecasts
  - Confidence scoring (0-100%)
  - Model versioning
  - Historical prediction accuracy
- **Prediction history:**
  - Track prediction accuracy over time
  - Compare model versions
  - Export predictions

#### 15. **Custom Alert Templates** 📋
- **Pre-built templates:**
  - 5% price change alert
  - 10% price change alert
  - Volume spike alert
  - Moving average crossover
- **Features:**
  - Save custom templates
  - Quick alert creation from templates
  - Template sharing
  - Template management

#### 16. **Advanced Notifications** 📧
- **Multi-channel delivery (ready for backend):**
  - Push notifications
  - Email alerts
  - SMS notifications
  - In-app notifications
- **Smart batching:**
  - Prevent notification fatigue
  - Batch similar alerts
  - Customizable batch windows

#### 17. **Export & Reporting** 📄
- **Data export:**
  - CSV export of alerts
  - Portfolio reports
  - Performance analytics
  - PDF generation
- **Features:**
  - Email reports
  - Scheduled reports
  - Custom date ranges


## Tech Stack

```
Language:              Kotlin
UI:                    Jetpack Compose
Architecture:          MVVM (ViewModel + StateFlow/Flow)
Local Database:        Room Database (v2 with 11 entities)
Data Persistence:      DataStore (preferences & settings)
Networking:            Retrofit + OkHttp + Gson
Background Tasks:      Android WorkManager
Dependency Injection:  Hilt (Dagger)
Notifications:         NotificationManager/NotificationCompat
APIs Supported:        Yahoo Finance, Alpha Vantage, IEX Cloud, Finnhub, 
                       Polygon.io, News API (Ready for Firebase)
Build System:          Gradle (Kotlin DSL)
```

## Enhanced Project Structure

```
com.stockalert/
├── data/
│   ├── api/
│   │   ├── ApiModels.kt              (Multi-source API response DTOs)
│   │   └── YahooFinanceApi.kt        (Multiple API interfaces)
│   │
│   ├── db/
│   │   ├── Entities.kt               (11 Room entities + new)
│   │   ├── Dao.kt                    (11 DAOs for enhanced functionality)
│   │   └── StockAlertDatabase.kt     (Updated Room database v2)
│   │
│   ├── preferences/
│   │   ├── SettingsManager.kt        (NEW: Global preferences)
│   │   └── NotificationPreferencesRepository.kt (NEW: Per-stock notifications)
│   │
│   ├── provider/
│   │   └── StockDataProvider.kt      (NEW: Content provider)
│   │
│   └── repository/
│       ├── StockRepository.kt        (Core stock operations)
│       ├── PortfolioRepository.kt    (NEW: Portfolio management)
│       ├── PriceHistoryRepository.kt (NEW: Historical data & indicators)
│       └── ExtendedRepositories.kt   (NEW: News, Predictions, Templates)
│
├── domain/
│   └── model/
│       └── Models.kt                 (Enhanced with 10+ new models)
│
├── ui/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── detail/
│   │   ├── DetailScreen.kt
│   │   └── DetailViewModel.kt
│   ├── alerts/
│   │   ├── AlertsScreen.kt
│   │   └── AlertsViewModel.kt
│   ├── portfolio/               (NEW: Portfolio screens - ready for UI)
│   │   ├── PortfolioScreen.kt
│   │   └── PortfolioViewModel.kt
│   ├── settings/                (NEW: Settings screens - ready for UI)
│   │   ├── SettingsScreen.kt
│   │   └── SettingsViewModel.kt
│   ├── components/
│   │   └── StockComponents.kt
│   └── theme/
│       └── Theme.kt
│
├── worker/
│   ├── PriceCheckWorker.kt
│   └── BootCompletedReceiver.kt   (NEW: Device boot handling)
│
├── widget/
│   └── StockAlertWidgetProvider.kt (NEW: Widget support)
│
├── notification/
│   └── NotificationHelper.kt
│
├── di/
│   └── DependencyInjection.kt    (Enhanced with 20+ providers)
│
├── utils/
│   ├── AppConstants.kt           (Enhanced with 100+ new constants)
│   └── Extensions.kt
│
├── MainActivity.kt
└── StockAlertApp.kt
```

## Database Schema (v2.0)

### Core Entities
- `StockEntity` - Stock price data (updated with dataSource)
- `PriceAlertEntity` - Alert configurations (enhanced with multiple conditions)

### New Portfolio Entities
- `PortfolioEntity` - User portfolios
- `WatchlistEntity` - Watchlists within portfolios
- `WatchlistItemEntity` - Stocks in watchlists

### New Data Entities
- `PriceHistoryEntity` - Historical OHLCV data
- `NewsArticleEntity` - Stock news articles
- `PricePredictionEntity` - ML model predictions

### New Settings Entities
- `UserSettingsEntity` - Global app preferences
- `NotificationSettingsEntity` - Per-stock notification preferences
- `AlertTemplateEntity` - Saved alert templates

## API Integration Status

| Data Source | Status | Features |
|------------|--------|----------|
| **Yahoo Finance** | ✅ Active | Quotes, History, Volume |
| **Alpha Vantage** | 🔄 Ready | Intraday, Daily, Weekly |
| **IEX Cloud** | 🔄 Ready | Real-time, Historical, News |
| **Finnhub** | 🔄 Ready | News, Sentiment |
| **Polygon.io** | 🔄 Ready | OHLCV, Aggregates |
| **News API** | 🔄 Ready | News feeds, Headlines |
| **Firebase** | 🔄 Ready | Auth, Cloud Sync |
| **Email/SMS** | 🔄 Ready | Backend required |
```
├── worker/
│   └── PriceCheckWorker.kt        (WorkManager background task)
├── notification/
│   └── NotificationHelper.kt      (Notification management)
├── di/
│   └── DependencyInjection.kt     (Hilt modules)
├── MainActivity.kt                 (Main activity with navigation)
└── StockAlertApp.kt               (Application class)
```

## API Integration

### Yahoo Finance
- **Endpoint**: `https://query1.finance.yahoo.com/v8/finance/chart/{SYMBOL}.NS`
- **Key Point**: Append `.NS` to NSE stock symbols (e.g., RELIANCE.NS)
- **No authentication required**
- **Rate limiting**: Implement exponential backoff for retries

### Supported Stocks
The app includes a predefined list of major Indian stocks:
- RELIANCE, TCS, INFY, HDFC, ICICIBANK, AXISBANK
- LT, ITC, HCLTECH, MARUTI, SUNPHARMA, SBIN
- KOTAKBANK, WIPRO, ASIANPAINT, DMART, NESTLEIND
- And more...

## Database Schema

### PriceAlertEntity
```sql
CREATE TABLE price_alerts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    stockSymbol TEXT NOT NULL,
    stockName TEXT NOT NULL,
    targetPrice REAL NOT NULL,
    alertType TEXT NOT NULL,  -- "ABOVE" or "BELOW"
    status TEXT NOT NULL DEFAULT "ACTIVE",  -- "ACTIVE" or "TRIGGERED"
    createdAt LONG NOT NULL,
    triggeredAt LONG
)
```

### StockEntity
```sql
CREATE TABLE stocks (
    symbol TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    currentPrice REAL NOT NULL,
    dayHigh REAL NOT NULL,
    dayLow REAL NOT NULL,
    volume LONG NOT NULL,
    previousClose REAL NOT NULL,
    lastUpdated LONG NOT NULL
)
```

## Android Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Background Task Details

### WorkManager Setup
- **Frequency**: 15-minute periodic checks
- **Flex Interval**: 5 minutes
- **Persistence**: Survives app restart and device reboot
- **Efficiency**: Grouped by stock symbol to minimize API calls

### Execution Flow
1. **On Device Boot**: WorkManager restores all scheduled tasks
2. **Every 15 Minutes**: PriceCheckWorker runs
3. **Fetch Prices**: Gets latest prices for all monitored stocks
4. **Evaluate Conditions**: Check if current price meets alert targets
5. **Trigger Notifications**: Show notification if condition met
6. **Mark Triggered**: Update alert status in database
7. **Stop Monitoring**: Triggered alerts no longer checked

## Notifications

### Notification Features
- **Channel**: High priority notification channel
- **Content**:
  - Title: "[STOCK_NAME] Alert!"
  - Body: "Price: ₹[CURRENT] (Target: ₹[TARGET])"
  - Expanded: Detailed alert information
- **Actions**: Tap to open app and navigate to stock
- **Persistence**: Survives app restart

### Android 13+ Support
- Requests `POST_NOTIFICATIONS` permission at runtime
- User can grant/deny in app settings
- Gracefully handles permission denial

## UI/UX Design

### Material Design 3
- Modern Material Design 3 components
- Dynamic color theming
- Light and dark mode support
- Responsive layouts for all screen sizes

### Navigation
- Bottom navigation bar for main screens
- Back navigation from detail screens
- Deep linking support for notifications

## Build & Run

### Prerequisites
- Android Studio (2022.1+)
- Kotlin 1.9.0+
- Gradle 8.0+
- Minimum SDK: API 24
- Target SDK: API 34

### Building
```bash
# Clean and build
./gradlew clean build

# Run on emulator/device
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

### Gradle Dependencies Summary
- AndroidX: Core, AppCompat, Activity, Lifecycle
- Jetpack Compose: UI, Material3, Navigation
- Room: Database with Flow support
- Retrofit + OkHttp: Network calls
- Hilt: Dependency injection
- WorkManager: Background tasks
- Material3: UI components

## Error Handling

### Network Errors
- Graceful error messages to user
- Retry functionality built-in
- Offline support (view saved alerts)

### API Rate Limiting
- Exponential backoff retry strategy
- Grouped API calls minimize requests
- Cache stock data in Room DB

### Invalid Inputs
- Input validation for target prices
- Empty state handling
- Network state detection

## Performance Considerations

1. **Database**: Indexed queries on symbol and status
2. **Network**: Connection pooling via OkHttp
3. **WorkManager**: Efficient background job scheduling
4. **Compose**: State hoisting and recomposition optimization
5. **Memory**: Proper coroutine cleanup in ViewModels

## Testing (Manual)

1. **Search Functionality**
   - Search by company name (e.g., "Reliance")
   - Search by ticker (e.g., "INFY")
   - Verify result accuracy

2. **Alert Creation**
   - Create "Price Goes Above" alert
   - Create "Price Falls Below" alert
   - Verify saved to database

3. **Background Monitoring**
   - Enable WorkManager logging
   - Observe 15-minute task execution
   - Verify price fetching

4. **Notifications**
   - Manually trigger alert condition
   - Verify notification appears
   - Tap notification, verify app opens

5. **Offline Support**
   - Disable network
   - View My Alerts screen
   - Re-enable network

## Future Enhancements

- [ ] Portfolio tracking (buy/sell price tracking)
- [ ] Advanced charting with price history
- [ ] Multiple alert thresholds per stock
- [ ] Custom notification sounds
- [ ] Alert frequency customization
- [ ] Export alerts to CSV
- [ ] Push notifications via Firebase Cloud Messaging
- [ ] Multi-market support (BSE, NSE, MCX)
- [ ] Stock watchlist feature
- [ ] Real-time price streaming (WebSocket)

## Known Limitations

1. **API Rate Limiting**: Yahoo Finance has undocumented rate limits
2. **Market Hours**: Prices update only during market hours (9:15 AM - 3:30 PM IST)
3. **Network Dependency**: Background tasks require internet connection
4. **Notification Delay**: Up to 15 minutes between condition met and notification

## Troubleshooting

### WorkManager Not Running
- Verify RECEIVE_BOOT_COMPLETED permission in manifest
- Check device battery settings (not in aggressive doze mode)
- Ensure app is not force-stopped

### Notifications Not Showing
- Check POST_NOTIFICATIONS permission for Android 13+
- Verify notification channel is created
- Check app settings > Notifications

### Stock Data Not Updating
- Verify internet connection
- Check if market is open (9:15 AM - 3:30 PM IST)
- Try manual search to test API connectivity

## License

This project is provided as-is for educational purposes.

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review logcat output for errors
3. Verify all permissions are granted
4. Ensure minimum API level 24

---

**Built with ❤️ using Android Studio and Kotlin**
