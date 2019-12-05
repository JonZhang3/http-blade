package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.Callback;
import com.httpblade.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.Request;
import com.httpblade.Response;
import com.httpblade.common.Constants;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Utils;
import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Proxy;

public class BaseHttpClientImpl implements HttpClient {

    private long connectTimeout = Constants.CONNECT_TIMEOUT;
    private long readTimeout = Constants.READ_TIMEOUT;
    private long writeTimeout = Constants.WRITE_TIMEOUT;
    private int maxRedirectCount = Constants.MAX_REDIRECT_COUNT;
    private CookieHome cookieHome;
    private Headers globalHeaders;
    private Proxy proxy;
    private com.httpblade.common.Proxy commonProxy;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private AsyncTaskExecutor asyncExecutor = new AsyncTaskExecutor();

    public BaseHttpClientImpl() {
    }

    BaseHttpClientImpl(BaseHttpClientBuilderImpl clientBuilder) {
        this.connectTimeout = clientBuilder.connectTimeout;
        this.readTimeout = clientBuilder.readTimeout;
        this.writeTimeout = clientBuilder.writeTimeout;
        this.maxRedirectCount = clientBuilder.maxRedirectCount;
        this.cookieHome = clientBuilder.cookieHome;
        this.globalHeaders = clientBuilder.globalHeaders;
        this.commonProxy = clientBuilder.proxy;
        if (clientBuilder.proxy != null) {
            this.proxy = com.httpblade.common.Proxy.toJavaProxy(this.commonProxy);
            if (this.commonProxy.hasAuth()) {
                globalHeaders.set(HttpHeader.PROXY_AUTHORIZATION,
                    Utils.basicAuthString(clientBuilder.proxy.getUsername(), clientBuilder.proxy.getPassword()));
            }
        }
        this.hostnameVerifier = clientBuilder.hostnameVerifier;
        this.sslSocketFactory = clientBuilder.sslSocketFactory;
    }

    @Override
    public Response request(Request request) {
        BaseHttpRequestImpl requestImpl = (BaseHttpRequestImpl) request;
        try {
            return requestImpl.build(this).execute();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Request request, Callback callback) {
        BaseHttpRequestImpl requestImpl = (BaseHttpRequestImpl) request;
        final BaseHttpConnection connection = requestImpl.build(this);
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
    public long connectTimeout() {
        return connectTimeout;
    }

    @Override
    public long readTimeout() {
        return readTimeout;
    }

    @Override
    public long writeTimeout() {
        return writeTimeout;
    }

    @Override
    public int maxRedirectCount() {
        return maxRedirectCount;
    }

    @Override
    public CookieHome cookieHome() {
        return cookieHome;
    }

    @Override
    public com.httpblade.common.Proxy proxy() {
        return commonProxy;
    }

    @Override
    public HostnameVerifier hostnameVerifier() {
        return hostnameVerifier;
    }

    @Override
    public SSLSocketFactory sslSocketFactory() {
        return sslSocketFactory;
    }

    Headers globalHeaders() {
        return globalHeaders;
    }

    Proxy javaProxy() {
        return proxy;
    }

}
