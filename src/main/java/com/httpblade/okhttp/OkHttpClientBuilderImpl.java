package com.httpblade.okhttp;

import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.base.HttpClientBuilder;
import com.httpblade.common.Headers;
import com.httpblade.common.SSLSocketFactoryBuilder;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OkHttpClientBuilderImpl implements HttpClientBuilder<OkHttpClientBuilderImpl> {

    OkHttpClient.Builder builder;
    int maxRedirectCount;
    Headers globalHeaders = new Headers();

    public OkHttpClientBuilderImpl() {
        builder = new OkHttpClient.Builder();
        builder.followRedirects(false);
    }

    @Override
    public OkHttpClientBuilderImpl connectTimeout(long time, TimeUnit unit) {
        builder.connectTimeout(time, unit);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl readTimeout(long time, TimeUnit unit) {
        builder.readTimeout(time, unit);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl writeTimeout(long time, TimeUnit unit) {
        builder.writeTimeout(time, unit);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl maxRedirectCount(int max) {
        this.maxRedirectCount = max;
        builder.followRedirects(false);
        builder.followSslRedirects(false);
        List<Interceptor> interceptors = builder.interceptors();
        interceptors.removeIf(interceptor -> interceptor.getClass().equals(RedirectInterceptor.class));
        builder.addInterceptor(new RedirectInterceptor(max));
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl hostnameVerifier(HostnameVerifier hostnameVerifier) {
        builder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl sslSocketFactory(SSLSocketFactoryBuilder builder) {
        if(builder != null) {
            this.builder.sslSocketFactory(builder.build(), builder.getTrustManager());
        }
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl setDefaultHeader(String name, String value) {
        globalHeaders.set(name, value);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl addDefaultHeader(String name, String value) {
        globalHeaders.add(name, value);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl proxy(Proxy proxy) {
        builder.proxy(proxy);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl cookieHome(CookieHome cookieHome) {
        if (cookieHome != null) {
            builder.cookieJar(new DefaultCookieJar(cookieHome));
        }
        return this;
    }

    @Override
    public HttpClient build() {
        return new OkHttpClientImpl(this);
    }

}
