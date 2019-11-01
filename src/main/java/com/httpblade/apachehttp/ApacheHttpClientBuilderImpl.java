package com.httpblade.apachehttp;

import com.httpblade.base.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.HostnameVerifier;
import java.util.concurrent.TimeUnit;

public class ApacheHttpClientBuilderImpl implements com.httpblade.base.HttpClientBuilder<ApacheHttpClientBuilderImpl> {

    HttpClientBuilder clientBuilder;
    RequestConfig.Builder requestConfigBuilder;
    long writeTimeout = Defaults.WRITE_TIMEOUT;
    Headers globalHeaders = new Headers();
    CookieHome cookieHome;

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
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl sslSocketFactory(final SSLSocketFactoryBuilder builder) {
        if (builder != null) {
            clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(builder.buildContext()));
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
        if (proxy.hasAuth()) {
            proxy(proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword());
        } else {
            proxy(proxy.getHost(), proxy.getPort());
        }
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl proxy(String host, int port) {
        clientBuilder.setProxy(new HttpHost(host, port));
        return this;
    }

    @Override
    public ApacheHttpClientBuilderImpl proxy(String host, int port, String username, String password) {
        clientBuilder.setProxy(null);
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(username, password));
        clientBuilder.setDefaultCredentialsProvider(cp);
        return this;
    }

    @Override
    public HttpClient build() {
        return new ApacheHttpClientImpl(this);
    }

    private static CredentialsProvider createCredentialsProvider(Proxy proxy) {
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
            new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
        return cp;
    }

}
