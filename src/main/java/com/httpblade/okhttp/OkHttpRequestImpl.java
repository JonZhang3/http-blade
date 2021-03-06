package com.httpblade.okhttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import okhttp3.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class OkHttpRequestImpl extends AbstractRequest<OkHttpRequestImpl> {

    private Request.Builder builder = new Request.Builder();
    private HttpUrl url;
    private HttpMethod method;

    @Override
    public OkHttpRequestImpl url(String url) {
        this.url = HttpUrl.parse(url);
        return this;
    }

    @Override
    public OkHttpRequestImpl method(HttpMethod method) {
        this.method = method;
        return this;
    }

    @Override
    public OkHttpRequestImpl setHeader(String name, String value) {
        headers.set(name, value);
        return this;
    }

    @Override
    public OkHttpRequestImpl addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public OkHttpRequestImpl removeHeader(String name) {
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
    public OkHttpRequestImpl pathParam(String name, String value) {
        return null;
    }

    @Override
    public URL getUrl() {
        return this.url == null ? null : this.url.url();
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    Request build(com.httpblade.common.Headers globalHeaders) {
        if (method == null) {
            throw new HttpBladeException("must specify a method");
        }
        HttpUrl.Builder urlBuilder = this.url.newBuilder();
        if (method == HttpMethod.GET) {
            OkHttpFormUtil.createGetUrl(form, urlBuilder);
            builder.get();
        } else {
            if (body != null) {
                builder.method(method.value(), body.createOkhttpRequestBody(header(HttpHeader.CONTENT_TYPE),
                    this.charset));
            } else {
                RequestBody body = OkHttpFormUtil.createRequestBody(form);
                builder.method(method.value(), body);
                headers.set(HttpHeader.CONTENT_TYPE, form.contentType());
            }
        }
        setBasicAuth();
        builder.headers(createHeaders(globalHeaders));
        builder.url(urlBuilder.build());
        return builder.build();
    }

    private Headers createHeaders(com.httpblade.common.Headers globalHeaders) {
        Headers.Builder builder = new Headers.Builder();
        BiConsumer<String, List<String>> consumer = (name, values) -> {
            for (String value : values) {
                builder.add(name, value);
            }
        };
        if (globalHeaders != null) {
            globalHeaders.forEach(consumer);
        }
        this.headers.forEach(consumer);
        return builder.build();
    }

}
