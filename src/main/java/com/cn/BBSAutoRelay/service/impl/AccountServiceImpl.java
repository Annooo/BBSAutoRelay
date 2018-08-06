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

    /**
     * 新增账号信息
     * @param account
     * @return
     */
    @Override
    public int addAccount(Account account) {
        return accountMapper.insert(account);
    }

    /**
     * 修改账号信息
     * @param account
     * @return
     */
    @Override
    public int updateAccount(Account account) {
        return accountMapper.updateByPrimaryKeySelective(account);
    }

    /**
     * 查询账号信息
     * @return
     */
    @Override
    public PageInfo queryAccounts(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Account> accounts = accountMapper.selectAll();
        PageInfo result = new PageInfo(accounts);
        return result;
    }
}
