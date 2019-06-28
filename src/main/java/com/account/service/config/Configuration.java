package com.account.service.config;

public class Configuration {

    private final DbConfiguration dbConfiguration;
    private final HttpServerConfiguration httpServerConfiguration;

    public Configuration(DbConfiguration dbConfiguration, HttpServerConfiguration httpServerConfiguration) {
        this.dbConfiguration = dbConfiguration;
        this.httpServerConfiguration = httpServerConfiguration;
    }

    public Configuration() {
        this.dbConfiguration = new DbConfiguration();
        this.httpServerConfiguration = new HttpServerConfiguration();
    }

    public DbConfiguration getDbConfiguration() {
        return dbConfiguration;
    }

    public HttpServerConfiguration getHttpServerConfiguration() {
        return httpServerConfiguration;
    }
}
