package com.cn.BBSAutoRelay.service.impl;

import com.cn.BBSAutoRelay.mapper.AccountMapper;
import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public int addAccount(Account account) {
        return accountMapper.insert(account);
    }

    @Override
    public List<Account> queryAccounts() {
        return accountMapper.selectAccountAll();
    }
}
