package com.httpblade.okhttp;

import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.base.HttpClientBuilder;
import com.httpblade.common.Defaults;
import com.httpblade.common.GlobalProxyAuth;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OkHttpClientBuilderImpl implements HttpClientBuilder<OkHttpClientBuilderImpl> {

    OkHttpClient.Builder builder;
    int maxRedirectCount;
    Headers globalHeaders = new Headers();
    Proxy proxy;

    public OkHttpClientBuilderImpl() {
        builder = new OkHttpClient.Builder();
        setDefaults();
    }

    private void setDefaults() {
        connectTimeout(Defaults.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        readTimeout(Defaults.READ_TIMEOUT, TimeUnit.MILLISECONDS);
        writeTimeout(Defaults.WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.followRedirects(false);
        builder.followSslRedirects(false);
        maxRedirectCount(Defaults.MAX_REDIRECT_COUNT);
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
        if (max > 0) {
            this.maxRedirectCount = max;
            List<Interceptor> interceptors = builder.interceptors();
            interceptors.removeIf(interceptor -> interceptor.getClass().equals(RedirectInterceptor.class));
            builder.addInterceptor(new RedirectInterceptor(max));
        }
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl hostnameVerifier(HostnameVerifier hostnameVerifier) {
        builder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    public OkHttpClientBuilderImpl socketFactory(SocketFactory socketFactory) {
        this.builder.socketFactory(socketFactory);
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl sslSocketFactory(SSLSocketFactoryBuilder builder) {
        if (builder != null) {
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
        if (proxy != null) {
            builder.proxy(Proxy.toJavaProxy(proxy));
            if (proxy.hasAuth()) {
                builder.proxyAuthenticator(createAuthenticator(proxy.getUsername(), proxy.getPassword()));
            }
        }
        this.proxy = proxy;
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl proxy(String host, int port) {
        this.proxy = new Proxy(host, port);
        builder.proxy(Proxy.toJavaProxy(this.proxy));
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl proxy(String host, int port, String username, String password) {
        this.proxy = new Proxy(host, port, username, password);
        builder.proxy(Proxy.toJavaProxy(proxy));
        builder.proxyAuthenticator(createAuthenticator(username, password));
        return this;
    }

    @Override
    public OkHttpClientBuilderImpl globalProxyAuth(String username, String password) {
        java.net.Authenticator.setDefault(new GlobalProxyAuth(username, password));
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

    private static Authenticator createAuthenticator(String username, String password) {
        return (route, response) -> {
            String credential = Credentials.basic(username, password, StandardCharsets.UTF_8);
            return response.request().newBuilder()
                .header(HttpHeader.PROXY_AUTHORIZATION, credential).build();
        };
    }

}
