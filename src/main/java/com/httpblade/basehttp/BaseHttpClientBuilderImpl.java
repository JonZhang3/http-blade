package com.httpblade.basehttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpClientBuilder;
import com.httpblade.common.Defaults;
import com.httpblade.common.GlobalProxyAuth;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.Authenticator;
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
    public BaseHttpClientBuilderImpl proxy(String host, int port) {
        this.proxy = new Proxy(host, port);
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl proxy(String host, int port, String username, String password) {
        this.proxy = new Proxy(host, port, username, password);
        return this;
    }

    @Override
    public BaseHttpClientBuilderImpl globalProxyAuth(String username, String password) {
        Authenticator.setDefault(new GlobalProxyAuth(username, password));
        return this;
    }

    @Override
    public HttpClient build() {
        return new BaseHttpClientImpl(this);
    }

}
