package com.cn.BBSAutoRelay.zhihu;

import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.selenium.WebDriverPool;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ZhihuAction implements BBSAction{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile WebDriverPool webDriverPool;

    private int sleepTime = 0;


    public ZhihuAction(int sleepTime, WebDriverPool webDriverPool ) {
        if(webDriverPool != null) {
            this.webDriverPool = webDriverPool;
        }
        this.sleepTime = sleepTime;
    }

    @Override
    public void register(WebDriver webDriver) {

    }

    @Override
    public void login(WebDriver webDriver) {

        //this.logger.info("downloading page " + webDriver.getUrl());
        webDriver.get("https://www.zhihu.com/");

        // 设置页面加载时间为5秒
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        //System.out.println(content);

        WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div/div/div[1]/div/div[1]/div[2]/button[1]"));
        loginButton.click();
        logger.info("点击");

        // 表单切换到最顶层的frame中。
        //webDriver.switchTo().frame("top");
        //System.out.println(webDriver.switchTo().frame(0));
        webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        WebElement account = webDriver.findElement(By.name("username"));
        account.sendKeys("17620351934");
        WebElement password = webDriver.findElement(By.name("password"));
        password.sendKeys("chenning1");

        WebElement login = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/form/button"));
        login.click();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void posted(WebDriver webDriver) {

    }

    @Override
    public void reply(WebDriver webDriver) {

    }
}
