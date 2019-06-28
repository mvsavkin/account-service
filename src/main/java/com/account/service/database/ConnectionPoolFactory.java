package com.account.service.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory for creation connection pool.
 */
public interface ConnectionPoolFactory {

    /**
     * Create new connection pool.
     * @return new connection pool.
     */
    Pool<Connection> createConnectionPool() throws SQLException;
}
