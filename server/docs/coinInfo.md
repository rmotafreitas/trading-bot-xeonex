# 🪙 Coin Info Controller - Xeonex API

Welcome to the Coin Info Controller documentation of the Xeonex API! This section will guide you through the various endpoints related to cryptocurrency information retrieval.

## Endpoints

### 1. **Get Currency List**
- **URL:** `/coin/currency`
- **Method:** GET
- **Description:** Retrieves a list of supported currencies.
- **Response:**
    - Success (200 OK):
        - List of CurrencyDTO objects containing currency name and code.

### 2. **Get Coin Price**
- **URL:** `/coin/{pair}`
- **Method:** GET
- **Description:** Retrieves the current price of a cryptocurrency pair.
- **Request Parameters:**
    - `pair` (string): Cryptocurrency pair (e.g., BTC-USD).
- **Headers:**
    - `Authorization` (string): Bearer token for user authentication.
- **Response:**
    - Success (200 OK):
        - JSON object containing cryptocurrency price information.

### 3. **Get Coin Price Chart**
- **URL:** `/coin/{pair}/chart`
- **Method:** GET
- **Description:** Retrieves the price chart data for a cryptocurrency pair.
- **Request Parameters:**
    - `pair` (string): Cryptocurrency pair (e.g., BTC-USD).
    - `interval` (string, optional, default: "1d"): Chart interval (e.g., "1d", "1h").
    - `type` (string, optional, default: "candle"): Chart type ("candle" or "line").
- **Headers:**
    - `Authorization` (string): Bearer token for user authentication.
- **Response:**
    - Success (200 OK):
        - Candlestick or line chart data depending on the specified type.

### 4. **Get Available Pairs**
- **URL:** `/coin/pairs`
- **Method:** GET
- **Description:** Retrieves a list of available cryptocurrency pairs.
- **Response:**
    - Success (200 OK):
        - List of available cryptocurrency pairs.

### 5. **Get Possible Day Types**
- **URL:** `/coin/timeTypes`
- **Method:** GET
- **Description:** Retrieves a list of possible time intervals for price data.
- **Response:**
    - Success (200 OK):
        - List of possible time intervals.

That's it! You're now ready to explore and retrieve cryptocurrency information using the Coin Info Controller endpoints of the Xeonex API. If you encounter any issues, please refer to the provided documentation and error messages.
