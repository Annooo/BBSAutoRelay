package com.cn.BBSAutoRelay.service;

import com.cn.BBSAutoRelay.model.Account;

import java.util.List;

public interface AccountService {

    /**
     * 新增账号信息
     * @param account
     * @return
     */
    int addAccount(Account account);

    /**
     * 查询账号信息
     * @return
     */
    List<Account> queryAccounts();
}
