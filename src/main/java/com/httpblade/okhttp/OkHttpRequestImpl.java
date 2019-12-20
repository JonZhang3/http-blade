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
import com.httpblade.common.Utils;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class OkHttpRequestImpl extends AbstractRequest<OkHttpRequestImpl> {

    private Request.Builder builder = new Request.Builder();
    private HttpUrl url;
    private HttpUrl.Builder urlBuilder;
    private String path;
    private HttpMethod method;
    private OkHttpClient.Builder customClientBuilder;
    private OkHttpClient nowClient;

    public OkHttpRequestImpl(final HttpClient client) {
        super(client);
        nowClient = (OkHttpClient) client.raw();
        Defaults.setDefaultHeaders(headers);
    }

    @Override
    public OkHttpRequestImpl url(String url) {
        String resultUrl = configUrl(url);
        this.url = HttpUrl.parse(resultUrl);
        if (this.url == null) {
            throw new HttpBladeException("the url is error");
        }
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
        if(this.url == null) {
            throw new HttpBladeException("You must first specify Http URL.");
        }
        if (path == null) {
            this.path = this.url.encodedPath();
        }
        path = path.replaceAll("%7B" + name + "%7D", value);
        return this;
    }

    @Override
    public OkHttpRequestImpl setQuery(String name, String value) {
        if(this.url == null) {
            throw new HttpBladeException("You must first specify Http URL.");
        }

        return this;
    }

    @Override
    public OkHttpRequestImpl addQuery(String name, String value) {
        return this;
    }

    @Override
    public OkHttpRequestImpl setEncodedQuery(String encodedName, String encodedValue) {
        if(this.url == null) {
            throw new HttpBladeException("You must first specify Http URL.");
        }

        return this;
    }

    @Override
    public OkHttpRequestImpl addEncodedQuery(String encodedName, String encodedValue) {
        return this;
    }

    @Override
    public OkHttpRequestImpl noProxy() {
        if (customClientBuilder == null) {
            customClientBuilder = nowClient.newBuilder();
        }
        customClientBuilder.proxy(null);
        return this;
    }

    @Override
    public OkHttpRequestImpl proxy(String host, int port) {
        if (customClientBuilder == null) {
            customClientBuilder = nowClient.newBuilder();
        }
        if (Utils.isNotEmpty(host)) {
            customClientBuilder.proxy(Proxy.toJavaProxy(new Proxy(host, port)));
        }
        return this;
    }

    @Override
    public OkHttpRequestImpl proxy(String host, int port, String username, String password) {
        if (customClientBuilder == null) {
            customClientBuilder = nowClient.newBuilder();
        }
        if (Utils.isNotEmpty(host)) {
            customClientBuilder.proxy(Proxy.newJavaProxy(host, port));
            if (username != null && password != null) {
                customClientBuilder.proxyAuthenticator(OkHttpClientImpl.createAuthenticator(username, password));
            }
        }
        return this;
    }

    @Override
    public OkHttpRequestImpl connectTimeout(long time, TimeUnit unit) {
        if (time >= 0) {
            if (customClientBuilder == null) {
                customClientBuilder = nowClient.newBuilder();
            }
            customClientBuilder.connectTimeout(time, unit);
        }
        return this;
    }

    @Override
    public OkHttpRequestImpl readTimeout(long time, TimeUnit unit) {
        if (time >= 0) {
            if (customClientBuilder == null) {
                customClientBuilder = nowClient.newBuilder();
            }
            customClientBuilder.readTimeout(time, unit);
        }
        return this;
    }

    @Override
    public OkHttpRequestImpl writeTimeout(long time, TimeUnit unit) {
        if (time >= 0) {
            if (customClientBuilder == null) {
                customClientBuilder = nowClient.newBuilder();
            }
            customClientBuilder.writeTimeout(time, unit);
        }
        return this;
    }

    @Override
    public OkHttpRequestImpl maxRedirectCount(int maxCount) {
        if (maxCount > 0) {
            if (customClientBuilder == null) {
                customClientBuilder = nowClient.newBuilder();
            }
            List<Interceptor> interceptors = customClientBuilder.interceptors();
            interceptors.removeIf(interceptor -> interceptor.getClass().equals(RedirectInterceptor.class));
            customClientBuilder.addInterceptor(new RedirectInterceptor(maxCount));
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

    @Override
    public Response request() {
        OkHttpClient client = buildClient();
        Request request = this.build();
        try {
            return new OkHttpResponseImpl(client.newCall(request).execute());
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(final Callback callback) {
        OkHttpClient client = buildClient();
        Request request = this.build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.error(e);
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (callback != null) {
                    callback.success(new OkHttpResponseImpl(response));
                }
            }
        });
    }

    private OkHttpClient buildClient() {
        if (customClientBuilder != null) {
            return customClientBuilder.build();
        }
        return nowClient;
    }

    private Request build() {
        if (method == null) {
            throw new HttpBladeException("You must specify a http method");
        }
        HttpUrl.Builder urlBuilder = this.url.newBuilder();
        if (path != null) {
            urlBuilder.encodedPath(path);
        }
        // 如果请求方法允许有请求体
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
        builder.headers(createHeaders());
        builder.url(urlBuilder.build());
        return builder.build();
    }

    private Headers createHeaders() {
        final Headers.Builder headerBuilder = new Headers.Builder();
        BiConsumer<String, List<String>> action = (name, values) -> {
            for (String value : values) {
                headerBuilder.add(name, value);
            }
        };
        com.httpblade.common.Headers commonHeaders = this.client.headers();
        if (commonHeaders != null) {
            commonHeaders.forEach(action);
        }
        com.httpblade.common.Headers methodHeaders = this.client.headers(this.method.value());
        if (methodHeaders != null) {
            methodHeaders.forEach(action);
        }
        this.headers.forEach(action);
        return headerBuilder.build();
    }

    public static void main(String[] args) {
        HttpUrl url = HttpUrl.parse("http://www.baidu.com?a=张建东");
    }

}
