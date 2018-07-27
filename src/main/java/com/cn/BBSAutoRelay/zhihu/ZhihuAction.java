package com.cn.BBSAutoRelay.zhihu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.httpClient.HttpResult;
import com.cn.BBSAutoRelay.httpClient.IHttpClient;
import com.cn.BBSAutoRelay.selenium.WebDriverPool;
import com.cn.BBSAutoRelay.sms.YmAPI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.python.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    private static final String login_url = "https://www.zhihu.com/api/v3/oauth/sign_in";
    private static final String captcha_url = "https://www.zhihu.com/api/v3/oauth/captcha?lang=cn";
    private static final String check_url = "https://www.zhihu.com/inbox";
    private static final String crack_captcha_url = "http://39.108.101.181:5001/zhihu/cn";

    private static IHttpClient request;

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
        webDriver.get("https://www.zhihu.com/");

        // 设置页面加载时间为5秒
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        //System.out.println(content);

        WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div/div/div[1]/div/div[1]/div[2]/button[2]"));
        loginButton.click();
        logger.info("点击");

        // 表单切换到最顶层的frame中。
        //webDriver.switchTo().frame("top");
        //System.out.println(webDriver.switchTo().frame(0));
        webDriver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        //获取手机号
        WebElement account = webDriver.findElement(By.name("phoneNo"));

        String token = ymAPI.getToken();
        String phone = ymAPI.getPhone("891","",token);
        account.sendKeys(phone);

        //获取验证码
        ///html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/button
        WebElement button = webDriver.findElement(By.xpath("/html/body/div[5]/div/span/div/div[2]/div/div/div/div[2]/div[1]/div/div/form/div[3]/div[1]/button"));
        button.click();

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

        System.out.println(webDriver.manage().getCookies());

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(WebDriver webDriver, String userName, String password) throws Exception {

        request = new IHttpClient();
        /**
         * 判断是否需要登陆
         */
        if(!check_login()) {
            post_data.replace("username", userName);
            post_data.replace("password", password);
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
    public void posted(WebDriver webDriver) {

    }

    @Override
    public void reply(WebDriver webDriver) throws IOException {
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
            Base64Utils.Base64ToImage(img,"C:/Users/Administrator/Desktop/test1.jpg");

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

    public static void main(String[] args) throws Exception {
        carck_captcha("R0lGODdhkAFYAIcAAPn5+VVVVejo6NfX16ioqJeXl4aGhnV1dWNjY8XFxWtra7m5ucvLy7Ozs3x8\nfOHh4YyMjKKiot3d3Z2dnZCQkK6urgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACwAAAAAkAFYAEAI/wABCBxI\nsKDBgwgTKlzIsKHDhxAjSpxIsaLFixgzatzIsaPHjyBDihxJsqTJkyhLCmAgAYDLlzBjypxJs6bN\nmzhz6tzJs6fPn0CDCh1KtKhRnwIUBFhqAIDTpwAKBJg6FQGAq1izAjAQoGtXAGDDih1LtqzYAwHS\n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\n");
    //[72,18.537506103515625],[142,21.537506103515625]]
    //[[39.4550138234789, 145.97414376060084], [44.010828632589309, 285.1273889251533]]

    }
}
