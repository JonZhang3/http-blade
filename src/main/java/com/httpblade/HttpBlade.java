package com.httpblade;

import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.util.concurrent.TimeUnit;

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

    public static final HttpBlade INSTANCE = new HttpBlade();

    public static HttpBladeBuilder create() {
        return new HttpBladeBuilder();
    }

    private HttpClient client;

    private HttpBlade() {
        client = Environment.defaultClient;
    }

    HttpBlade(HttpBladeBuilder builder) {

    }

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

    public Request options(String url) {
        return Environment.newRequest().options(url);
    }

    public Request trace(String url) {
        return Environment.newRequest().trace(url);
    }

    public Request connect(String url) {
        return Environment.newRequest().connect(url);
    }

    public Request patch(String url) {
        return Environment.newRequest().patch(url);
    }

    public HttpBladeBuilder newBuilder() {
        return new HttpBladeBuilder(this);
    }

    public static class HttpBladeBuilder {

        private long connectTimeout;
        private long readTimeout;
        private long writeTimeout;
        private Proxy proxy;
        private HostnameVerifier hostnameVerifier;
        private CookieHome cookieHome;
        private SocketFactory socketFactory;
        private SSLSocketFactoryBuilder sslSocketFactoryBuilder;

        public HttpBladeBuilder() {

        }

        public HttpBladeBuilder(HttpBlade httpBlade) {

        }

        public HttpBladeBuilder connectTimeout(long time, TimeUnit unit) {
            this.connectTimeout = unit.toMillis(time);
            return this;
        }

        public HttpBladeBuilder readTimeout(long time, TimeUnit unit) {
            this.readTimeout = unit.toMillis(time);
            return this;
        }

        public HttpBladeBuilder writeTimeout(long time, TimeUnit unit) {
            this.writeTimeout = unit.toMillis(time);
            return this;
        }

        public HttpBladeBuilder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public HttpBladeBuilder proxy(String host, int port) {
            this.proxy = new Proxy(host, port);
            return this;
        }

        public HttpBladeBuilder proxy(String host, int port, String username, String password) {
            this.proxy = new Proxy(host, port, username, password);
            return this;
        }

        public HttpBladeBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public HttpBladeBuilder socketFactory(SocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        public HttpBladeBuilder sslSocketFactory(SSLSocketFactoryBuilder builder) {
            this.sslSocketFactoryBuilder = builder;
            return this;
        }

        public HttpBlade build() {
            return null;
        }

    }

}
