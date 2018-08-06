package com.cn.BBSAutoRelay.common;

import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.model.Account;
import org.openqa.selenium.WebDriver;

/**
 * @author chenning
 * @time 2018年7月19日17点46分
 */
public interface BBSAction {

    /**
     * 注册
     * @param webDriver
     */
    void register(WebDriver webDriver) throws Exception;

    /*
    登陆
     */
    void login(WebDriver webDriver, Account account) throws Exception;

    /**
     * 发帖
     * @param webDriver
     */
    void posted(WebDriver webDriver, Account account) throws Exception;

    /**
     * 回复
     * @param webDriver
     * @throws Exception
     */
    void reply(WebDriver webDriver, Account account) throws Exception;

    /**
     * 发帖记录
     * @param webDriver
     */
    JSONObject postedRecord(WebDriver webDriver, Account account) throws Exception;

    /**
     * 回复记录
     * @param webDriver
     * @throws Exception
     */
    JSONObject replyRecord(WebDriver webDriver, Account account) throws Exception;
}
