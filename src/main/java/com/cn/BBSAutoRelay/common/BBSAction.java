package com.cn.BBSAutoRelay.common;

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
    void login(WebDriver webDriver, String userName, String password) throws Exception;

    /**
     * 发帖
     * @param webDriver
     */
    void posted(WebDriver webDriver) throws Exception;

    /**
     * 回复
     * @param webDriver
     * @throws Exception
     */
    void reply(WebDriver webDriver) throws Exception;
}
