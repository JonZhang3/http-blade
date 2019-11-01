package com.httpblade.apachehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.Callback;
import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.base.Request;
import com.httpblade.base.Response;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class ApacheHttpClientImpl implements HttpClient {

    private CloseableHttpClient client;
    private RequestConfig requestConfig;
    private long writeTimeout = Defaults.WRITE_TIMEOUT;
    private Headers globalHeaders;
    private CookieHome cookieHome;
    private AsyncTaskExecutor asyncExecutor = new AsyncTaskExecutor();

    public ApacheHttpClientImpl() {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        requestConfig = RequestConfig.custom()
            .setConnectTimeout(Defaults.CONNECT_TIMEOUT)
            .setSocketTimeout(Defaults.READ_TIMEOUT)
            .setRedirectsEnabled(true)
            .setMaxRedirects(Defaults.MAX_REDIRECT_COUNT)
            .build();
        clientBuilder.setDefaultRequestConfig(requestConfig);
        clientBuilder.disableCookieManagement();
        client = clientBuilder.build();
    }

    ApacheHttpClientImpl(ApacheHttpClientBuilderImpl clientBuilder) {
        this.requestConfig = clientBuilder.requestConfigBuilder.build();
        clientBuilder.clientBuilder.setDefaultRequestConfig(this.requestConfig);
        this.writeTimeout = clientBuilder.writeTimeout;
        this.client = clientBuilder.clientBuilder.build();
        this.globalHeaders = clientBuilder.globalHeaders;
        this.cookieHome = clientBuilder.cookieHome;
    }

    @Override
    public Response request(Request request) {
        ApacheHttpRequestImpl requestImpl = (ApacheHttpRequestImpl) request;
        try {
            CloseableHttpResponse response = client.execute(requestImpl.build(this.globalHeaders, this.cookieHome));
            return new ApacheHttpResponseImpl(response, request.getUrl(), this.cookieHome);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Request request, Callback callback) {
        ApacheHttpRequestImpl requestImpl = (ApacheHttpRequestImpl) request;
        HttpUriRequest httpUriRequest = requestImpl.build(this.globalHeaders, this.cookieHome);
        asyncExecutor.enqueue(new Task(callback) {
            @Override
            public void execute() {
                try {
                    CloseableHttpResponse response = client.execute(httpUriRequest);
                    callback.success(new ApacheHttpResponseImpl(response, request.getUrl(), cookieHome));
                } catch (IOException e) {
                    if (callback != null) {
                        callback.error(e);
                    }
                }
            }
        });
    }

    @Override
    public long connectTimeout() {
        return requestConfig.getConnectTimeout();
    }

    @Override
    public long readTimeout() {
        return requestConfig.getSocketTimeout();
    }

    @Override
    public long writeTimeout() {
        return writeTimeout;
    }

    @Override
    public int maxRedirectCount() {
        return requestConfig.getMaxRedirects();
    }

    @Override
    public CookieHome cookieHome() {
        return cookieHome;
    }

}
