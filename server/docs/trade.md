# 📈 Trade Controller - Xeonex API

Welcome to the documentation for the Trade Controller endpoint of the Xeonex API! In this section, you'll find detailed information about the functionalities provided by the `TradeController`.

## Endpoint

### Open Trade

- **URL:** `/trade/open`
- **Method:** POST
- **Description:** Opens a new trade based on the provided parameters.
- **Headers:**
    - `Authorization` (string): User's authorization token.
- **Request Body:**
    - `risk` (integer): Risk percentage for the trade (between 5 and 80).
    - `spread` (decimal): Spread value in percentage.
    - `asset` (string): Asset for the trade (e.g., "BTC/ETH").
    - `window_money` (string): Time window for the trade (e.g., "1d", "4h", "15m").
    - `initialInvestment` (decimal): Initial investment amount.
    - `takeProfit` (decimal): Take-profit percentage.
    - `stopLoss` (decimal): Stop-loss percentage.
- **Response:**
    - Success (200 OK):
        - JSON containing trade information and status.
    - Error (400 Bad Request):
        - Message indicating the reason for failure.

### Close Trade

- **URL:** `/trade/close/{trade_id}`
- **Method:** POST
- **Description:** Closes an existing trade.
- **Headers:**
    - `Authorization` (string): User's authorization token.
- **Path Parameters:**
    - `trade_id` (string): Identifier of the trade to be closed.
- **Response:**
    - Success (200 OK):
        - Message confirming the closure of the trade.
    - Error (400 Bad Request):
        - Message indicating the reason for failure.

### Activate Trade

- **URL:** `/trade/activate/{trade_id}`
- **Method:** POST
- **Description:** Activates a pending trade.
- **Headers:**
    - `Authorization` (string): User's authorization token.
- **Path Parameters:**
    - `trade_id` (string): Identifier of the trade to be activated.
- **Response:**
    - Success (200 OK):
        - Message confirming the activation of the trade.
    - Error (400 Bad Request):
        - Message indicating the reason for failure.

### Get All Trades

- **URL:** `/trade/all`
- **Method:** GET
- **Description:** Retrieves information about all trades associated with the user.
- **Headers:**
    - `Authorization` (string): User's authorization token.
- **Response:**
    - Success (200 OK):
        - JSON array containing information about all trades.
    - Error (400 Bad Request):
        - Message indicating the reason for failure.

### Get Trade by ID

- **URL:** `/trade/{trade_id}`
- **Method:** GET
- **Description:** Retrieves information about a specific trade based on its ID.
- **Headers:**
    - `Authorization` (string): User's authorization token.
- **Path Parameters:**
    - `trade_id` (string): Identifier of the trade to retrieve.
- **Response:**
    - Success (200 OK):
        - JSON containing information about the specified trade.
    - Error (400 Bad Request):
        - Message indicating the reason for failure.

## Conclusion

You now have access to all the functionalities provided by the Trade Controller endpoint of the Xeonex API. If you encounter any issues or have questions, please refer to this documentation or contact support for assistance. Happy trading!
