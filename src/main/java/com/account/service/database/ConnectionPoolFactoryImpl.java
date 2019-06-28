package com.account.service.database;

import com.account.service.config.DbConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolFactoryImpl implements ConnectionPoolFactory {

    private final DbConfiguration configuration;

    public ConnectionPoolFactoryImpl(DbConfiguration configuration) {
        this.configuration = configuration;
        // STEP 1: Register JDBC driver
        try {
            Class.forName(configuration.getJdbcDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pool<Connection> createConnectionPool() throws SQLException {
        return ConnectionPool.create(
                configuration.getDbUrl(),
                configuration.getDbUser(),
                configuration.getDbPassword(),
                configuration.getPoolSize());
    }
}
