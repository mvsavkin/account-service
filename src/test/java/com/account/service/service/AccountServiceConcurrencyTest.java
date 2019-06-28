package com.account.service.service;

import com.account.service.config.DbConfiguration;
import com.account.service.dao.AccountDAO;
import com.account.service.dao.AccountDAOImpl;
import com.account.service.database.ConnectionPoolFactoryImpl;
import com.account.service.database.Pool;
import com.account.service.dto.AccountDTO;
import com.account.service.dto.TransferDTO;
import com.account.service.utils.DBUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountServiceConcurrencyTest {

    private static final int CONCURRENCY_THREADS = 10;

    private static AccountService accountService;
    private static Pool<Connection> connectionPool;

    @BeforeClass
    public static void setUp() throws Exception {
        connectionPool = new ConnectionPoolFactoryImpl(new DbConfiguration()).createConnectionPool();
        DBUtils.populateTestData(connectionPool);
        AccountDAO accountDAO = new AccountDAOImpl(connectionPool);
        accountService = new AccountServiceImpl(accountDAO);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connectionPool.close();
    }

    @Test
    public void testTransferConcurrencyAndDeadLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENCY_THREADS);
        CountDownLatch countDownLatch = new CountDownLatch(CONCURRENCY_THREADS * 3);
        for (int i = 0; i < CONCURRENCY_THREADS; i++) {
            executorService.execute(() ->
                transferTestMoney(countDownLatch, new TransferDTO(1, 2, new BigDecimal(10.00))));
            executorService.execute(() ->
                transferTestMoney(countDownLatch, new TransferDTO(2, 3, new BigDecimal(10.00))));
            executorService.execute(() ->
                transferTestMoney(countDownLatch, new TransferDTO(3, 1, new BigDecimal(10.00))));
        }
        countDownLatch.await();

        AccountDTO sourceAccount = accountService.findById(1);
        Assert.assertEquals(new BigDecimal(100.00).setScale(2, RoundingMode.DOWN), sourceAccount.getAmount());
    }

    private void transferTestMoney(CountDownLatch countDownLatch, TransferDTO transferDTO) {
        try {
            accountService.transfer(transferDTO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }

}
