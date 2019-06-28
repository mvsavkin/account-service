package com.account.service.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool extends BasicPool<Connection> {

    private ConnectionPool(List<Connection> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public static BasicPool<Connection> create(String url, String user, String password, int poolSize) throws SQLException {

        List<Connection> pool = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.add(createConnection(url, user, password));
        }
        return new ConnectionPool(pool);
    }


    private static Connection createConnection(
            String url, String user, String password)
            throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
