package com.cn.BBSAutoRelay.httpClient;

import java.io.Serializable;

/**
 * HttpClient响应实体
 * @autho chenning
 * @time 2018年7月23日 09点56分
 */
public class HttpResult implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 状体码
     */
    private Integer statusCode;

    /**
     * 响应内容
     */
    private String content;

    public HttpResult(){

    }

    public HttpResult(Integer statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }

    /**
     * 获取状态码
     * @return 状态码
     */
    public Integer getStatusCode() {
        return statusCode;
    }


    /**
     * 设置获取状态码
     * @param statusCode 获取状态码
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }


    /**
     * 获取响应内容
     * @return 响应内容
     */
    public String getContent() {
        return content;
    }


    /**
     * 设置响应内容
     * @param content 响应内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpResult [statusCode=" + statusCode + ", content=" + content + "]";
    }

}
