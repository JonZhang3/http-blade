package com.httpblade;

import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.util.HashMap;
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

    private HttpClient client;

    private HttpBlade() {
        client = Environment.defaultClient;
    }

    HttpBlade(Builder builder) {
        this.client = Environment.createClient(builder.clientType, builder.baseUrl, builder.connectTimeout,
            builder.readTimeout, builder.writeTimeout, builder.maxRedirectCount, builder.cookieHome,
            builder.hostnameVerifier, builder.proxy, builder.socketFactory, builder.globalHeaders);
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

    public Builder newBuilder() {
        return new Builder(this);
    }

    public Builder newBuilder(int clientType) {
        return new Builder(clientType, this);
    }

    public static class Builder {

        private int clientType = Environment.CLIENT_TYPE_DEFAULT;
        private String baseUrl = "";
        private long connectTimeout = Defaults.CONNECT_TIMEOUT;
        private long readTimeout = Defaults.READ_TIMEOUT;
        private long writeTimeout = Defaults.WRITE_TIMEOUT;
        private int maxRedirectCount = Defaults.MAX_REDIRECT_COUNT;
        private Proxy proxy;
        private HostnameVerifier hostnameVerifier;
        private CookieHome cookieHome;
        private SocketFactory socketFactory;
        private SSLSocketFactoryBuilder sslSocketFactoryBuilder;
        private Map<String, Headers> globalHeaders = new LinkedHashMap<>();

        public Builder() {
        }

        public Builder(int clientType) {
            this.clientType = clientType;
        }

        Builder(int clientType, HttpBlade httpBlade) {
            this(httpBlade);
            this.clientType = clientType;
        }

        Builder(HttpBlade httpBlade) {
            HttpClient client = httpBlade.client;
            this.baseUrl = client.baseUrl;
            this.connectTimeout = client.connectTimeout;
            this.readTimeout = client.readTimeout;
            this.writeTimeout = client.writeTimeout;
            this.proxy = client.proxy;
            this.hostnameVerifier = client.hostnameVerifier;
            this.cookieHome = client.cookieHome;
            this.socketFactory = client.socketFactory;

            this.globalHeaders = new HashMap<>();
            this.globalHeaders.putAll(client.globalHeaders);
        }

        public Builder baseUrl(String baseUrl) {
            if (baseUrl != null) {
                if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
                    // TODO substring方法的性能问题，与 toCharArray 比较
                    this.baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                } else {
                    this.baseUrl = baseUrl;
                }
            }
            return this;
        }

        public Builder connectTimeout(long time, TimeUnit unit) {
            if (time > 0) {
                this.connectTimeout = unit.toMillis(time);
            }
            return this;
        }

        public Builder readTimeout(long time, TimeUnit unit) {
            if (time > 0) {
                this.readTimeout = unit.toMillis(time);
            }
            return this;
        }

        public Builder writeTimeout(long time, TimeUnit unit) {
            if (time > 0) {
                this.writeTimeout = unit.toMillis(time);
            }
            return this;
        }

        public Builder maxRedirectCount(int maxCount) {
            this.maxRedirectCount = maxCount;
            return this;
        }

        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder proxy(String host, int port) {
            this.proxy = new Proxy(host, port);
            return this;
        }

        public Builder proxy(String host, int port, String username, String password) {
            this.proxy = new Proxy(host, port, username, password);
            return this;
        }

        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public Builder socketFactory(SocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        public Builder sslSocketFactory(SSLSocketFactoryBuilder builder) {
            this.sslSocketFactoryBuilder = builder;
            return this;
        }

        public Builder setHeader(String name, String value) {
            getHeaders(Defaults.KEY_COMMON).set(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            getHeaders(Defaults.KEY_COMMON).add(name, value);
            return this;
        }

        public Builder setGetHeader(String name, String value) {
            getHeaders(HttpMethod.GET.value()).set(name, value);
            return this;
        }

        public Builder addGetHeader(String name, String value) {
            getHeaders(HttpMethod.GET.value()).add(name, value);
            return this;
        }

        public Builder setPostHeader(String name, String value) {
            getHeaders(HttpMethod.POST.value()).set(name, value);
            return this;
        }

        public Builder addPostHeader(String name, String value) {
            getHeaders(HttpMethod.POST.value()).add(name, value);
            return this;
        }

        public Builder setPutHeader(String name, String value) {
            getHeaders(HttpMethod.PUT.value()).set(name, value);
            return this;
        }

        public Builder addPutHeader(String name, String value) {
            getHeaders(HttpMethod.PUT.value()).add(name, value);
            return this;
        }

        public Builder setDeleteHeader(String name, String value) {
            getHeaders(HttpMethod.DELETE.value()).set(name, value);
            return this;
        }

        public Builder addDeleteHeader(String name, String value) {
            getHeaders(HttpMethod.DELETE.value()).add(name, value);
            return this;
        }

        public Builder setHeadHeader(String name, String value) {
            getHeaders(HttpMethod.HEAD.value()).set(name, value);
            return this;
        }

        public Builder addHeadHeader(String name, String value) {
            getHeaders(HttpMethod.HEAD.value()).add(name, value);
            return this;
        }

        public Builder setOptionsHeader(String name, String value) {
            getHeaders(HttpMethod.OPTIONS.value()).set(name, value);
            return this;
        }

        public Builder addOptionsHeader(String name, String value) {
            getHeaders(HttpMethod.OPTIONS.value()).add(name, value);
            return this;
        }

        public Builder setTraceHeader(String name, String value) {
            getHeaders(HttpMethod.TRACE.value()).set(name, value);
            return this;
        }

        public Builder addTraceHeader(String name, String value) {
            getHeaders(HttpMethod.TRACE.value()).add(name, value);
            return this;
        }

        public Builder setConnectHeader(String name, String value) {
            getHeaders(HttpMethod.CONNECT.value()).set(name, value);
            return this;
        }

        public Builder addConnectHeader(String name, String value) {
            getHeaders(HttpMethod.CONNECT.value()).add(name, value);
            return this;
        }

        public Builder setPatchHeader(String name, String value) {
            getHeaders(HttpMethod.PATCH.value()).set(name, value);
            return this;
        }

        public Builder addPatchHeader(String name, String value) {
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
