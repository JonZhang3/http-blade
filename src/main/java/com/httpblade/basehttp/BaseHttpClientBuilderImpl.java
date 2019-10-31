package com.httpblade.basehttp;

import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.base.HttpClientBuilder;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class BaseHttpClientBuilderImpl implements HttpClientBuilder<BaseHttpClientBuilderImpl> {

    long connectTimeout = Defaults.CONNECT_TIMEOUT;
    long readTimeout = Defaults.READ_TIMEOUT;
    long writeTimeout = Defaults.WRITE_TIMEOUT;
    int maxRedirectCount = Defaults.MAX_REDIRECT_COUNT;
    CookieHome cookieHome;
    Headers globalHeaders = new Headers();
    Proxy proxy;
    HostnameVerifier hostnameVerifier;
    SSLSocketFactory sslSocketFactory;

    public BaseHttpClientBuilderImpl() {

    }

    @Override
    public BaseHttpClientBuilderImpl connectTimeout(long time, TimeUnit unit) {
        if (time > 0) {
            connectTimeout = unit.toMillis(time);
        }
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl readTimeout(long time, TimeUnit unit) {
        if (time > 0) {
            readTimeout = unit.toMillis(time);
        }
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl writeTimeout(long time, TimeUnit unit) {
        if (time > 0) {
            writeTimeout = unit.toMillis(time);
        }
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl cookieHome(CookieHome cookieHome) {
        this.cookieHome = cookieHome;
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl maxRedirectCount(int max) {
        if(max <= 0) {
            max = 0;
        }
        this.maxRedirectCount = max;
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl sslSocketFactory(SSLSocketFactoryBuilder builder) {
        if(builder != null) {
            this.sslSocketFactory = builder.build();
        }
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl setDefaultHeader(String name, String value) {
        globalHeaders.set(name, value);
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl addDefaultHeader(String name, String value) {
        globalHeaders.add(name, value);
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public HttpClient build() {
        return new BaseHttpClientImpl(this);
    }

}
