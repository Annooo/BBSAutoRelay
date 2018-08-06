package com.cn.BBSAutoRelay.Configuration;

import com.cn.BBSAutoRelay.selenium.UserAgentUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * webDriver连接池配置
 */
@Configuration
public class WebDriverConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 初始化容量
     */
    private static final int DEFAULT_CAPACITY = 5;

    /**
     * 实际容量
     */
    private static int capacity = 0;
//    private static final int STAT_RUNNING = 1;
//    private static final int STAT_CLODED = 2;

    private AtomicInteger stat;

    private WebDriver mDriver;

//    private boolean mAutoQuitDriver;

//    private static final String DEFAULT_CONFIG_FILE = "/data/webmagic/webmagic-selenium/config.ini";

    private static final String DRIVER_FIREFOX = "firefox";
    private static final String DRIVER_CHROME = "chrome";
    private static final String DRIVER_PHANTOMJS = "phantomjs";

//    protected static Properties sConfig;

    protected static DesiredCapabilities sCaps;

    private List<WebDriver> webDriverList;

    private BlockingDeque<WebDriver> innerQueue;

    @Value("${selenium.driver}")
    private String driver;

    @Value("${selenium.driver_show}")
    private String driver_show;

    @Value("${selenium.phantomjs_exec_path}")
    private String phantomjs_exec_path;

    @Value("${selenium.phantomjs_driver_path}")
    private String phantomjs_driver_path;

    @Value("${selenium.phantomjs_driver_loglevel}")
    private String phantomjs_driver_loglevel;

    public WebDriverConfig() {
        this(DEFAULT_CAPACITY);
        logger.info("webDriver连接池启动初始化......");
    }

    public WebDriverConfig(int capacity) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.stat = new AtomicInteger(1);
        this.mDriver = null;
        //this.mAutoQuitDriver = true;
        this.webDriverList = Collections.synchronizedList(new ArrayList());
        this.innerQueue = new LinkedBlockingDeque();
        this.capacity = capacity;
    }

    public void configure() throws IOException {
        sCaps = new DesiredCapabilities();
        sCaps.setJavascriptEnabled(true);
        sCaps.setCapability("takesScreenshot", true);
        if (driver.equals(DRIVER_PHANTOMJS)) {
            if (phantomjs_exec_path == null) {
                throw new IOException(String.format("Property '%s' not set!", "phantomjs.binary.path"));
            }
            sCaps.setCapability("phantomjs.binary.path", phantomjs_exec_path);
            if (phantomjs_driver_path != null) {
                System.out.println("Test will use an external GhostDriver");
                sCaps.setCapability("phantomjs.ghostdriver.path", phantomjs_driver_path);
            } else {
                System.out.println("Test will use PhantomJS internal GhostDriver");
            }
            /**
             * 自定义phantomjs配置
             */
            sCaps.setJavascriptEnabled(true);
            sCaps.setCapability("takesScreenshot", true);
            sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "Cache-Control", "max-age=0");
            sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "Connection", "keep-alive");
            sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "User-Agent", UserAgentUtils.radomUserAgent());
            sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, "--load-images=yes");
        }

        ArrayList<String> cliArgsCap = new ArrayList();
        cliArgsCap.add("--web-security=false");
        cliArgsCap.add("--ssl-protocol=any");
        cliArgsCap.add("--ignore-ssl-errors=true");
        sCaps.setCapability("phantomjs.cli.args", cliArgsCap);
        sCaps.setCapability("phantomjs.ghostdriver.cli.args", new String[]{"--logLevel=" + (phantomjs_driver_loglevel != null ? phantomjs_driver_loglevel : "INFO")});
        if (this.isUrl(driver)) {
            sCaps.setBrowserName("phantomjs");
            this.mDriver = new RemoteWebDriver(new URL(driver), sCaps);
        } else if (driver.equals(DRIVER_FIREFOX)) {
            this.mDriver = new FirefoxDriver(sCaps);
        } else if (driver.equals(DRIVER_CHROME)) {
            ChromeOptions chromeOptions = new ChromeOptions();
            if("false".equals(driver_show)){
                //设置浏览器窗口打开大小  （非必须）
                chromeOptions.addArguments("--window-size=1920,1080");
                //设置为 headless 模式 （必须）
                chromeOptions.addArguments("--headless");
                //设置浏览器禁用显卡
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--no-sandbox");
            }
            sCaps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

            //this.mDriver = new ChromeDriver(chromeOptions);
            this.mDriver = new ChromeDriver(sCaps);
            //this.mDriver.manage().window().maximize();
        } else if (driver.equals(DRIVER_PHANTOMJS)) {
            this.mDriver = new PhantomJSDriver(sCaps);
        }
    }

    private boolean isUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException var3) {
            return false;
        }
    }

    public WebDriver getWebDriver() throws InterruptedException {
        logger.info("获取驱动");
        this.checkRunning();
        WebDriver poll = (WebDriver)this.innerQueue.poll();
        if (poll != null) {
            return poll;
        } else {
            if (this.webDriverList.size() < this.capacity) {
                List var2 = this.webDriverList;
                synchronized(this.webDriverList) {
                    if (this.webDriverList.size() < this.capacity) {
                        try {
                            this.configure();
                            mDriver.manage().timeouts().pageLoadTimeout(30,TimeUnit.SECONDS);
                            mDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                            mDriver.manage().timeouts().setScriptTimeout(10,TimeUnit.SECONDS);
                            mDriver.manage().window().maximize();
                            this.innerQueue.add(this.mDriver);
                            this.webDriverList.add(this.mDriver);
                        } catch (IOException var5) {
                            var5.printStackTrace();
                        }
                    }
                }
            }

            return (WebDriver)this.innerQueue.take();
        }
    }

    protected void checkRunning() {
        if (!this.stat.compareAndSet(1, 1)) {
            throw new IllegalStateException("Already closed!");
        }
    }
}
