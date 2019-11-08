package com.httpblade.okhttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class OkHttpRequestImpl extends AbstractRequest<OkHttpRequestImpl> {

    private Request.Builder builder = new Request.Builder();
    private HttpUrl url;
    private String path;
    private HttpMethod method;

    @Override
    public OkHttpRequestImpl url(String url) {
        this.url = HttpUrl.parse(url);
        if (this.url == null) {
            throw new HttpBladeException("the url is null or error");
        }
        this.path = this.url.encodedPath();
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
    public OkHttpRequestImpl pathVariable(String name, String value) {
        if (path != null) {
            path = path.replaceAll("\\{ + name + \\}", value);
        }
        return this;
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
            throw new HttpBladeException("must specify a http method");
        }
        HttpUrl.Builder urlBuilder = this.url.newBuilder();
        urlBuilder.addEncodedPathSegment(path);
        if (HttpMethod.requiresRequestBody(method)) {
            if (body != null) {
                builder.method(method.value(), body.createOkhttpRequestBody(header(HttpHeader.CONTENT_TYPE),
                    this.charset));
            } else {
                RequestBody body = OkHttpFormUtil.createRequestBody(form);
                builder.method(method.value(), body);
                headers.set(HttpHeader.CONTENT_TYPE, form.contentType());
            }
        } else {
            OkHttpFormUtil.createGetUrl(form, urlBuilder);
            builder.get();
        }
        setBasicAuth();
        builder.headers(createHeaders(globalHeaders));
        builder.url(urlBuilder.build());
        return builder.build();
    }

    private Headers createHeaders(com.httpblade.common.Headers globalHeaders) {
        final Headers.Builder headerBuilder = new Headers.Builder();
        this.headers.merge(globalHeaders);
        this.headers.forEach((name, values) -> {
            for (String value : values) {
                headerBuilder.add(name, value);
            }
        });
        return headerBuilder.build();
    }

}
