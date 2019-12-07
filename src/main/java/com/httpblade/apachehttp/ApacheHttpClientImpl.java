package com.httpblade.apachehttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpClient;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import com.httpblade.common.task.AsyncTaskExecutor;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApacheHttpClientImpl extends HttpClient {

    private final CloseableHttpClient client;
    private final RequestConfig requestConfig;
    private final AsyncTaskExecutor asyncExecutor = new AsyncTaskExecutor();

    public ApacheHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout, int maxRedirectCount,
                                CookieHome cookieHome, HostnameVerifier hostnameVerifier, Proxy proxy,
                                SocketFactory socketFactory, Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier, proxy, socketFactory, globalHeaders);
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

        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.disableCookieManagement();
        clientBuilder.setHostnameVerifier();
        clientBuilder.setConnectionManager(new HttpClientConnectionManager() {
            @Override
            public ConnectionRequest requestConnection(HttpRoute route, Object state) {
                return null;
            }

            @Override
            public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration, TimeUnit timeUnit) {

            }

            @Override
            public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {

            }

            @Override
            public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {

            }

            @Override
            public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {

            }

            @Override
            public void closeIdleConnections(long idletime, TimeUnit tunit) {

            }

            @Override
            public void closeExpiredConnections() {

            }

            @Override
            public void shutdown() {

            }
        });
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout((int) connectTimeout)
            .setSocketTimeout((int) readTimeout)
            .setCircularRedirectsAllowed(false)
            .setRelativeRedirectsAllowed(false);
        if(maxRedirectCount > 0) {
            configBuilder.setRedirectsEnabled(true)
                .setMaxRedirects(maxRedirectCount);
        } else {
            configBuilder.setRedirectsEnabled(false);
        }

    }

    @Override
    public Object raw() {
        return client;
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



//    @Override
//    public void requestAsync(Request request, Callback callback) {
//        ApacheHttpRequestImpl requestImpl = (ApacheHttpRequestImpl) request;
//        HttpUriRequest httpUriRequest = requestImpl.build(this.globalHeaders, this.cookieHome);
//        asyncExecutor.enqueue(new Task(callback) {
//            @Override
//            public void execute() {
//                try {
//                    CloseableHttpResponse response = client.execute(httpUriRequest);
//                    callback.success(new ApacheHttpResponseImpl(response, request.getUrl(), cookieHome));
//                } catch (IOException e) {
//                    if (callback != null) {
//                        callback.error(e);
//                    }
//                }
//            }
//        });
//    }

}
