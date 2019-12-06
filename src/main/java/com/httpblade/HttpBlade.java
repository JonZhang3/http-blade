package com.httpblade;

import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.util.LinkedHashMap;
import java.util.Map;
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

        private String baseUrl = "";
        private long connectTimeout;
        private long readTimeout;
        private long writeTimeout;
        private Proxy proxy;
        private HostnameVerifier hostnameVerifier;
        private CookieHome cookieHome;
        private SocketFactory socketFactory;
        private SSLSocketFactoryBuilder sslSocketFactoryBuilder;
        private Map<String, Headers> globalHeaders = new LinkedHashMap<>();

        public HttpBladeBuilder() {

        }

        HttpBladeBuilder(HttpBlade httpBlade) {

        }

        public HttpBladeBuilder baseUrl(String baseUrl) {
            if (baseUrl != null) {
                this.baseUrl = baseUrl;
            }
            return this;
        }

        public HttpBladeBuilder connectTimeout(long time, TimeUnit unit) {
            if (time > 0) {
                this.connectTimeout = unit.toMillis(time);
            }
            return this;
        }

        public HttpBladeBuilder readTimeout(long time, TimeUnit unit) {
            if (time > 0) {
                this.readTimeout = unit.toMillis(time);
            }
            return this;
        }

        public HttpBladeBuilder writeTimeout(long time, TimeUnit unit) {
            if (time > 0) {
                this.writeTimeout = unit.toMillis(time);
            }
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

        public HttpBladeBuilder setHeader(String name, String value) {
            getHeaders(Defaults.KEY_COMMON).set(name, value);
            return this;
        }

        public HttpBladeBuilder addHeader(String name, String value) {
            getHeaders(Defaults.KEY_COMMON).add(name, value);
            return this;
        }

        public HttpBladeBuilder setGetHeader(String name, String value) {
            getHeaders(HttpMethod.GET.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addGetHeader(String name, String value) {
            getHeaders(HttpMethod.GET.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setPostHeader(String name, String value) {
            getHeaders(HttpMethod.POST.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addPostHeader(String name, String value) {
            getHeaders(HttpMethod.POST.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setPutHeader(String name, String value) {
            getHeaders(HttpMethod.PUT.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addPutHeader(String name, String value) {
            getHeaders(HttpMethod.PUT.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setDeleteHeader(String name, String value) {
            getHeaders(HttpMethod.DELETE.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addDeleteHeader(String name, String value) {
            getHeaders(HttpMethod.DELETE.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setHeadHeader(String name, String value) {
            getHeaders(HttpMethod.HEAD.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addHeadHeader(String name, String value) {
            getHeaders(HttpMethod.HEAD.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setOptionsHeader(String name, String value) {
            getHeaders(HttpMethod.OPTIONS.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addOptionsHeader(String name, String value) {
            getHeaders(HttpMethod.OPTIONS.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setTraceHeader(String name, String value) {
            getHeaders(HttpMethod.TRACE.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addTraceHeader(String name, String value) {
            getHeaders(HttpMethod.TRACE.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setConnectHeader(String name, String value) {
            getHeaders(HttpMethod.CONNECT.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addConnectHeader(String name, String value) {
            getHeaders(HttpMethod.CONNECT.value()).add(name, value);
            return this;
        }

        public HttpBladeBuilder setPatchHeader(String name, String value) {
            getHeaders(HttpMethod.PATCH.value()).set(name, value);
            return this;
        }

        public HttpBladeBuilder addPatchHeader(String name, String value) {
            getHeaders(HttpMethod.PATCH.value()).add(name, value);
            return this;
        }

        public HttpBlade build() {
            return new HttpBlade(this);
        }

        private Headers getHeaders(String key) {
            Headers headers = this.globalHeaders.get(key);
            if (headers == null) {
                headers = new Headers();
                this.globalHeaders.put(key, headers);
            }
            return headers;
        }

    }

}
