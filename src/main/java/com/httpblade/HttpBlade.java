package com.httpblade;

import com.httpblade.base.HttpClient;

public final class HttpBlade {

    private static JsonParserFactory jsonParserFactory;
    private static XmlParserFactory xmlParserFactory;

    /**
     * JDK 自带客户端类型
     */
    public static final int CLIENT_TYPE_JDK = Environment.CLIENT_TYPE_JDK;

    /**
     * OKHttp 客户端类型
     */
    public static final int CLIENT_TYPE_OKHTTP = Environment.CLIENT_TYPE_OKHTTP;

    /**
     * Apache http components 客户端类型
     */
    public static final int CLIENT_TYPE_APACHE_HTTP = Environment.CLIENT_TYPE_APACHE_HTTP;

    public static final HttpBlade INSTANCE = null;

    public static HttpBlade create() {
        return null;
    }

    private HttpClient client;

    private HttpBlade() {
        client = Environment.defaultClient;
    }

//    public static Request createRequest() {
//        return Environment.newRequest();
//    }

//    public static HttpClientBuilder createClientBuilder() {
//        return Environment.newClientBuilder();
//    }

    public static int nowClientType() {
        return Environment.nowUseClientType;
    }

    /**
     * 设置默认的全局 Http 客户端
     *
     * @param httpClient 自定义的全局 Http 客户端
     */
//    public static void setDefaultClient(HttpClient httpClient) {
//        if (httpClient == null) {
//            throw new NullPointerException("the parameter is null");
//        }
//        Environment.defaultClient = httpClient;
//    }

    /**
     * 获取当前默认的全局 Http 客户端
     *
     * @return Http 客户端
     */
//    public static HttpClient defaultClient() {
//        return Environment.defaultClient;
//    }

    public static void setJsonParserFactory(JsonParserFactory parserFactory) {
        HttpBlade.jsonParserFactory = parserFactory;
    }

    public static JsonParserFactory getJsonParserFactory() {
        return jsonParserFactory;
    }

    public static XmlParserFactory getXmlParserFactory() {
        return xmlParserFactory;
    }

    public static void setXmlParserFactory(XmlParserFactory xmlParserFactory) {
        HttpBlade.xmlParserFactory = xmlParserFactory;
    }

    public Request get(String url) {
        return Environment.newRequest().get(url);
    }

    public Request post(String url) {
        return Environment.newRequest().post(url);
    }

    public Request put(String url) {
        return Environment.newRequest().put(url);
    }

    public Request delete(String url) {
        return Environment.newRequest().delete(url);
    }

    public Request head(String url) {
        return Environment.newRequest().head(url);
    }

    public static Request options(String url) {
        return Environment.newRequest().options(url);
    }

    public static Request trace(String url) {
        return Environment.newRequest().trace(url);
    }

    public static Request connect(String url) {
        return Environment.newRequest().connect(url);
    }

    public static Request patch(String url) {
        return Environment.newRequest().patch(url);
    }

    HttpClient getClient() {
        return client;
    }

}
