package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.base.Response;
import com.httpblade.common.Body;
import com.httpblade.common.ContentType;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.HttpStatus;
import com.httpblade.common.HttpUrl;
import com.httpblade.common.form.Form;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.BiConsumer;

class BaseHttpConnection {

    private static final int DEFAULT_CHUNK_LENGTH = 1024 * 2;

    private HttpURLConnection conn;
    private HttpUrl httpUrl;
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
    private Form form;
    private Body body;
    private Charset charset;
    private boolean requiresRequestBody = false;

    BaseHttpConnection setUrl(HttpUrl url) {
        this.httpUrl = url;
        return this;
    }

    BaseHttpConnection setMethod(HttpMethod method) {
        this.method = method;
        this.requiresRequestBody = HttpMethod.requiresRequestBody(method);
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

    BaseHttpConnection setForm(Form form) {
        this.form = form;
        return this;
    }

    BaseHttpConnection setBody(Body body) {
        this.body = body;
        return this;
    }

    BaseHttpConnection setCharset(Charset charset) {
        this.charset = charset;
        return this;
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

    Response execute() throws IOException {
        String contentType = this.headers.get(HttpHeader.CONTENT_TYPE);
        if (!requiresRequestBody) {
            form.forEachFields(charset, (index, name, value) -> httpUrl.addQuery(name, value));
        } else if (body != null) {
            if (form.onlyNormalField()) {
                form.forEachFields(charset, (index, name, value) -> httpUrl.addQuery(name, value));
            } else {
                throw new HttpBladeException("You have provided the request body for the request.");
            }
            this.headers.set(HttpHeader.CONTENT_TYPE, getContentType(body, contentType));
        } else {
            this.headers.set(HttpHeader.CONTENT_TYPE, form.contentType());
        }
        this.url = httpUrl.toURL();
        return innerExecute(1);
    }

    private Response innerExecute(int redirectCount) throws IOException {
        initConnection();
        connect();
        int code = conn.getResponseCode();
        switch (code) {
            case HttpStatus.TEMP_REDIRECT:
            case HttpStatus.PERM_REDIRECT:
                if (method != HttpMethod.GET && method != HttpMethod.HEAD) {
                    return new BaseHttpResponseImpl(this);
                }
            case HttpStatus.MULT_CHOICE:
            case HttpStatus.MOVED_PERM:
            case HttpStatus.MOVED_TEMP:
            case HttpStatus.SEE_OTHER:
                if (maxRedirectCount < 1 || redirectCount > maxRedirectCount) {
                    return new BaseHttpResponseImpl(this);
                } else {
                    return innerExecute(++redirectCount);
                }
            default:
                return new BaseHttpResponseImpl(this);
        }
    }

    private void initConnection() throws IOException {
        if (this.conn != null) {
            this.conn.disconnect();
        }
        conn = openConnection(this.url, this.proxy);
        conn.setUseCaches(false);
        conn.setChunkedStreamingMode(DEFAULT_CHUNK_LENGTH);
        conn.setRequestMethod(method.value());
        conn.setInstanceFollowRedirects(this.maxRedirectCount >= 1);
        conn.setDoInput(true);
        if (requiresRequestBody) {
            conn.setDoOutput(true);
            conn.setUseCaches(false);
        }
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        configHttps(conn, this.hostnameVerifier, this.ssf);
        addHeaders(conn, headers);
        addCookie(conn, this.url, this.cookieHome);
    }

    private void connect() throws IOException {
        if (conn == null) {
            throw new NullPointerException("the http connection is null");
        }
        if (HttpMethod.requiresRequestBody(method)) {
            OutputStream out = conn.getOutputStream();
            if (body != null) {
                body.writeTo(out, charset);
            } else {
                form.writeTo(out, charset);
            }
            out.flush();
            //out.close();
        } else {
            conn.connect();
        }
    }

    void close() {
        if (conn != null) {
            conn.disconnect();
        }
    }

    private static HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
        URLConnection conn = proxy == null ? url.openConnection() : url.openConnection(proxy);
        if (conn instanceof HttpURLConnection) {
            return (HttpURLConnection) conn;
        }
        throw new HttpBladeException("the url protocol must be http or https");
    }

    private static void addHeaders(HttpURLConnection conn, Headers headers) {
        BiConsumer<String, List<String>> consumer = (name, values) -> {
            if (values.size() == 1) {
                conn.setRequestProperty(name, values.get(0));
            } else {
                for (String value : values) {
                    conn.addRequestProperty(name, value);
                }
            }
        };
        if (headers != null) {
            headers.forEach(consumer);
        }
        // 增加 connection keep-alive，从而可以复用连接
        conn.setRequestProperty(HttpHeader.CONNECTION, "Keep-Alive");
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

    private static String getContentType(Body body, String contentType) {
        String result = contentType;
        if (result != null) {
            if (body.isString()) {
                result = ContentType.guessContentType(body.getStringData());
            } else {
                result = ContentType.OCTET_STREAM;
            }
        }
        return result;
    }

}
