package com.account.service;

import com.account.service.config.Configuration;
import com.account.service.config.DbConfiguration;
import com.account.service.dao.AccountDAO;
import com.account.service.dao.AccountDAOImpl;
import com.account.service.database.ConnectionPoolFactoryImpl;
import com.account.service.database.Pool;
import com.account.service.http.HttpAccountService;
import com.account.service.service.AccountService;
import com.account.service.service.AccountServiceImpl;
import com.account.service.utils.DBUtils;

import java.sql.Connection;


public class Runner {

    public static void main( String[] args ) throws Exception {
        Configuration configuration = new Configuration();

        DbConfiguration dbConfiguration = configuration.getDbConfiguration();
        Pool<Connection> connectionPool = new ConnectionPoolFactoryImpl(dbConfiguration).createConnectionPool();
        DBUtils.populateTestData(connectionPool);
        AccountDAO accountDAO = new AccountDAOImpl(connectionPool);
        AccountService accountService = new AccountServiceImpl(accountDAO);
        HttpAccountService service = new HttpAccountService(configuration, accountService);
        service.initialize();
        service.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            service.stop();
            try {
                connectionPool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }
}
