package com.account.service.dao;

import com.account.service.config.DbConfiguration;
import com.account.service.database.Pool;
import com.account.service.database.ConnectionPoolFactoryImpl;
import com.account.service.exceptions.AccountNotFoundException;
import com.account.service.exceptions.InsufficientMoneyException;
import com.account.service.model.Account;
import com.account.service.utils.DBUtils;
import org.junit.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

import static org.junit.Assert.*;

public class AccountDAOImplTest {

    private static AccountDAO accountDAO;
    private static Pool<Connection> connectionPool;

    @BeforeClass
    public static void setUp() throws Exception {
        connectionPool = new ConnectionPoolFactoryImpl(new DbConfiguration()).createConnectionPool();
        DBUtils.populateTestData(connectionPool);
        accountDAO = new AccountDAOImpl(connectionPool);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connectionPool.close();
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferToNotExistAccount() {
        accountDAO.transfer(1, 4, new BigDecimal(10.00));
    }

    @Test(expected = InsufficientMoneyException.class)
    public void transferWithoutMoney() {
        accountDAO.transfer(1, 2, new BigDecimal(150.00));
    }

    @Test
    public void transfer() {
        accountDAO.transfer(1, 2, new BigDecimal(50.00));
        Account sourceAccount = accountDAO.findById(1);
        Account targetAccount = accountDAO.findById(2);
        Assert.assertEquals(new BigDecimal(50.00).setScale(2, RoundingMode.DOWN), sourceAccount.getAmount());
        Assert.assertEquals(new BigDecimal(250.00).setScale(2, RoundingMode.DOWN), targetAccount.getAmount());
    }

    @Test
    public void findById() {
        Account account = accountDAO.findById(1);
        assertEquals(1, account.getUserId());
    }

    @Test(expected = AccountNotFoundException.class)
    public void findByIdNotExist() {
        accountDAO.findById(4);
    }
}
