package com.cn.BBSAutoRelay.service;

import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.model.Account;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;

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

    /**
     * 查询账号信息
     * @return
     */
    JSONObject queryPostedRecord(Account account);

    /**
     * 注册
     * @param account
     * @return
     */
    void register(Account account);
}
