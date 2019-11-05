package com.httpblade.apachehttp;

import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.common.Defaults;
import com.httpblade.common.GlobalProxyAuth;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;
import org.apache.http.client.config.RequestConfig;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.Authenticator;
import java.util.concurrent.TimeUnit;

public class ApacheHttpClientBuilderImpl implements com.httpblade.base.HttpClientBuilder<ApacheHttpClientBuilderImpl> {

    HttpClientBuilder clientBuilder;
    RequestConfig.Builder requestConfigBuilder;
    long writeTimeout = Defaults.WRITE_TIMEOUT;
    Headers globalHeaders = new Headers();
    CookieHome cookieHome;
    Proxy proxy;
    HostnameVerifier hostnameVerifier;
    SSLSocketFactory sslSocketFactory;

    public ApacheHttpClientBuilderImpl() {
        clientBuilder = HttpClients.custom();
        requestConfigBuilder = RequestConfig.custom();
        setDefault();
    }

    private void setDefault() {
        clientBuilder.disableAutomaticRetries();
        clientBuilder.disableCookieManagement();
        requestConfigBuilder.setConnectTimeout(Defaults.CONNECT_TIMEOUT);
        requestConfigBuilder.setSocketTimeout(Defaults.READ_TIMEOUT);
        requestConfigBuilder.setRedirectsEnabled(true);
        requestConfigBuilder.setMaxRedirects(Defaults.MAX_REDIRECT_COUNT);
        globalHeaders.set(HttpHeader.USER_AGENT, Defaults.USER_AGENT_STRING);
    }

    @Override
    public ApacheHttpClientBuilderImpl connectTimeout(long time, TimeUnit unit) {
        if (time > 0) {
            requestConfigBuilder.setConnectTimeout((int) unit.toMillis(time));
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl readTimeout(long time, TimeUnit unit) {
        if (time > 0) {
            requestConfigBuilder.setSocketTimeout((int) unit.toMillis(time));
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl writeTimeout(long time, TimeUnit unit) {
        if (time > 0) {
            this.writeTimeout = unit.toMillis(time);
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl cookieHome(final CookieHome cookieHome) {
        this.cookieHome = cookieHome;
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl maxRedirectCount(int max) {
        if (max <= 0) {
            requestConfigBuilder.setRedirectsEnabled(false);
        } else {
            requestConfigBuilder.setRedirectsEnabled(true);
            requestConfigBuilder.setMaxRedirects(max);
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl hostnameVerifier(HostnameVerifier hostnameVerifier) {
        clientBuilder.setSSLHostnameVerifier(hostnameVerifier);
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl sslSocketFactory(final SSLSocketFactoryBuilder builder) {
        if (builder != null) {
            clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(builder.buildContext()));
            this.sslSocketFactory = builder.build();
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl setDefaultHeader(String name, String value) {
        globalHeaders.set(name, value);
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl addDefaultHeader(String name, String value) {
        globalHeaders.add(name, value);
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl proxy(String host, int port) {
        this.proxy = new Proxy(host, port);
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl proxy(String host, int port, String username, String password) {
        this.proxy = new Proxy(host, port, username, password);
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl globalProxyAuth(String username, String password) {
        Authenticator.setDefault(new GlobalProxyAuth(username, password));
        return this;
    }

    @Override
    public HttpClient build() {
        return new ApacheHttpClientImpl(this);
    }

}
