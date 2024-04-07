# 🤖 Bot Controller - Xeonex API

Welcome to the documentation for the Bot Controller endpoint of the Xeonex API! In this section, you'll find detailed information about the `getIndicatorInfo` endpoint.

## Endpoint

### Get Indicator Information

- **URL:** `/gpt/getIndicatorInfo`
- **Method:** GET
- **Description:** Retrieves indicator information for a given currency and time interval.
- **Headers:**
    - `Authorization` (string): User's authorization token.
- **Query Parameters:**
    - `currency` (string): Currency code for which indicator information is required (e.g., "BTC").
    - `interval` (string): Time interval for which indicator information is required (e.g., "1d").
- **Response:**
    - Success (200 OK):
        - JSON containing indicator information for the specified currency and interval.
    - Error (400 Bad Request):
        - Message indicating an invalid time interval.

### Example Usage

```http
GET /gpt/getIndicatorInfo?currency=BTC&interval=1d HTTP/1.1
Authorization: Bearer {token}


HTTP/1.1 200 OK
Content-Type: application/json

{
  "currency": "BTC/USD",
  "backtrack_interval": "1d",
  "rsi": {...},
  "macd": {...},
  "ma": {...},
  "ema": {...},
  "bbands": {...},
  "fibonacciretracement": {...}
}
