package com.cn.BBSAutoRelay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.mapper.AccountMapper;
import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.service.AccountService;
import com.cn.BBSAutoRelay.zhihu.ZhihuAction;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private com.cn.BBSAutoRelay.Configuration.WebDriverConfig webDriverConfig;

    @Autowired
    private ZhihuAction zhihuAction;

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

    @Override
    public JSONObject queryPostedRecord(Account account) {
        return null;
    }

    /**
     * 注册
     *
     * @param account
     * @return
     */
    @Override
    public void register(Account account){
        try {
            WebDriver webDriver = webDriverConfig.getWebDriver();
            zhihuAction.register(webDriver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
