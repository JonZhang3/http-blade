package com.httpblade.apachehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.SSLSocketFactoryBuilder;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpInetSocketAddress;;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.HostnameVerifier;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class ApacheHttpClientBuilderImpl implements com.httpblade.base.HttpClientBuilder<ApacheHttpClientBuilderImpl> {

    HttpClientBuilder clientBuilder;
    RequestConfig.Builder requestConfigBuilder;
    long writeTimeout = Defaults.WRITE_TIMEOUT;
    Headers globalHeaders = new Headers();
    CookieHome cookieHome;

    public ApacheHttpClientBuilderImpl() {
        clientBuilder = HttpClients.custom();
        clientBuilder.disableCookieManagement();
        requestConfigBuilder = RequestConfig.custom();
        setDefault();
    }

    private void setDefault() {
        requestConfigBuilder.setConnectTimeout(Defaults.CONNECT_TIMEOUT);
        requestConfigBuilder.setSocketTimeout(Defaults.READ_TIMEOUT);
        requestConfigBuilder.setRedirectsEnabled(true);
        requestConfigBuilder.setMaxRedirects(Defaults.MAX_REDIRECT_COUNT);
    }

    @Override
    public ApacheHttpClientBuilderImpl connectTimeout(long time, TimeUnit unit) {
        if(time > 0) {
            requestConfigBuilder.setConnectTimeout((int) unit.toMillis(time));
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl readTimeout(long time, TimeUnit unit) {
        if(time > 0) {
            requestConfigBuilder.setSocketTimeout((int) unit.toMillis(time));
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl writeTimeout(long time, TimeUnit unit) {
        if(time > 0) {
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
        if(max <= 0) {
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
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl sslSocketFactory(final SSLSocketFactoryBuilder builder) {
        if(builder != null) {

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
        SocketAddress address = proxy.address();
        if (address instanceof HttpInetSocketAddress) {
            throw new HttpBladeException("the [HttpInetSocketAddress] is deprecated.");
        } else if (address instanceof InetSocketAddress) {
            InetSocketAddress isAddr = (InetSocketAddress) address;
            clientBuilder.setProxy(new HttpHost(isAddr.getAddress(), isAddr.getPort()));
        } else {
            throw new HttpBladeException("must use [InetSocketAddress]");
        }
        return this;
    }

    @Override
    public HttpClient build() {
        return new ApacheHttpClientImpl(this);
    }
}
