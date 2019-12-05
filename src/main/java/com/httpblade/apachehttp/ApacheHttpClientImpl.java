package com.httpblade.apachehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.Callback;
import com.httpblade.CookieHome;
import com.httpblade.base.HttpClient;
import com.httpblade.Request;
import com.httpblade.Response;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import com.httpblade.common.task.AsyncTaskExecutor;
import com.httpblade.common.task.Task;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

public class ApacheHttpClientImpl implements HttpClient {

    private final CloseableHttpClient client;
    private final RequestConfig requestConfig;
    private long writeTimeout = Defaults.WRITE_TIMEOUT;
    private Headers globalHeaders;
    private CookieHome cookieHome;
    private final HostnameVerifier hostnameVerifier;
    private final SSLSocketFactory sslSocketFactory;
    private final Proxy proxy;
    private final AsyncTaskExecutor asyncExecutor = new AsyncTaskExecutor();

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
        hostnameVerifier = null;
        sslSocketFactory = null;
        proxy = null;
    }

    ApacheHttpClientImpl(ApacheHttpClientBuilderImpl clientBuilder) {
        this.requestConfig = clientBuilder.requestConfigBuilder.build();
        clientBuilder.clientBuilder.setDefaultRequestConfig(this.requestConfig);
        this.proxy = clientBuilder.proxy;
        if (proxy != null) {
            if (proxy.getType() == java.net.Proxy.Type.HTTP) {
                clientBuilder.clientBuilder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort()));
                if (proxy.hasAuth()) {
                    CredentialsProvider cp = new BasicCredentialsProvider();
                    cp.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
                    clientBuilder.clientBuilder.setDefaultCredentialsProvider(cp);
                }
            } else if (proxy.getType() == java.net.Proxy.Type.SOCKS) {
                Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new SocketProxyConnectionFactory(proxy.getHost(), proxy.getPort()))
                    .register("https", new SocketProxySSLConnectionFactory(null, null,
                        proxy.getHost(), proxy.getPort()))
                    .build();
                PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
                clientBuilder.clientBuilder.setConnectionManager(connManager);
            }
        }
        this.client = clientBuilder.clientBuilder.build();
        this.hostnameVerifier = clientBuilder.hostnameVerifier;
        this.sslSocketFactory = clientBuilder.sslSocketFactory;
        this.writeTimeout = clientBuilder.writeTimeout;
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

    @Override
    public HostnameVerifier hostnameVerifier() {
        return hostnameVerifier;
    }

    @Override
    public SSLSocketFactory sslSocketFactory() {
        return sslSocketFactory;
    }

    @Override
    public Proxy proxy() {
        return proxy;
    }

}
