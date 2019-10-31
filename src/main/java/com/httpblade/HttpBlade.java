package com.httpblade;

import com.httpblade.base.HttpClient;
import com.httpblade.base.HttpClientBuilder;
import com.httpblade.base.Request;
import com.httpblade.common.HttpMethod;

public final class HttpBlade {

    /** JDK 自带客户端类型 */
    public static final int CLIENT_TYPE_JDK = Environment.CLIENT_TYPE_JDK;
    /** OKHttp 客户端类型 */
    public static final int CLIENT_TYPE_OKHTTP = Environment.CLIENT_TYPE_OKHTTP;
    /** Apache http components 客户端类型 */
    public static final int CLIENT_TYPE_APACHE_HTTP = Environment.CLIENT_TYPE_APACHE_HTTP;

    public static void use(int clientType) {
        Environment.use(clientType);
    }

    public static Request createRequest() {
        return Environment.newRequest();
    }

    public static HttpClientBuilder createClientBuilder() {
        return Environment.newClientBuilder();
    }

    public static int nowClientType() {
        return Environment.nowUseClientType;
    }

    public static void setDefaultClient(HttpClient httpClient) {
        if(httpClient == null) {
            throw new NullPointerException("the parameter is null");
        }
        Environment.defaultClient = httpClient;
    }

    public static HttpClient defaultClient() {
        return Environment.defaultClient;
    }

    public static RequestWrapper get(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.GET);
    }

    public static RequestWrapper post(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.POST);
    }

    public static RequestWrapper put(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.PUT);
    }

    public static RequestWrapper delete(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.DELETE);
    }

    public static RequestWrapper head(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.HEAD);
    }

    public static RequestWrapper options(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.OPTIONS);
    }

    public static RequestWrapper trace(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.TRACE);
    }

    public static RequestWrapper connect(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.CONNECT);
    }

    public static RequestWrapper patch(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.PATCH);
    }

}
