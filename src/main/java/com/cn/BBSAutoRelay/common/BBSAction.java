package com.cn.BBSAutoRelay.common;

import org.openqa.selenium.WebDriver;

/**
 * @author chenning
 * @time 2018年7月19日17点46分
 */
public interface BBSAction {

    void register(WebDriver webDriver);

    void login(WebDriver webDriver);

    void posted(WebDriver webDriver);

    void reply(WebDriver webDriver) throws Exception;
}
