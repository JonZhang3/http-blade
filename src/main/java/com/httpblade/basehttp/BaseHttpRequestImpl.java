package com.httpblade.basehttp;

import com.httpblade.Callback;
import com.httpblade.HttpBladeException;
import com.httpblade.AbstractRequest;
import com.httpblade.HttpClient;
import com.httpblade.Response;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.Utils;
import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseHttpRequestImpl extends AbstractRequest<BaseHttpRequestImpl> {

    private static AsyncTaskExecutor asyncExecutor = new AsyncTaskExecutor();

    private HttpMethod method;
    private HttpUrl url;
    private BaseHttpConnection connection = new BaseHttpConnection();

    public BaseHttpRequestImpl(HttpClient client) {
        super(client);
        connection.setHostnameVerifier(client.hostnameVerifier()).setConnectTimeout((int) client.connectTimeout()).setReadTimeout((int) client.readTimeout()).setCookieHome(client.cookieHome()).setMaxRedirectCount(client.maxRedirectCount());
    }

    @Override
    public BaseHttpRequestImpl url(String url) {
        String resultUrl = this.configUrl(url);
        try {
            this.url = new HttpUrl(resultUrl);
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
        String path = url.getPath();
        if (path == null) {
            path = "/";
        }
        url.setPath(path.replaceAll("\\{ + name + \\}", Utils.encode(value, "UTF-8")));
        return this;
    }

    @Override
    public BaseHttpRequestImpl queryString(String name, String value) {
        url.getQueries().add(name, Utils.encode(value, StandardCharsets.UTF_8.name()));
        return this;
    }

    @Override
    public BaseHttpRequestImpl queryString(String name, String value, boolean encoded) {
        if (encoded) {
            url.getQueries().add(name, value);
        } else {
            queryString(name, value);
        }
        return this;
    }

    @Override
    public BaseHttpRequestImpl proxy(Proxy proxy) {

        return this;
    }

    @Override
    public BaseHttpRequestImpl proxy(String host, int port) {
        return this;
    }

    @Override
    public BaseHttpRequestImpl proxy(String host, int port, String username, String password) {
        return this;
    }

    @Override
    public BaseHttpRequestImpl connectTimeout(long time, TimeUnit unit) {
        int mills = Utils.checkDuration("connect timeout", time, unit);
        if (mills > 0) {
            connection.setConnectTimeout(mills);
        }
        return this;
    }

    @Override
    public BaseHttpRequestImpl readTimeout(long time, TimeUnit unit) {
        int mills = Utils.checkDuration("read timeout", time, unit);
        if (mills > 0) {
            connection.setReadTimeout(mills);
        }
        return this;
    }

    @Override
    public BaseHttpRequestImpl writeTimeout(long time, TimeUnit unit) {
        return this;
    }

    @Override
    public BaseHttpRequestImpl maxRedirectCount(int maxCount) {
        if (maxCount < 0) {
            maxCount = 0;
        }
        connection.setMaxRedirectCount(maxCount);
        return this;
    }

    @Override
    public Response request() {
        try {
            build();
            return connection.execute();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Callback callback) {
        build();
        asyncExecutor.enqueue(new Task(callback) {
            @Override
            public void execute() {
                try {
                    Response response = connection.execute();
                    callback.success(response);
                } catch (Exception e) {
                    if (callback != null) {
                        callback.error(e);
                    }
                }
            }
        });
    }

    @Override
    public URL getUrl() {
        return url.url();
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    void build() {
        if (this.method == null) {
            throw new HttpBladeException("must specify a http method");
        }
        connection.setUrl(url).setMethod(getMethod()).setSSLSocketFactory(null).setForm(form).setBody(body).setCharset(charset);
    }

}
