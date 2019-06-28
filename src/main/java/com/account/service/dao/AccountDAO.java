package com.account.service.dao;

import com.account.service.model.Account;

import java.math.BigDecimal;

/**
 * DAO layer for Account entity.
 */
public interface AccountDAO {

    /**
     * Transfer money from account to account.
     * @param sourceAccountId id source account.
     * @param targetAccountId id target account
     * @param amount amount transfer money.
     */
    void transfer(long sourceAccountId, long targetAccountId, BigDecimal amount);

    /**
     * Find account by id.
     * @param accountId id account.
     * @return Account.
     */
    Account findById(long accountId);
}
