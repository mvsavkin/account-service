# Simple Account Service

An account service to transfer money from one account to another. The transferred money must contain two decimal places, the rest will be discarded.

## API

- GET /api/account-service/findById/{id} - if account exist by id then it returns, if not 601 HTTP code returns.

- POST /api/account-service/transfer - transfer money from one account to another.
  - HTTP codes:
    - 200 - success transfer
    - 600 - insufficient funds
    - 601 - account not found
    - 500 - internal server error
    
  - Example JSON: 
 ``{"sourceAccountId": 1,"targetAccountId": 2,"amount": 49.50}`` 
## Configuration
### HTTP Configuration

- "http.server.threads" - Count threads http server's. Default value is 100.
- "http.server.port" - Http port. Default value is 8080.
- "http.server.socket.backlog" - Parameter specifies the number of pending connections the queue will hold. Default value is 0.
- "http.server.stop.delay" - The maximum time in seconds to wait until exchanges have finished.

### Database Configuration

- "db.url" - Database connection URL.
- "db.user" - The database user on whose behalf the connection is being made.
- "db.password" - The database user's password.
- "db.jdbc.driver" - Full class name JDBC driver's
- "db.pool.size" - Connection pool size.

H2 is default embedded database.
