package com.cn.BBSAutoRelay.service.impl;

import com.cn.BBSAutoRelay.mapper.AccountMapper;
import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.service.AccountService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    public PageInfo queryAccounts(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        PageHelper.startPage(pageNum, pageSize);
        List<Account> accounts = accountMapper.selectAccountAll();
        PageInfo result = new PageInfo(accounts);
        return result;
    }
}
