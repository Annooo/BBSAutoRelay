package com.cn.BBSAutoRelay.httpClient;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import com.cn.BBSAutoRelay.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

/**
 * httpclient工具类
 * @autho chenning
 * @time 2018年7月23日 09点56分
 */
public class IHttpClient {

    private static CloseableHttpClient httpClient;
    private HttpClientContext context = null;
    private static CookieStore cookieStore = null;

    private static String CHARSET = "UTF-8";

    private static String charset;

    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    private void init(){
        context = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Constants.TIMEOUT)
                .setSocketTimeout(15000)
                //设置cookie标准
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        httpClient = HttpClientBuilder.create()
                //长连接
                //.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                //重定向
                //.setRedirectStrategy(new DefaultRedirectStrategy())
                .setDefaultRequestConfig(config)
                .setDefaultCookieStore(cookieStore)
                .setProxy(new HttpHost("127.0.0.1",8888))
                .build();
    }

    public IHttpClient() {
        this(null,CHARSET);
    }

    public IHttpClient(HttpClientContext context) {
        this(context,CHARSET);
    }

    public IHttpClient(HttpClientContext context, String charset) {
        if(context != null) {
            this.context = context;
        }
        this.charset = charset;
        init();
    }

    public HttpClientContext getContext() {
        return context;
    }

    public HttpResult doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return doGet(url, params, headers, false ,charset);
    }

    public HttpResult doGetSSL(String url, Map<String, String> params, Map<String, String> headers) {
        return doGetSSL(url, params, headers, charset);
    }

    public HttpResult doPost(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return doPost(url, params, headers, charset);
    }

    public HttpResult doPostJson(String url, Map<String, String> params, String json, Map<String, String> headers) throws Exception {
        return doPostJson(url, params, json, headers, charset);
    }

    public HttpResult doPut(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return doPut(url, params, headers, charset);
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
        System.out.println("get close cookie:"+context.getCookieStore());
        return httpResult;
    }

    /**
     * HTTP Post 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 页面内容
     * @throws IOException
     */
    private HttpResult doPost(String url, Map<String, String> params, Map<String, String> headers, String charset)
            throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpResult httpResult = new HttpResult();
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = String.valueOf(entry.getValue());
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                httpPost.addHeader(key, headers.get(key));
            }
        }
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
        }
        CloseableHttpResponse response = null;
        try {
            System.out.println("post brfore cookie:"+context.getCookieStore());
            response = httpClient.execute(httpPost, context);
            System.out.println("post after cookie:"+context.getCookieStore());
            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != 200) {
//                httpPost.abort();
//                throw new RuntimeException("IHttpClient,error status code :" + statusCode);
//            }
            httpResult.setStatusCode(statusCode);
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            httpResult.setContent(result);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }
        System.out.println("post close cookie:"+context.getCookieStore());
        return httpResult;
    }

    /**
     * 有参post请求,json交互
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param json 请求的json
     * @param charset 编码格式
     * @return 页面内容
     * @throws Exception
     */
    private HttpResult doPostJson(String url , Map<String, String> params, String json, Map<String, String> headers, String charset) throws Exception{
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpResult httpResult = new HttpResult();
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                httpPost.addHeader(key, headers.get(key));
            }
        }
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
        }
        if(StringUtils.isNotBlank(json)){
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost, context);
            int statusCode = response.getStatusLine().getStatusCode();
            // 判断返回状态是否为200
//            if (statusCode != 200) {
//                httpPost.abort();
//                throw new RuntimeException("IHttpClient,error status code :" + statusCode);
//            }
            httpResult.setStatusCode(statusCode);
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            httpResult.setContent(result);
        } finally {
            if (response != null)
                response.close();
        }
        return httpResult;
    }

    /**
     * HTTP Put 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 页面内容
     * @throws IOException
     */
    private HttpResult doPut(String url, Map<String, String> params, Map<String, String> headers, String charset)
            throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        HttpResult httpResult = new HttpResult();
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPut httpPut = new HttpPut(url);
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                httpPut.addHeader(key, headers.get(key));
            }
        }
        if (pairs != null && pairs.size() > 0) {
            httpPut.setEntity(new UrlEncodedFormEntity(pairs, charset));
        }
        CloseableHttpResponse response = null;
        try {
            System.out.println("put brfore cookie:"+context.getCookieStore());
            response = httpClient.execute(httpPut, context);
            System.out.println("put after cookie:"+context.getCookieStore());
            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != 200) {
//                httpPut.abort();
//                throw new RuntimeException("IHttpClient,error status code :" + statusCode);
//            }
            httpResult.setStatusCode(statusCode);
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            httpResult.setContent(result);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }
        System.out.println("put close cookie:"+context.getCookieStore());
        return httpResult;
    }

    /**
     * HTTPS Get 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset  编码格式
     * @return 页面内容
     */
    private HttpResult doGetSSL(String url, Map<String, String> params, Map<String, String> headers, String charset) {
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
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);

            // https  注意这里获取https内容，使用了忽略证书的方式，当然还有其他的方式来获取https内容
            CloseableHttpClient httpsClient = createSSLClientDefault();
            CloseableHttpResponse response = httpsClient.execute(httpGet, context);
            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != 200) {
//                httpGet.abort();
//                throw new RuntimeException("IHttpClient,error status code :" + statusCode);
//            }
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
        return httpResult;
    }

    /**
     * 这里创建了忽略整数验证的CloseableHttpClient对象
     * @return
     */
    private CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * 手动增加cookie
     * @param name
     * @param value
     * @param domain
     * @param path
     */
    public void addCookie(String name, String value, String domain, String path) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookieStore.addCookie(cookie);
    }

    /**
     * 把当前cookie从控制台输出出来
     */
    public void printCookies() {
        cookieStore = context.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            System.out.println("key:" + cookie.getName() + "  value:" + cookie.getValue());
        }
    }

    /**
     * 检查cookie的键值是否包含传参
     *
     * @param key
     * @return
     */
    public boolean checkCookie(String key) {
        cookieStore = context.getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        boolean res = false;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                res = true;
                break;
            }
        }
        return res;
    }
}
