package com.cn.BBSAutoRelay.sms;

import com.cn.BBSAutoRelay.httpClient.HttpResult;
import com.cn.BBSAutoRelay.httpClient.IHttpClient;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 易码短信服务平台开放接口
 * 官方网址：www.51ym.me
 */
@Component
public class YmAPI {

    private static final Logger log = LoggerFactory.getLogger(YmAPI.class);

    @Value("${ym.username}")
    private String username;

    @Value("${ym.password}")
    private String password;

    @Value("${ym.token}")
    private String token;

    private static final int WAIT_TIME = 100;

    private static IHttpClient request = new IHttpClient();

    public String getToken(){
        String url =  "http://api.fxhyd.cn/UserInterface.aspx?action=login&username=" + username+"&password="+password;
        HttpResult httpResult = request.doGet(url,null,null);
        System.out.println(httpResult);
        if("success".equals(httpResult.getContent().split("\\|")[0])) {
            token = httpResult.getContent().split("\\|")[1];
            log.info("TOKEN是" + token);
            return token;
        }else {
            log.error("获取TOKEN错误,错误代码" + httpResult.getContent() + "。代码释义：1001:参数token不能为空;1002:参数action不能为空;1003:参数action错误;1004:token失效;1005:用户名或密码错误;1006:用户名不能为空;1007:密码不能为空;1008:账户余额不足;1009:账户被禁用;1010:参数错误;1011:账户待审核;1012:登录数达到上限");
        }
        return null;
    }

    /**
     * 获取手机号
     * @param itemId 项目编号
     * @param excludeno 排除号段170_171
     * @return
     */
    public String getPhone(String itemId, String excludeno, String token){
        String mobile;
        String url = "http://api.fxhyd.cn/UserInterface.aspx?action=getmobile&token=" + token+"&itemid="+itemId+"&excludeno="+excludeno;
        HttpResult httpResult = request.doGet(url,null,null);
        System.out.println(httpResult);
        if("success".equals(httpResult.getContent().split("\\|")[0])) {
            mobile = httpResult.getContent().split("\\|")[1];
            log.info("获取号码是:" + mobile);
            return mobile;
        }else {
            log.error("获取号码错误,错误代码" + httpResult.getContent());
        }
        return null;
    }

    /**
     * 获取短信内容
     * @param itemId
     * @param mobile
     * @param token
     * @return
     * @throws Exception
     */
    public String getMessage(String itemId, String mobile, String token) throws Exception {
        String message;
        String url = "http://api.fxhyd.cn/UserInterface.aspx?action=getsms&token=" + token+"&itemid="+itemId+"&mobile="+mobile+"&release=1";
        HttpResult httpResult = request.doGet(url,null,null);
        long TIME1 = DateUtils.getFragmentInSeconds(new Date(),1);
        long TIME2 = DateUtils.getFragmentInSeconds(new Date(),1);
        int ROUND = 1;
        while((TIME2 - TIME1) < WAIT_TIME && !"success".equals(httpResult.getContent().split("\\|")[0])){
            System.out.println(TIME2 +"--"+ TIME1+"--"+(TIME2-TIME1));
            Thread.sleep(5000);
            httpResult = request.doGet(url,null,null);
            TIME2= DateUtils.getFragmentInSeconds(new Date(),1);
            ROUND ++;
        }

        if("success".equals(httpResult.getContent().split("\\|")[0])) {
            message = httpResult.getContent().split("\\|")[1];
            log.info("短信内容是:" + message +"耗费时长" + (TIME2 - TIME1) + "s,循环数是" + ROUND);
            //释放号码
            releasePhone(itemId, mobile, token);
            return getVerificationCode(message);
        }else {
            log.error("获取短信超时，错误代码是" + httpResult.getContent() + ",循环数是" + ROUND);
        }
        return null;
    }

    /**
     * 释放手机号
     * @param itemId
     * @param mobile
     * @param token
     * @return
     * @throws Exception
     */
    public void releasePhone(String itemId, String mobile, String token){
        String url = "http://api.fxhyd.cn/UserInterface.aspx?action=release&token=" + token+"&itemid="+itemId+"&mobile="+mobile;
        HttpResult httpResult = request.doGet(url,null,null);
        System.out.println(httpResult);
        if("success".equals(httpResult.getContent())) {
            log.info("号码成功释放");
        }else {
            log.error("号码释放错误,错误代码" + httpResult.getContent());
        }
    }

    /**
     * 获取验证码
     * @param message
     * @return
     */
    public static String getVerificationCode(String message){
        String reg = "[0-9]+";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(message);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getVerificationCode("撒谎大师的话123456sdas"));
    }
}
