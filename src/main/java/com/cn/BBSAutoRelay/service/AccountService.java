package com.cn.BBSAutoRelay.service;

import com.cn.BBSAutoRelay.model.Account;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface AccountService {

    /**
     * 新增账号信息
     * @param account
     * @return
     */
    int addAccount(Account account);

    /**
     * 修改账号信息
     * @param account
     * @return
     */
    int updateAccount(Account account);

    /**
     * 查询账号信息
     * @return
     */
    PageInfo queryAccounts(int pageNum, int pageSize);
}
