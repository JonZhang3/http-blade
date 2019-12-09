package com.httpblade.apachehttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpClient;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.HostnameVerifier;
import java.util.Map;

public class ApacheHttpClientImpl extends HttpClient {

    private final CloseableHttpClient client;

    public ApacheHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout, int maxRedirectCount,
                                CookieHome cookieHome, HostnameVerifier hostnameVerifier, Proxy proxy,
                                Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier, proxy, globalHeaders);
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.disableCookieManagement();
        clientBuilder.setSSLHostnameVerifier(hostnameVerifier);
        if(proxy != null) {
            if(proxy.getType() == java.net.Proxy.Type.HTTP) {
                clientBuilder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort()));
                if(proxy.hasAuth()) {
                    CredentialsProvider cp = new BasicCredentialsProvider();
                    cp.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
                    clientBuilder.setDefaultCredentialsProvider(cp);
                }
            } else if(proxy.getType() == java.net.Proxy.Type.SOCKS) {
                Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new SocketProxyConnectionFactory(proxy.getHost(), proxy.getPort()))
                    .register("https", new SocketProxySSLConnectionFactory(null, null,
                        proxy.getHost(), proxy.getPort()))
                    .build();
                PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
                clientBuilder.setConnectionManager(connManager);
            }
        }
        // TODO SSLSocketFactory
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
        clientBuilder.setDefaultRequestConfig(configBuilder.build());
        this.client = clientBuilder.build();
    }

    @Override
    public Object raw() {
        return client;
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
