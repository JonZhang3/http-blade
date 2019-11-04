package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class BaseHttpRequestImpl extends AbstractRequest<BaseHttpRequestImpl> {

    private String url;
    private HttpMethod method;

    public BaseHttpRequestImpl() {

    }

    @Override
    public BaseHttpRequestImpl url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public BaseHttpRequestImpl method(HttpMethod method) {
        if(HttpMethod.PATCH == method) {
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
    public BaseHttpRequestImpl addHeader(String name, String value) {
        headers.add(name, value);
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
    public BaseHttpRequestImpl pathParam(String name, String value) {
        return null;
    }

    @Override
    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    Headers getHeaders() {
        return headers;
    }

}
