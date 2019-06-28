package com.account.service.utils;

import com.account.service.database.Pool;
import com.account.service.exceptions.InternalRepositoryException;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DBUtils {

    private DBUtils () {
    }

    public static Connection getConnectionFromPool(Pool<Connection> connectionPool){
        Connection connection;
        try {
            connection = connectionPool.get();
        } catch (Exception e) {
            throw new InternalRepositoryException("Error while working with database.", e);
        }
        return connection;
    }

    public static Connection getConnectionWithTransactionFromPool(Pool<Connection> connectionPool){
        Connection connection;
        try {
            connection = connectionPool.get();
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new InternalRepositoryException("Error while working with database.", e);
        }
        return connection;
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Throwable ex) {
                // We don't trust the JDBC driver
                System.err.println("Unexpected error:");
                ex.printStackTrace();
            }
        }
    }

    public static void rollbackTransaction(Connection connection) {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("Rollback...");
    }

    public static void populateTestData(Pool<Connection> connectionPool) {
        Connection connection = connectionPool.get();
        try {
            connection.setAutoCommit(false);
            RunScript.execute(connection, new FileReader("src/test/resources/demo.sql"));
            connection.commit();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
        } finally {
            connectionPool.release(connection);
        }
    }
}
