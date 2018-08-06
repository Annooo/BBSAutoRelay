package com.cn.BBSAutoRelay.httpClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AbstractHttpClient{

    private Logger logger = LoggerFactory.getLogger(AbstractHttpClient.class);

    private static String CHARSET = "UTF-8";

    private static String charset;

    @Autowired(required = false)
    private CloseableHttpClient httpClient;

    @Autowired(required = false)
    private RequestConfig requestConfig;

    private HttpClientContext context = null;

    private static CookieStore cookieStore = null;

    public HttpResult doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return doGet(url, params, headers, false ,CHARSET);
    }

    /**
     * HTTP Get 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    private HttpResult doGet(String url, Map<String, String> params, Map<String, String> headers, boolean redirects, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpResult httpResult = new HttpResult();
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                // 将请求参数和url进行拼接
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            if (headers != null) {
                Set<String> keys = headers.keySet();
                for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    httpGet.addHeader(key, headers.get(key));
                }
            }
            httpGet.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, redirects);
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            int statusCode = response.getStatusLine().getStatusCode();
            httpResult.setStatusCode(statusCode);
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            httpResult.setContent(result);
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("get close cookie:"+context.getCookieStore());
        return httpResult;
    }
}
