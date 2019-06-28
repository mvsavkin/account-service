package com.account.service.service;

import com.account.service.dao.AccountDAO;
import com.account.service.dto.AccountDTO;
import com.account.service.dto.TransferDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public void transfer(TransferDTO transferDTO) {
        BigDecimal amount = transferDTO.getAmount().setScale(2, RoundingMode.DOWN);
        accountDAO.transfer(transferDTO.getSourceAccountId(), transferDTO.getTargetAccountId(), amount);
    }

    @Override
    public AccountDTO findById(long accountId) {
        return new AccountDTO(accountDAO.findById(accountId));
    }
}
