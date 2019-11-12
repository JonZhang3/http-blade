package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.common.Defaults;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.HttpUrl;
import com.httpblade.common.Utils;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseHttpRequestImpl extends AbstractRequest<BaseHttpRequestImpl> {

    private HttpUrl httpUrl;
    private HttpMethod method;

    public BaseHttpRequestImpl() {
        Defaults.setDefaultHeaders(this.headers);
    }

    @Override
    public BaseHttpRequestImpl url(String url) {
        this.httpUrl = new HttpUrl(url);
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
        httpUrl.setPathVariable(name, value);
        return this;
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
            .setProxy(client.javaProxy())
            .setHeaders(headers.merge(client.globalHeaders()))
            .setConnectTimeout((int) client.connectTimeout())
            .setReadTimeout((int) client.readTimeout())
            .setHostnameVerifier(client.hostnameVerifier())
            .setSSLSocketFactory(client.sslSocketFactory())
            .setCookieHome(client.cookieHome())
            .setForm(form)
            .setBody(body)
            .setCharset(charset)
            .setMaxRedirectCount(client.maxRedirectCount());
    }

}
