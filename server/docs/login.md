# Login docs - Xeonex API

### In application.properties file you need to configure the following lines:
```properties
jose.is.enable.jose=1 // if you want to enable jose encryption
```


## Endpoints

### 1. **Login**

- **URL:** `/auth/login`
- **Method:** POST
- **Description:** Endpoint for user authentication.
- **Request Body:**
    - `login` (string): User's login/username.
    - `password` (string): User's password.
- **Response:**
    - Success (200 OK):
        - `token` (string): JWT token for authentication.
    - Error (401 Unauthorized):
        - `error` (string): Message indicating invalid login or password.
        - `error` (string): Message indicating invalid token.

## Example Usage

```http
POST /auth/login HTTP/1.1
Content-Type: application/json

{
  "login": "example_user",
  "password": "example_password"
}

http```    
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJleGFtcGxlX3VzZXIiLCJpYXQiOjE2NDkxMzY2MzYsImV4cCI6MTY0OTcyMzAzNn0.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
}
```

This is the login endpoint for the Xeonex API. It is used to collect a token for a registered user. The endpoint is `/auth/login` and it accepts a POST request. The request body should contain the user's login and password. If the login and password are correct, the endpoint will return a JWT token that can be used for authentication. If the login or password are incorrect, the endpoint will return an error message. The token can then be used to authenticate the user for other endpoints in the API.

We do not use a register, because we have a double authentication once the frontEnd uses a jose protocol to encrypt the password and send it to the backend, the backend decrypts the password and checks if it is correct, if it is correct it returns a token to the frontEnd, if it is not correct it returns an error message.