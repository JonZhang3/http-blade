package com.httpblade.basehttp;

import com.httpblade.Callback;
import com.httpblade.HttpBladeException;
import com.httpblade.AbstractRequest;
import com.httpblade.HttpClient;
import com.httpblade.Response;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.HttpUrl;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;
import com.httpblade.common.Utils;

import javax.net.ssl.HostnameVerifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseHttpRequestImpl extends AbstractRequest<BaseHttpRequestImpl> {

    private HttpUrl httpUrl;
    private HttpMethod method;
    private URL url;
    private String path;
    private String queryString;
    private Map<String, List<String>> queries = new HashMap<>();
    private BaseHttpConnection connection = new BaseHttpConnection();

    public BaseHttpRequestImpl(HttpClient client) {
        super(client);
        connection.setProxy(Proxy.toJavaProxy(client.proxy()));
        connection.setHostnameVerifier(client.hostnameVerifier());

    }

    @Override
    public BaseHttpRequestImpl url(String url) {
        this.httpUrl = new HttpUrl(url);
        try {
            this.url = new URL(url);
            this.path = this.url.getPath();
            if(this.path == null) {
                this.path = "/";
            }
            this.queryString = this.url.getQuery();
            if(this.queryString == null) {
                this.queryString = "";
            }
            Utils.parseQueryString(queryString, queries);
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
        return this;
    }

    @Override
    public BaseHttpRequestImpl method(HttpMethod method) {
        if (HttpMethod.PATCH == method) {
            this.method = HttpMethod.POST;
            this.setHeader(HttpHeader.X_HTTP_METHOD_OVERRIDE, "PATCH");
        } else {
            this.method = method;
        }
        connection.setMethod(method);
        return this;
    }

    @Override
    public BaseHttpRequestImpl setHeader(String name, String value) {
        headers.set(name, value);
        return this;
    }

    @Override
    public BaseHttpRequestImpl setDateHeader(String name, Date date) {
        setHeader(name, Utils.formatHttpDate(date));
        return this;
    }

    @Override
    public BaseHttpRequestImpl addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public BaseHttpRequestImpl addDateHeader(String name, Date date) {
        addHeader(name, Utils.formatHttpDate(date));
        return this;
    }

    @Override
    public BaseHttpRequestImpl removeHeader(String name) {
        headers.remove(name);
        return this;
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.contain(name);
    }

    @Override
    public String header(String name) {
        return headers.get(name);
    }

    @Override
    public List<String> headers(String name) {
        return headers.getList(name);
    }

    @Override
    public Map<String, List<String>> allHeaders() {
        return headers.get();
    }

    @Override
    public BaseHttpRequestImpl pathVariable(String name, String value) {
        //httpUrl.setPathVariable(name, value);
        this.path = path.replaceAll("\\{ + name + \\}", Utils.encode(value, "UTF-8"));
        return this;
    }

    @Override
    public BaseHttpRequestImpl proxy(Proxy proxy) {

        return null;
    }

    @Override
    public BaseHttpRequestImpl proxy(String host, int port) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl proxy(String host, int port, String username, String password) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl connectTimeout(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl readTimeout(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl writeTimeout(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl maxRedirectCount(int maxCount) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return null;
    }

    @Override
    public BaseHttpRequestImpl sslSocketFactory(SSLSocketFactoryBuilder builder) {
        return null;
    }

    @Override
    public Response request() {
        return null;
    }

    @Override
    public void requestAsync(Callback callback) {

    }

    @Override
    public URL getUrl() {
        return httpUrl.toURL();
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    BaseHttpConnection build(BaseHttpClientImpl client) {
        if (this.method == null) {
            throw new HttpBladeException("must specify a http method");
        }
        return new BaseHttpConnection()
            .setUrl(httpUrl)
            .setMethod(getMethod())
            .setProxy(client.javaProxy)
            .setConnectTimeout((int) client.connectTimeout())
            .setReadTimeout((int) client.readTimeout())
            .setHostnameVerifier(client.hostnameVerifier())
            .setSSLSocketFactory(null)
            .setCookieHome(client.cookieHome())
            .setForm(form)
            .setBody(body)
            .setCharset(charset)
            .setMaxRedirectCount(client.maxRedirectCount());
    }

}
