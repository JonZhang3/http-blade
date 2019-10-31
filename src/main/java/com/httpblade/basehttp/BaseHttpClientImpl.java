package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.*;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Proxy;

public class BaseHttpClientImpl implements HttpClient {

    private long connectTimeout = Defaults.CONNECT_TIMEOUT;
    private long readTimeout = Defaults.READ_TIMEOUT;
    private long writeTimeout = Defaults.WRITE_TIMEOUT;
    private int maxRedirectCount = Defaults.MAX_REDIRECT_COUNT;
    private CookieHome cookieHome;
    private Headers globalHeaders;
    private Proxy proxy;
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
        this.proxy = clientBuilder.proxy;
        this.hostnameVerifier = clientBuilder.hostnameVerifier;
        this.sslSocketFactory = clientBuilder.sslSocketFactory;
    }

    @Override
    public Response request(Request request) {
        BaseHttpRequestImpl requestImpl = (BaseHttpRequestImpl) request;
        try {
            return doRequest(createConnection(requestImpl));
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Request request, Callback callback) {
        BaseHttpRequestImpl requestImpl = (BaseHttpRequestImpl) request;
        final BaseHttpConnection connection = createConnection(requestImpl);
        asyncExecutor.enqueue(new Task(callback) {
            @Override
            public void execute() {
                try {
                    Response response = doRequest(connection);
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

    private BaseHttpConnection createConnection(BaseHttpRequestImpl requestImpl) {
        BaseHttpConnection connection = new BaseHttpConnection()
            .setUrl(requestImpl.getUrl())
            .setMethod(requestImpl.getMethod())
            .setProxy(proxy)
            .setHeaders(requestImpl.getHeaders())
            .setConnectTimeout((int) connectTimeout)
            .setReadTimeout((int) readTimeout)
            .setHostnameVerifier(hostnameVerifier)
            .setSSLSocketFactory(sslSocketFactory)
            .setCookieHome(this.cookieHome)
            .setMaxRedirectCount(this.maxRedirectCount);
        connection.build(globalHeaders);
        return connection;
    }

    private Response doRequest(BaseHttpConnection conn) throws IOException {
        conn.connect();
        return conn.response();
    }

}
