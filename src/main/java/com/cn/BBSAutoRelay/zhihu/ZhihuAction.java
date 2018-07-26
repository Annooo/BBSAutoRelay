package com.cn.BBSAutoRelay.zhihu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.BBSAutoRelay.common.BBSAction;
import com.cn.BBSAutoRelay.httpClient.HttpResult;
import com.cn.BBSAutoRelay.httpClient.IHttpClient;
import com.cn.BBSAutoRelay.selenium.WebDriverPool;
import com.cn.BBSAutoRelay.util.Base64Utils;
import com.sun.crypto.provider.HmacSHA1;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.coyote.http2.ByteUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.python.bouncycastle.crypto.macs.HMac;
import org.python.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZhihuAction implements BBSAction{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile WebDriverPool webDriverPool;

    private int sleepTime = 0;

    private static Map<String,String> headers;

    private static JSONObject post_data;

    private static final String login_url = "https://www.zhihu.com/api/v3/oauth/sign_in";
    private static final String captcha_url = "https://www.zhihu.com/api/v3/oauth/captcha?lang=cn";
    private static final String check_url = "https://www.zhihu.com/inbox";
    private static final String crack_captcha_url = "http://39.108.101.181:5001/zhihu/cn";

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
                "        \"lang\": \"en\"," +
                "        \"ref_source\": \"homepage\"," +
                "        \"utm_source\": \"\"" +
                "    }");
        System.out.println(post_data);
    }

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
    public void login(WebDriver webDriver, String userName, String password) throws Exception {

        /**
         * 获取是否需要验证码
         */
        IHttpClient request = new IHttpClient();

        post_data.replace("username",userName);
        post_data.replace("password",password);
        post_data.replace("captcha",check_captcha(request));

        System.out.println(post_data);

        post_data.replace("signature",get_signature());

        System.out.println(post_data);

        HttpResult httpResult = request.doPost(login_url, JSONObject.parseObject(post_data.toJSONString(),Map.class), null);
        System.out.println(httpResult);

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
     * 获取验证码
     * @return
     */
    private JSONObject check_captcha(IHttpClient request) throws Exception {
        HttpResult httpResult = request.doGet(this.captcha_url,null, headers);
        System.out.println(httpResult);
        boolean show_captcha = JSONObject.parseObject(httpResult.getContent()).getBoolean("show_captcha");
        //无验证码
        if(!show_captcha){
            return null;
        //有验证码，重新请求获取验证码
        }else{
            httpResult = request.doPut(this.captcha_url,null, null);
            System.out.println(httpResult);
            String img = JSONObject.parseObject(httpResult.getContent()).getString("img_base64");
            //System.out.println(img);
            Base64Utils.Base64ToImage(img,"C:/Users/Administrator/Desktop/test1.jpg");

            //识别验证码
            JSONObject carck_result = carck_captcha(img);
            System.out.println(carck_result);
            if(carck_result.getString("status").equals("success")){
                JSONObject result = new JSONObject();
                result.put("img_size", JSONArray.parseArray("[200,44]"));
                result.put("input_points",carck_result.getJSONArray("positions"));

                return result;
            }
        }
        return null;
    }

    private JSONObject carck_captcha(String img_base64) throws Exception {
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

    public static void main(String[] args) {
    }
}
