package com.account.service.config;

import static com.account.service.config.DbProperty.*;

public class DbConfiguration {
    private static final int DEFAULT_INITIAL_POOL_SIZE = 10;
    // JDBC driver name and database URL
    private static final String DEFAULT_JDBC_DRIVER = "org.h2.Driver";
    private static final String DEFAULT_DB_URL = "jdbc:h2:~/test";
    // Database credentials
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASS = "";

    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String jdbcDriver;
    private int poolSize;

    public DbConfiguration() {
        String dbUrl = System.getProperty(DB_URL);
        String dbUser = System.getProperty(DB_USER);
        String dbPassword = System.getProperty(DB_PASSWORD);
        String jdbcDriver = System.getProperty(JDBC_DRIVER_PROPERTY);
        String poolSize = System.getProperty(POOL_SIZE_PROPERTY);

        this.dbUrl = dbUrl != null ? dbUrl : DEFAULT_DB_URL;
        this.dbUser = dbUser != null ? dbUser : DEFAULT_USER;
        this.dbPassword = dbPassword != null ? dbPassword : DEFAULT_PASS;
        this.jdbcDriver = jdbcDriver != null ? jdbcDriver : DEFAULT_JDBC_DRIVER;
        this.poolSize = poolSize != null ? Integer.valueOf(poolSize) : DEFAULT_INITIAL_POOL_SIZE;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
