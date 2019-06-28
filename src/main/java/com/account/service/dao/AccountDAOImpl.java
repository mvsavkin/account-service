package com.account.service.dao;

import com.account.service.database.Pool;
import com.account.service.exceptions.AccountNotFoundException;
import com.account.service.exceptions.InsufficientMoneyException;
import com.account.service.exceptions.InternalRepositoryException;
import com.account.service.model.Account;
import com.account.service.utils.DBUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.account.service.dao.AccountField.*;

public class AccountDAOImpl implements AccountDAO {

    private static final String UPDATE_ACCOUNT_BALANCE_QUERY = "UPDATE ACCOUNT SET amount = ? WHERE ID = ?";
    private static final String SELECT_AMOUNT_FROM_ACCOUNT_QUERY_FOR_UPDATE = "SELECT * FROM ACCOUNT WHERE ID = ? FOR UPDATE;";
    private static final String SELECT_AMOUNT_FROM_ACCOUNT_QUERY = "SELECT * FROM ACCOUNT WHERE ID = ? FOR UPDATE;";

    private static final int INDEX_ACCOUNT_ID_SELECT_QUERY = 1;
    private static final int INDEX_ACCOUNT_ID_UPDATE_QUERY = 2;
    private static final int INDEX_ACCOUNT_AMOUNT_UPDATE_QUERY = 1;

    private final Pool<Connection> connectionPool;

    public AccountDAOImpl(Pool<Connection> connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void transfer(long sourceAccountId, long targetAccountId, BigDecimal amount) {
        final Connection connection = DBUtils.getConnectionWithTransactionFromPool(connectionPool);
        try (PreparedStatement updateAccountStatement = connection.prepareStatement(UPDATE_ACCOUNT_BALANCE_QUERY);
             PreparedStatement selectAccountStatement = connection.prepareStatement(SELECT_AMOUNT_FROM_ACCOUNT_QUERY_FOR_UPDATE)) {
            System.out.println("Begin transaction...");
            int[] counts;
            //Firstly process code for account with the smallest id for to avoid Deadlock
            if (sourceAccountId < targetAccountId) {
                withdraw(sourceAccountId, amount, updateAccountStatement, selectAccountStatement);
                counts = transfer(targetAccountId, amount, updateAccountStatement, selectAccountStatement);
            } else {
                counts = transfer(targetAccountId, amount, updateAccountStatement, selectAccountStatement);
                withdraw(sourceAccountId, amount, updateAccountStatement, selectAccountStatement);
            }

            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Count update rows: " + counts.length);
            System.out.println("Commit...");
        } catch (AccountNotFoundException | InsufficientMoneyException e) {
            DBUtils.rollbackTransaction(connection);
            throw e;
        } catch (Exception e) {
            DBUtils.rollbackTransaction(connection);
            throw new InternalRepositoryException("Error while working with database.", e);
        } finally {
            connectionPool.release(connection);
        }
    }

    private int[] transfer(long targetAccountId, BigDecimal amount, PreparedStatement updateAccountStatement,
                           PreparedStatement selectAccountStatement) throws SQLException {
        //Get target account
        ResultSet resultSet;
        selectAccountStatement.setLong(INDEX_ACCOUNT_ID_SELECT_QUERY, targetAccountId);
        resultSet = selectAccountStatement.executeQuery();
        BigDecimal targetAccountAmount = getAccountAmount(targetAccountId, resultSet);
        DBUtils.closeResultSet(resultSet);

        //Add to target account
        updateAccountStatement.setLong(INDEX_ACCOUNT_ID_UPDATE_QUERY, targetAccountId);
        updateAccountStatement.setBigDecimal(INDEX_ACCOUNT_AMOUNT_UPDATE_QUERY, targetAccountAmount.add(amount));
        updateAccountStatement.addBatch();
        return updateAccountStatement.executeBatch();
    }

    private void withdraw(long sourceAccountId, BigDecimal amount, PreparedStatement updateAccountStatement, PreparedStatement selectAccountStatement) throws SQLException {
        //Get source account and check difference between amount of account and amount
        selectAccountStatement.setLong(INDEX_ACCOUNT_ID_SELECT_QUERY, sourceAccountId);
        ResultSet resultSet = selectAccountStatement.executeQuery();
        BigDecimal sourceAccountAmount = getAccountAmount(sourceAccountId, resultSet);
        DBUtils.closeResultSet(resultSet);
        int diff = sourceAccountAmount.compareTo(amount);
        if (diff < 0) {
            throw new InsufficientMoneyException("Insufficient funds.");
        }
        //Subtract from source account
        updateAccountStatement.setLong(INDEX_ACCOUNT_ID_UPDATE_QUERY, sourceAccountId);
        updateAccountStatement.setBigDecimal(INDEX_ACCOUNT_AMOUNT_UPDATE_QUERY, sourceAccountAmount.subtract(amount));
        updateAccountStatement.addBatch();
    }

    @Override
    public Account findById(long accountId) {
        Connection connection = DBUtils.getConnectionFromPool(connectionPool);
        try (PreparedStatement statement = connection.prepareStatement(SELECT_AMOUNT_FROM_ACCOUNT_QUERY)) {
            //Subtract from source account
            statement.setLong(INDEX_ACCOUNT_ID_SELECT_QUERY, accountId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong(ID);
                long userId = resultSet.getLong(USER_ID);
                BigDecimal amount = resultSet.getBigDecimal(AMOUNT);
                DBUtils.closeResultSet(resultSet);
                return new Account(id, userId, amount);
            } else {
                DBUtils.closeResultSet(resultSet);
                throw new AccountNotFoundException("Account not found. id: " + accountId);
            }
        } catch (SQLException e) {
            throw new InternalRepositoryException("Error while working with database.", e);
        } finally {
            connectionPool.release(connection);
        }
    }


    private BigDecimal getAccountAmount(long sourceAccountId, ResultSet resultSetSourceAccount) throws SQLException {
        BigDecimal sourceAccountAmount;
        if (resultSetSourceAccount.next()) {
            sourceAccountAmount = resultSetSourceAccount.getBigDecimal("amount");
        } else {
            throw new AccountNotFoundException("Account not found. id: " + sourceAccountId);
        }
        return sourceAccountAmount;
    }
}
