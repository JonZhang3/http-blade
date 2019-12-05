package com.httpblade.okhttp;

import com.httpblade.HttpBladeException;
import com.httpblade.AbstractRequest;
import com.httpblade.Callback;
import com.httpblade.HttpClient;
import com.httpblade.Response;
import com.httpblade.common.Defaults;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;
import com.httpblade.common.Utils;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.net.ssl.HostnameVerifier;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpRequestImpl extends AbstractRequest<OkHttpRequestImpl> {

    private Request.Builder builder = new Request.Builder();
    private HttpUrl url;
    private String path;
    private HttpMethod method;

    public OkHttpRequestImpl(HttpClient client) {
        super(client);
        Defaults.setDefaultHeaders(headers);
    }

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
    public OkHttpRequestImpl setDateHeader(String name, Date date) {
        setHeader(name, Utils.formatHttpDate(date));
        return this;
    }

    @Override
    public OkHttpRequestImpl addHeader(String name, String value) {
        headers.add(name, value);
        return this;
    }

    @Override
    public OkHttpRequestImpl addDateHeader(String name, Date date) {
        addHeader(name, Utils.formatHttpDate(date));
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
            path = path.replaceAll("%7B" + name + "%7D", value);
        }
        return this;
    }

    @Override
    public OkHttpRequestImpl proxy(Proxy proxy) {
        if(proxy != null) {

        }
        return this;
    }

    @Override
    public OkHttpRequestImpl proxy(String host, int port) {
        return this;
    }

    @Override
    public OkHttpRequestImpl proxy(String host, int port, String username, String password) {
        return this;
    }

    @Override
    public OkHttpRequestImpl connectTimeout(long time, TimeUnit unit) {
        if(time >= 0) {

        }
        return this;
    }

    @Override
    public OkHttpRequestImpl readTimeout(long time, TimeUnit unit) {
        if(time >= 0) {

        }
        return null;
    }

    @Override
    public OkHttpRequestImpl writeTimeout(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public OkHttpRequestImpl maxRedirectCount(int maxCount) {
        return null;
    }

    @Override
    public OkHttpRequestImpl hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return null;
    }

    @Override
    public OkHttpRequestImpl sslSocketFactory(SSLSocketFactoryBuilder builder) {
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
        urlBuilder.encodedPath(path);
        if (HttpMethod.requiresRequestBody(method)) {
            if (body != null) {
                builder.method(method.value(), body.createOkhttpRequestBody(header(HttpHeader.CONTENT_TYPE),
                    this.charset));
                if (form.onlyNormalField()) {
                    OkHttpFormUtil.createGetUrl(form, urlBuilder);
                } else {
                    throw new HttpBladeException("You have provided the request body for the request.");
                }
            } else {
                RequestBody body = OkHttpFormUtil.createRequestBody(form);
                builder.method(method.value(), body);
                headers.set(HttpHeader.CONTENT_TYPE, form.contentType());
            }
        } else {
            OkHttpFormUtil.createGetUrl(form, urlBuilder);
            builder.method(method.value(), null);
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
