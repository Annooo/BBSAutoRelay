package com.cn.BBSAutoRelay.zhihu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.httpClient.HttpResult;
import com.cn.BBSAutoRelay.httpClient.IHttpClient;
import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.selenium.SeleniumUtil;
import com.cn.BBSAutoRelay.selenium.WebDriverPool;
import com.cn.BBSAutoRelay.service.AccountService;
import com.cn.BBSAutoRelay.sms.YmAPI;
import com.cn.BBSAutoRelay.util.ByteUtils;
import com.google.common.base.Function;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ZhihuAction implements BBSAction{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile WebDriverPool webDriverPool;

    private int sleepTime = 0;

    private static Map<String,String> headers;

    private static JSONObject post_data;

    @Autowired
    private YmAPI ymAPI;

    private static final String index_url = "https://www.zhihu.com";
    private static final String login_url = "https://www.zhihu.com/api/v3/oauth/sign_in";
    private static final String captcha_url = "https://www.zhihu.com/api/v3/oauth/captcha?lang=cn";
    private static final String check_url = "https://www.zhihu.com/inbox";
    private static final String crack_captcha_url = "http://39.108.101.181:5001/zhihu/cn";
    private static final String user_url = "https://www.zhihu.com/people/%s";
    private static final String asks_url = "https://www.zhihu.com/people/%s/asks";
    private static final String answers_url = "https://www.zhihu.com/api/v4/members/%s/answers?include=data[*].is_normal,admin_closed_comment,reward_info,is_collapsed,annotation_action,annotation_detail,collapse_reason,collapsed_by,suggest_edit,comment_count,can_comment,content,voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,updated_time,review_info,question,excerpt,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp;data[*].author.badge[?(type=best_answerer)].topics&offset=0&limit=20&sort_by=created";

    private static IHttpClient request;

    @Autowired
    private AccountService accountService;

    static {
        headers = new HashMap();
        //headers.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
        headers.put("User-Agent","Mozilla/5.0 (iPhone 84; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Version/10.0 MQQBrowser/7.8.0 ");
        headers.put("Referer","https://www.zhihu.com/signin?next=%2F");
        headers.put("Authorization","oauth c3cef7c66a1843f8b3a9e6a1e3160e20");

        System.out.println(System.currentTimeMillis());
        post_data = JSONObject.parseObject("{" +
                "        \"client_id\": \"c3cef7c66a1843f8b3a9e6a1e3160e20\"," +
                "        \"grant_type\": \"password\"," +
                "        \"timestamp\": "+String.valueOf(System.currentTimeMillis())+"," +
                "        \"source\": \"com.zhihu.web\"," +
                "        \"signature\": \"\"," +
                "        \"username\": \"\"," +
                "        \"password\": \"\"," +
                "        \"captcha\": \"\"," +
                "        \"lang\": \"cn\"," +
                "        \"ref_source\": \"homepage\"," +
                "        \"utm_source\": \"\"" +
                "    }");
        System.out.println(post_data);
    }

    public ZhihuAction() {
    }

    public ZhihuAction(int sleepTime, WebDriverPool webDriverPool ) {
        if(webDriverPool != null) {
            this.webDriverPool = webDriverPool;
        }
        this.sleepTime = sleepTime;
    }

    @Override
    public void register(WebDriver webDriver) throws Exception {
        webDriver.manage().deleteAllCookies();

        webDriver.get("https://www.zhihu.com/signup");

        //设置cookie
        //SeleniumUtil.setCookies(webDriver, account.getCookies());
        webDriver.navigate().forward();

        // 设置页面加载时间为5秒
//        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
//        webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

//        WebElement webElement = webDriver.findElement(By.xpath("/html"));
//        String content = webElement.getAttribute("outerHTML");
        //System.out.println(content);

        //休眠一秒钟
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        WebElement registerButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div/div/div[1]/div/div[1]/div[2]/button[2]"));
        registerButton.click();
        logger.info("点击");

        // 表单切换到最顶层的frame中。
        //webDriver.switchTo().frame("top");
        //System.out.println(webDriver.switchTo().frame(0));

        //休眠一秒钟
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        //webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/button")).click();
        //获取手机号

        String token = ymAPI.getToken();
        String phone = ymAPI.getPhone("891","",token);

        WebElement account = webDriver.findElement(By.name("phoneNo"));
        account.sendKeys(phone);
        //account.click();

        ((RemoteWebDriver) webDriver).executeScript(
                "window.getJSON=$.getJSON;$.getJSON=function(){ var funObj=arguments[2]; var myFun=function(data){  window.myData=data;  funObj(data); } ; window.getJSON(arguments[0],arguments[1],myFun) }");

        WebDriverWait wait = new WebDriverWait(webDriver, 1);
        Map<String, ?> data = (Map<String, ?>)wait.until(new Function<WebDriver, Object>() {
            public Object apply(@Nullable WebDriver driver) {
                return  ((RemoteWebDriver) driver).executeScript("return window.myData;");
            }
        });
        if(!"0".equals(data.get("code"))){
            System.out.println("error");
            return;
        }else{
            System.out.println("success");
        }

        ///html/body/div[4]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[2]
        //Captcha SignFlow-captchaContainer Register-captcha Captcha-chinese

        //是否出现验证码
        WebElement captcha = webDriver.findElement(By.className("Captcha"));
        if(captcha.isDisplayed()){
            logger.info("出现验证码!");
        }

        //获取验证码
        ///html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/button
        ///html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/button
        WebElement button = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/button"));
        button.click();

        ///html/body/div[4]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/div/div[2]
        //SignFlowInput-errorMask SignFlow-smsInputErrorMessage SignFlowInput-requiredErrorMask

        String message = ymAPI.getMessage("891",phone,token);
        WebElement digits = webDriver.findElement(By.name("digits"));
        digits.sendKeys(message);

        WebElement register = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/button"));
        register.click();

        //设置用户名密码
        //fullname
        WebElement fullname = webDriver.findElement(By.name("fullname"));
        fullname.sendKeys(phone);
        //password
        WebElement password = webDriver.findElement(By.name("password"));
        password.sendKeys("aaa"+phone);

        WebElement signin = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div/div/div/div[1]/div/form/button"));
        signin.click();

        WebElement introduce = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div[2]/div[1]/div/input"));
        introduce.sendKeys(".");
        WebElement done = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div[2]/div[1]/button"));
        done.click();


        System.out.println(webDriver.manage().getCookies());

        //保存账号
        Account account1 = new Account();
        account1.setUserName(phone);
        account1.setPassword("aaa"+phone);
        account1.setCookies(webDriver.manage().getCookies().toString());
        account1.setCreateTime(new Date());
        accountService.addAccount(account1);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void login(WebDriver webDriver, Account account) throws Exception {

        request = new IHttpClient();

        /**
         * 判断是否需要登陆
         */
        if(!check_login()) {
            post_data.replace("username", account.getUserName());
            post_data.replace("password", account.getPassword());
            post_data.replace("captcha", check_captcha());

            //System.out.println("获取验证码后cookie:"+request.getContext().getCookieStore());

            System.out.println(post_data);

            post_data.replace("signature", get_signature());

            System.out.println(post_data);

            //System.out.println("context:"+request.getContext().getCookieStore());

            HttpResult httpResult = request.doPost(login_url, JSONObject.parseObject(post_data.toJSONString(), Map.class), headers);
            System.out.println(httpResult);

            if(check_login()) {
                logger.info("登陆成功");

                List<Cookie> cookies = request.getContext().getCookieStore().getCookies();
                JSONObject cookie = new JSONObject();
                for (Cookie c : cookies) {
                    System.out.println("key:" + c.getName() + "  value:" + c.getValue());
                    cookie.put(c.getName(), c.getValue());
                }
                //修改cookie
                //account.setCookies(JSONObject.parseObject(httpResult.getContent()).getString("cookie"));
                account.setCookies(cookie.toJSONString());
                account.setLoginTime(new Date());
                accountService.updateAccount(account);
            }else{
                logger.info("登陆失败");
            }
        }

        //this.logger.info("downloading page " + webDriver.getUrl());
        /*
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

        System.out.println(webDriver.manage().getCookies());

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

    }

    @Override
    public void posted(WebDriver webDriver, Account account) {

        webDriver.get("https://www.zhihu.com");
        webDriver.manage().deleteAllCookies();

        //设置cookie
        SeleniumUtil.setCookies(webDriver, account.getCookies());

        webDriver.get("https://www.zhihu.com");

        //删除第一次建立连接时的cookie
        //webDriver.manage().deleteAllCookies();

        // 设置页面加载时间为5秒
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        ////*[@id="root"]/div/main/div/div/div[1]/div[1]/div/div/button[1]
        WebElement questionAskButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div/div[1]/div[1]/div/div/button[1]"));
        questionAskButton.click();

        ////*[@id="Popover80-toggle"]
        ///html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[1]/div/div/div/textarea
        WebElement title = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[1]/div/div/div/textarea"));
        title.sendKeys("空闲的时候去哪里旅游好?");

        ////*[@id="Popover40-toggle"]
        ///html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[1]/div/div/div/input
        WebElement topic = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[2]/div/div/div/input"));
        //WebElement topic = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[1]/div/div/div")).findElement(By.tagName("input"));
        topic.sendKeys("旅游推荐");
        System.out.println(topic.getText());

        ///html/body/div[7]
        WebElement topics = webDriver.findElement(By.xpath("/html/body/div[7]")).findElements(By.className("Menu-item")).get(0);
        System.out.println(topics.getAttribute("innerHTML"));
        topics.click();


//        Select select = new Select(topic);
//        select.selectByIndex(1);

        ////*[@id="anonymous-checkbox"]
//        WebElement anonymous = webDriver.findElement(By.xpath("//*[@id=\"anonymous-checkbox\"]"));
//        anonymous.click();

        ///html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[6]/button
        ///html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[5]/button
        WebElement submit = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div[2]/div/form/div[5]/button"));
        submit.click();

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reply(WebDriver webDriver, Account account) throws IOException {
        OkHttpClient client = new OkHttpClient();

        //Headers headers = new Headers();
        Request request = new Request.Builder()
                .url("https://www.zhihu.com/search?type=content&q=%E5%AE%B6%E7%94%A8%E6%8A%95%E5%BD%B1%E4%BB%AA")
                .get()
                .addHeader("Cookie", "_xsrf=ffe7e139-3b5a-465b-be8e-b44b354df670; d_c0=\"ANBmA_476w2PTjSBKKTiVq9YcQ0-C8PEdsc=|1531904609\"; q_c1=7f1e067f2a8647708656c033f726d4f3|1531904609000|1531904609000; _zap=2f8e1876-5a97-45de-93c2-acf90bdd62e9; tgw_l7_route=4902c7c12bebebe28366186aba4ffcde; capsion_ticket=\"2|1:0|10:1532049356|14:capsion_ticket|44:NWM2YmQ5NjFkZTJiNDNiNDgxYjMzMzM5N2U2MzA5MDI=|619014fd7733909fe0c82ddd46c16832b2d360776a90e00253650eb78d344d2a\"; z_c0=\"2|1:0|10:1532049366|4:z_c0|92:Mi4xWXpzekJnQUFBQUFBMEdZRF9qdnJEU1lBQUFCZ0FsVk4xb1UtWEFBc2dYMlZlUGhHTWhLaXBiSWlhOHNhRzA3cGdB|1099f57ecf105d808ca72e6b9f69460fd51b04e8dc021dc8004eeaf58cc4df4e\"")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());

        Map<String,String> cookies = new HashMap();

        Document doc = Jsoup.connect("https://www.zhihu.com/search?type=content&q=%E5%AE%B6%E7%94%A8%E6%8A%95%E5%BD%B1%E4%BB%AA")
                .get();

        //System.out.println(doc.body().html());

        Elements lists = doc.getElementsByClass("List-item");

//        for (Element headline : lists) {
//            System.out.println(String.format("%s\n\t",
//                    headline.getElementsByClass("")));
//        }
        //{"content":"<p>。</p>","reshipment_settings":"allowed","comment_permission":"nobody","reward_setting":{"can_reward":false}}
        JSONObject json = JSONObject.parseObject("{\"content\":\"<p>。</p>\",\"reshipment_settings\":\"allowed\",\"comment_permission\":\"nobody\",\"reward_setting\":{\"can_reward\":false}}");
        System.out.println(json);

        //https://www.zhihu.com/question/31166958/answer/104224643
        //https://www.zhihu.com/api/v4/questions/31166958/answers?include=admin_closed_comment%2Creward_info%2Cannotation_action%2Cannotation_detail%2Ccollapse_reason%2Cis_normal%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelevant_info%2Cquestion%2Cexcerpt%2Crelationship.is_authorized%2Cvoting%2Cis_thanked%2Cis_author%2Cis_nothelp%3Bmark_infos%5B*%5D.url%3Bauthor.badge%5B%3F(type%3Dbest_answerer)%5D.topics HTTP/1.1
        //admin_closed_comment,reward_info,annotation_action,annotation_detail,collapse_reason,is_normal,is_sticky,collapsed_by,suggest_edit,comment_count,can_comment,content,editable_content,voteup_count,reshipment_settings,comment_permission,created_time,updated_time,review_info,relevant_info,question,excerpt,relationship.is_authorized,voting,is_thanked,is_author,is_nothelp;mark_infos[*].url;author.badge[?(type=best_answerer)].topics

//        String loginState = null;
//        Map<String, String> postParams = new HashMap<>();
//        postParams.put("captcha", yzm);
//        postParams.put("_xsrf", "");//这个参数可以不用
//        postParams.put("password", pwd);
//        postParams.put("remember_me", "true");
//        HttpClientUtil.postRequest()
    }

    /**
     * 是否登陆
     * @return
     */
    private boolean check_login(){
        HttpResult httpResult = request.doGet(this.check_url,null, headers);
        if(httpResult.getStatusCode()<300){
            logger.info("已登录成功");
            return true;
        }else{
            logger.info("未登录或登陆失败");
            return false;
        }
    }
    /**
     * 获取验证码
     * @return
     */
    private JSONObject check_captcha() throws Exception {
        HttpResult httpResult = request.doGet(this.captcha_url,null, headers);
        System.out.println(httpResult);
        boolean show_captcha = JSONObject.parseObject(httpResult.getContent()).getBoolean("show_captcha");
        //无验证码
        if(!show_captcha){
            return null;
        //有验证码，重新请求获取验证码
        }else{
            httpResult = request.doPut(this.captcha_url,null, headers);
            //System.out.println("获取验证码后cookie:"+request.getContext().getCookieStore());
            System.out.println(httpResult);
            String img = JSONObject.parseObject(httpResult.getContent()).getString("img_base64");
            //System.out.println(img);
            //Base64Utils.Base64ToImage(img,"C:/Users/Administrator/Desktop/test1.jpg");

            //识别验证码
            JSONObject carck_result = carck_captcha(img);
            System.out.println(carck_result);

            //验证验证码
            JSONObject result = new JSONObject();
            if(carck_result.getString("status").equals("success")){
                result.put("img_size", JSONArray.parseArray("[200,44]"));
                result.put("input_points",carck_result.getJSONArray("positions"));
                //return result;
            }

            Thread.sleep(2000);
            JSONObject input_text = new JSONObject();
            input_text.put("input_text",result);
            httpResult = request.doPost(this.captcha_url,JSONObject.parseObject(input_text.toJSONString(), Map.class), headers);
            System.out.println(httpResult);
            return result;
        }
        //return null;
    }

    public static JSONObject carck_captcha(String img_base64) throws Exception {
        IHttpClient httpClient = new IHttpClient();
        Map<String, String> postParams = new HashMap<>();
        postParams.put("img_base64", img_base64);
        HttpResult httpResult = httpClient.doPost(crack_captcha_url, postParams,null);
        return JSONObject.parseObject(httpResult.getContent());
    }

    private String get_signature() throws Exception {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec signinKey = new SecretKeySpec("d1b964811afb40118a12068ff74a12f4".getBytes(), "HmacSHA1");
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");
        //用给定密钥初始化 Mac 对象
        mac.init(signinKey);
        //完成 Mac 操作
        mac.update(post_data.getString("grant_type").getBytes());
        mac.update(post_data.getString("client_id").getBytes());
        mac.update(post_data.getString("source").getBytes());
        mac.update(post_data.getString("timestamp").getBytes());

        return new String(ByteUtils.toHexString(mac.doFinal()));
    }

    /**
     * 发帖记录
     *
     * @param webDriver
     */
    @Override
    public JSONObject postedRecord(WebDriver webDriver, Account account) throws Exception {
        JSONObject result = new JSONObject();
//        if(!check_login()) {


            // 设置页面加载时间为5秒
//        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//        webDriver.manage().timeouts().setScriptTimeout(3, TimeUnit.SECONDS);
//        webDriver.manage().timeouts().pageLoadTimeout(3, TimeUnit.SECONDS);

            webDriver.get(String.format(asks_url, account.getUserName()));
            //删除第一次建立连接时的cookie
            webDriver.manage().deleteAllCookies();

            //设置cookie
            SeleniumUtil.setCookies(webDriver, account.getCookies());
//        }

//        try {
            webDriver.get(String.format(asks_url, account.getUserName()));
//        } catch (TimeoutException e) {
//            //e.printStackTrace();
//            //webDriver.navigate().
//            logger.info("超市");
//            ((JavascriptExecutor) webDriver).executeScript("window.stop()");
//        }


//        WebDriverWait wait = new WebDriverWait(webDriver,3);
//        wait.until(new ExpectedCondition<Boolean>(){
//            @Override
//            public Boolean apply(WebDriver d) {
//                logger.info("11111");
//                return d.findElement(By.xpath("//*[@id=\"Profile-asks\"]/div[2]/div")).isDisplayed();
//            }});



        ////*[@id="Profile-asks"]/div[2]/div
        List<WebElement> asks = webDriver.findElements(By.xpath("//*[@id=\"Profile-asks\"]/div[2]/div"));
        JSONArray datas = new JSONArray();

        for(WebElement webElement:asks){
            JSONObject data = new JSONObject();
            System.out.println(webElement.getAttribute("innerHTML"));
            ///html/body/div/h2/div/a
            data.put("title", webElement.findElement(By.xpath(".//*[@class='QuestionItem-title']/a")).getText());
            data.put("href", webElement.findElement(By.xpath(".//*[@class='QuestionItem-title']/a")).getAttribute("href"));
            data.put("createTime", webElement.findElement(By.xpath(".//*[@class='ContentItem-status']/span[1]")).getText());
            data.put("ask", webElement.findElement(By.xpath(".//*[@class='ContentItem-status']/span[2]")).getText());
            data.put("following", webElement.findElement(By.xpath(".//*[@class='ContentItem-status']/span[3]")).getText());
            datas.add(data);
        }

        result.put("status", "ok");
        result.put("datas",datas);

        System.out.println(result);
        return result;
//        HttpResult httpResult = request.doGet(String .format(this.answers_url,account.getUserName()),null,null);
//        logger.info(httpResult.getContent());
//        return JSONObject.parseObject(httpResult.getContent());
    }

    /**
     * 回复记录
     *
     * @param webDriver
     * @throws Exception
     */
    @Override
    public JSONObject replyRecord(WebDriver webDriver, Account account) throws Exception {
        //webDriver.manage().
        webDriver.get(String.format(this.user_url, account.getUserName()));
        return null;
        /*
        request = new IHttpClient();
        //login(webDriver, account);
        post_data.replace("username", account.getUserName());
        post_data.replace("password", account.getPassword());
        post_data.replace("captcha", check_captcha());

        //System.out.println("获取验证码后cookie:"+request.getContext().getCookieStore());

        //System.out.println(post_data);

        post_data.replace("signature", get_signature());

        request.doPost(login_url, JSONObject.parseObject(post_data.toJSONString(), Map.class), headers);

        //request.doGet(this.index_url,null, headers);

        //headers.replace("Referer","https://www.zhihu.com");
        //个人中心
        request.doGet(String .format(this.user_url, account.getUserName()),null, headers, true);

        //headers.put("Cookie",account.getCookies());
        //回答
        HttpResult httpResult = request.doGet(String .format(this.answers_url, String.valueOf(account.getUserName())),null, headers, false);
        logger.info(httpResult.getContent());
        return JSONObject.parseObject(httpResult.getContent());
        */
    }

    /**
     * 获取CookieStore
     * @param account
     * @return
     */
    private CookieStore getCookieStore(Account account){
        CookieStore cookieStore = new BasicCookieStore();
        JSONObject cookies = JSONObject.parseObject(account.getCookies());
        for (Map.Entry<String, Object> entry : cookies.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), (String) entry.getValue());
            cookie.setDomain(".zhihu.com");
            cookieStore.addCookie(cookie);
        }
        return cookieStore;
    }

    /**
     * 获取cookie
     * @param account
     * @return
     */
    private List<org.openqa.selenium.Cookie> getCookies(Account account){
        List<org.openqa.selenium.Cookie> cookies = new ArrayList<>();
        JSONObject _cookies = JSONObject.parseObject(account.getCookies());
        for (Map.Entry<String, Object> entry : _cookies.entrySet()) {
            //System.out.println(entry.getKey() + "=========" + entry.getValue());
            org.openqa.selenium.Cookie cookie = new org.openqa.selenium.Cookie(entry.getKey(), (String) entry.getValue());
            cookies.add(cookie);
        }
        return cookies;
    }

    public static void main(String[] args) throws Exception {

    }
}
