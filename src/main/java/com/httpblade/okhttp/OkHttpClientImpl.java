package com.httpblade.okhttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.base.Request;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpClientImpl implements HttpClient {

    private OkHttpClient client;
    private int maxRedirectCount = Defaults.MAX_REDIRECT_COUNT;
    private Headers globalHeaders;

    OkHttpClientImpl() {
        client = new OkHttpClientBuilderImpl()
            .connectTimeout(Defaults.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(Defaults.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(Defaults.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .maxRedirectCount(maxRedirectCount)
            .builder
            .build();
    }

    OkHttpClientImpl(OkHttpClientBuilderImpl clientBuilder) {
        this.client = clientBuilder.builder.build();
        this.maxRedirectCount = clientBuilder.maxRedirectCount;
        this.globalHeaders = clientBuilder.globalHeaders;
    }

    @Override
    public com.httpblade.base.Response request(Request request) throws HttpBladeException {
        OkHttpRequestImpl requestImpl = (OkHttpRequestImpl) request;
        try {
            Response response = client.newCall(requestImpl.build(this.globalHeaders)).execute();
            return new OkHttpResponseImpl(response);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Request request, com.httpblade.base.Callback callback) {
        OkHttpRequestImpl requestImpl = (OkHttpRequestImpl) request;
        client.newCall(requestImpl.build(this.globalHeaders)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.error(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    callback.success(new OkHttpResponseImpl(response));
                }
            }
        });
    }

    @Override
    public long connectTimeout() {
        return client.connectTimeoutMillis();
    }

    @Override
    public long readTimeout() {
        return client.readTimeoutMillis();
    }

    @Override
    public long writeTimeout() {
        return client.writeTimeoutMillis();
    }

    @Override
    public int maxRedirectCount() {
        return maxRedirectCount;
    }

    @Override
    public CookieHome cookieHome() {
        DefaultCookieJar cookieJar = (DefaultCookieJar) client.cookieJar();
        CookieHome cookieHome = null;
        if (cookieJar != null) {
            cookieHome = cookieJar.getCookieHome();
        }
        return cookieHome;
    }
}
