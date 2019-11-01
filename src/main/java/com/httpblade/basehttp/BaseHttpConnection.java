package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.base.Response;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.function.BiConsumer;

class BaseHttpConnection {

    private static final int DEFAULT_CHUNK_LENGTH = 1024 * 2;

    private HttpURLConnection conn;
    private URL url;
    private HttpMethod method;
    private Proxy proxy;
    private int connectTimeout;
    private int readTimeout;
    private int maxRedirectCount;
    private Headers headers = new Headers();
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory ssf;
    private CookieHome cookieHome;

    BaseHttpConnection setUrl(String url) {
        try {
            this.url = new URL(Utils.encodeBlank(url));
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
        return this;
    }

    BaseHttpConnection setUrl(URL url) {
        this.url = url;
        return this;
    }

    BaseHttpConnection setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    BaseHttpConnection setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    BaseHttpConnection setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    BaseHttpConnection setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    BaseHttpConnection setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    BaseHttpConnection setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    BaseHttpConnection setSSLSocketFactory(SSLSocketFactory ssf) {
        this.ssf = ssf;
        return this;
    }

    BaseHttpConnection setCookieHome(CookieHome cookieHome) {
        this.cookieHome = cookieHome;
        return this;
    }

    BaseHttpConnection setMaxRedirectCount(int maxRedirectCount) {
        this.maxRedirectCount = maxRedirectCount;
        return this;
    }

    void connect() throws IOException {
        if (conn != null) {
            conn.connect();
        }
    }

    HttpURLConnection getConnection() {
        return this.conn;
    }

    CookieHome getCookieHome() {
        return this.cookieHome;
    }

    URL getUrl() {
        return this.url;
    }

    Response response() throws IOException {
        if (this.conn != null) {
            if (this.maxRedirectCount < 1) {
                return new BaseHttpResponseImpl(this);
            }
            if (this.conn.getInstanceFollowRedirects()) {

            }
        }
        return null;
    }

    void close() {
        if (conn != null) {
            conn.disconnect();
        }
    }

    void build(Headers globalHeaders) {
        try {
            conn = opneConnection(this.url, this.proxy);
            conn.setUseCaches(false);
            conn.setChunkedStreamingMode(DEFAULT_CHUNK_LENGTH);
            conn.setRequestMethod(method.value());
            conn.setInstanceFollowRedirects(this.maxRedirectCount >= 1);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            configHttps(conn, this.hostnameVerifier, this.ssf);

            addHeaders(conn, globalHeaders, headers);
            addCookie(conn, this.url, this.cookieHome);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    private static HttpURLConnection opneConnection(URL url, Proxy proxy) throws IOException {
        URLConnection conn = proxy == null ? url.openConnection() : url.openConnection(proxy);
        if (conn instanceof HttpURLConnection) {
            return (HttpURLConnection) conn;
        }
        throw new HttpBladeException("");
    }

    private static void addHeaders(HttpURLConnection conn, Headers globalHeaders, Headers headers) {
        BiConsumer<String, List<String>> consumer = (name, values) -> {
            if (values.size() == 1) {
                conn.setRequestProperty(name, values.get(0));
            } else {
                for (String value : values) {
                    conn.addRequestProperty(name, value);
                }
            }
        };
        if (globalHeaders != null) {
            globalHeaders.forEach(consumer);
        }
        if (headers != null) {
            headers.forEach(consumer);
        }
    }

    private static void configHttps(HttpURLConnection conn, HostnameVerifier hostnameVerifier, SSLSocketFactory ssf) {
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection mConn = (HttpsURLConnection) conn;
            if (hostnameVerifier != null) {
                mConn.setHostnameVerifier(hostnameVerifier);
            }
            if (ssf != null) {
                mConn.setSSLSocketFactory(ssf);
            }
        }
    }

    private static void addCookie(HttpURLConnection conn, URL url, CookieHome cookieHome) {
        if (cookieHome != null) {
            List<Cookie> cookies = cookieHome.load(url);
            if (cookies != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, len = cookies.size(); i < len; i++) {
                    Cookie cookie = cookies.get(i);
                    sb.append(cookie.name()).append("=").append(cookie.value());
                    if (i < (len - 1)) {
                        sb.append(";");
                    }
                }
                conn.setRequestProperty(HttpHeader.COOKIE, sb.toString());
            }
        }
    }

}
