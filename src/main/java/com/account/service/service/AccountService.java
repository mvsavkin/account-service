package com.account.service.service;

import com.account.service.dto.AccountDTO;
import com.account.service.dto.TransferDTO;

public interface AccountService {

    void transfer(TransferDTO transferDTO);

    AccountDTO findById(long accountId);
}
