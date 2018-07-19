package com.cn.BBSAutoRelay.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class WebDriverPool {
    private Logger logger;
    private static final int DEFAULT_CAPACITY = 5;
    private final int capacity;
    private static final int STAT_RUNNING = 1;
    private static final int STAT_CLODED = 2;
    private AtomicInteger stat;
    private WebDriver mDriver;
    private boolean mAutoQuitDriver;
    private static final String DEFAULT_CONFIG_FILE = "/data/webmagic/webmagic-selenium/config.ini";
    private static final String DRIVER_FIREFOX = "firefox";
    private static final String DRIVER_CHROME = "chrome";
    private static final String DRIVER_PHANTOMJS = "phantomjs";
    protected static Properties sConfig;
    protected static DesiredCapabilities sCaps;
    private List<WebDriver> webDriverList;
    private BlockingDeque<WebDriver> innerQueue;

    //private Proxy proxy;

    public void configure() throws IOException {
        sConfig = new Properties();
        String configFile = "/data/webmagic/webmagic-selenium/config.ini";
        if (System.getProperty("selenuim_config") != null) {
            configFile = System.getProperty("selenuim_config");
        }
        sConfig.load(getClass().getClassLoader().getResourceAsStream(configFile));
        sCaps = new DesiredCapabilities();
        sCaps.setJavascriptEnabled(true);
        sCaps.setCapability("takesScreenshot", true);
        String driver = sConfig.getProperty("driver", "chrome");
        String driver_show = sConfig.getProperty("driver_show", "true");
        if (driver.equals(DRIVER_PHANTOMJS)) {
            if (sConfig.getProperty("phantomjs_exec_path") == null) {
                throw new IOException(String.format("Property '%s' not set!", "phantomjs.binary.path"));
            }

            sCaps.setCapability("phantomjs.binary.path", sConfig.getProperty("phantomjs_exec_path"));
            if (sConfig.getProperty("phantomjs_driver_path") != null) {
                System.out.println("Test will use an external GhostDriver");
                sCaps.setCapability("phantomjs.ghostdriver.path", sConfig.getProperty("phantomjs_driver_path"));
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
        sCaps.setCapability("phantomjs.ghostdriver.cli.args", new String[]{"--logLevel=" + (sConfig.getProperty("phantomjs_driver_loglevel") != null ? sConfig.getProperty("phantomjs_driver_loglevel") : "INFO")});
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
            //设置代理
            //System.out.println("代理:"+Data5uUtil.getIPpool().size());
            //Proxy proxy = Data5uUtil.getIPpool().poll();
//            Proxy proxy = Data5uUtil.getIPpool().peekLast();
//            if(proxy != null){
//                System.out.println("代理配置:==>"+proxy);
//                org.openqa.selenium.Proxy proxy1 = getProxy(proxy);
//                chromeOptions.setProxy(proxy1);
//                sCaps.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
//                sCaps.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
//                System.setProperty("http.nonProxyHosts", "localhost");//不需要用代理访问的主机
//                sCaps.setCapability(CapabilityType.PROXY, proxy);
//                //chromeOptions.addArguments("--proxy-server=socks5://" + proxy.getHost() + ":" + proxy.getPort());
//            }
            sCaps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

            //拓展设置
            //chromeOptions.addArguments("log-path=chromedriver.log");
            //chromeOptions.addArguments("screenshot"); 每打开一个页面就截图
            //chromeOptions.addArguments("start-maximized"); 最大化

            /*
            Map<String, Object> prefs = new HashMap<String, Object>();
            //prefs.put("profile.default_content_settings.popups", 0);
            //http://stackoverflow.com/questions/28070315/python-disable-images-in-selenium-google-chromedriver
            prefs.put("profile.managed_default_content_settings.images",2); //禁止下载加载图片
            chromeOptions.setExperimentalOption("prefs", prefs);
            */

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

    public WebDriverPool(int capacity) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.stat = new AtomicInteger(1);
        this.mDriver = null;
        this.mAutoQuitDriver = true;
        this.webDriverList = Collections.synchronizedList(new ArrayList());
        this.innerQueue = new LinkedBlockingDeque();
        this.capacity = capacity;
    }

    public WebDriverPool() {
        this(DEFAULT_CAPACITY);
    }

//    public void setProxy(Proxy proxy) {
//        this.proxy = proxy;
//    }

    public WebDriver get() throws InterruptedException {
        this.checkRunning();
        //System.out.println("驱动队列的数量:"+innerQueue.size());
        WebDriver poll = (WebDriver)this.innerQueue.poll();
        if (poll != null) {
            return poll;
        } else {
            if (this.webDriverList.size() < this.capacity) {
                List var2 = this.webDriverList;
                synchronized(this.webDriverList) {
                    if (this.webDriverList.size() < this.capacity) {
                        try {
                            //System.out.println("没有驱动了 要去重新生成了////////////////////////////////////////////");
                            this.configure();
                            mDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                            mDriver.manage().timeouts().pageLoadTimeout(100,TimeUnit.SECONDS);
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

    public void returnToPool(WebDriver webDriver) {
        this.checkRunning();
        //关闭webDriver
        //logger.info("关闭webDriver");
        //webDriver.quit();

        this.innerQueue.add(webDriver);
        //this.webDriverList.remove(webDriver);
    }
    public void removeToPool(WebDriver webDriver) {
        this.checkRunning();
        this.innerQueue.remove(webDriver);
        this.webDriverList.remove(webDriver);
    }

    protected void checkRunning() {
        if (!this.stat.compareAndSet(1, 1)) {
            throw new IllegalStateException("Already closed!");
        }
    }

    public void closeAll() {
        boolean b = this.stat.compareAndSet(1, 2);
        if (!b) {
            throw new IllegalStateException("Already closed!");
        } else {
            WebDriver webDriver;
            for(Iterator var2 = this.webDriverList.iterator(); var2.hasNext(); webDriver = null) {
                webDriver = (WebDriver)var2.next();
                this.logger.info("Quit webDriver" + webDriver);
                webDriver.quit();
            }

        }
    }

//    public org.openqa.selenium.Proxy getProxy(Proxy proxy) {
//        //设置代理
//        if(proxy != null){
//            org.openqa.selenium.Proxy proxy1 = new org.openqa.selenium.Proxy();
//            proxy1.setHttpProxy(String.format("%s:%d", proxy.getHost(), proxy.getPort()))
//                    .setFtpProxy(String.format("%s:%d", proxy.getHost(), proxy.getPort()))
//                    .setSslProxy(String.format("%s:%d", proxy.getHost(), proxy.getPort()));
//
//            return proxy1;
//        }
//        return null;
//    }

}
