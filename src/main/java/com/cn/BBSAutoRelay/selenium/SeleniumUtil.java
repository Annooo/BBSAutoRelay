package com.cn.BBSAutoRelay.selenium;

import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.model.Account;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeleniumUtil {


    /**
     * 设置cookie
     * @param webDriver
     * @param cookies
     */
    public static void setCookies( WebDriver webDriver ,String cookies){
        JSONObject _cookies = JSONObject.parseObject(cookies);
        for (Map.Entry<String, Object> entry : _cookies.entrySet()) {
            //System.out.println(entry.getKey() + "=========" + entry.getValue());
            webDriver.manage().addCookie(new Cookie(entry.getKey(),(String) entry.getValue()));
        }
    }
}
