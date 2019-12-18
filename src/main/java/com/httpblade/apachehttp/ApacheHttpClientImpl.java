package com.httpblade.apachehttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpBlade;
import com.httpblade.HttpClient;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.HostnameVerifier;
import java.util.HashMap;
import java.util.Map;

public class ApacheHttpClientImpl extends HttpClient {

    private final CloseableHttpClient client;
    private final CredentialsProvider cp = new BasicCredentialsProvider();

    ApacheHttpClientImpl() {
        this("", Defaults.CONNECT_TIMEOUT, Defaults.READ_TIMEOUT, Defaults.WRITE_TIMEOUT,
            Defaults.MAX_REDIRECT_COUNT, null, null, null, null, new HashMap<>());
    }

    public ApacheHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout,
                                int maxRedirectCount, CookieHome cookieHome, HostnameVerifier hostnameVerifier,
                                Proxy proxy, SSLBuilder sslBuilder, Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier,
            proxy, sslBuilder, globalHeaders);
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.disableCookieManagement();
        clientBuilder.setSSLHostnameVerifier(hostnameVerifier);
        clientBuilder.setDefaultCredentialsProvider(cp);
        if(sslBuilder != null) {
            clientBuilder.setSSLContext(sslBuilder.build());
        }
        if (proxy != null) {
            clientBuilder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort()));
            if (proxy.hasAuth()) {
                cp.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                    new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
            }
//            else if (proxy.getType() == java.net.Proxy.Type.SOCKS) {
//                SSLContext sslContext = null;
//                if(sslBuilder != null) {
//                    sslContext = sslBuilder.build();
//                }
//                Registry<ConnectionSocketFactory> registry =
//                    RegistryBuilder.<ConnectionSocketFactory>create()
//                        .register("http", new SocketProxyConnectionFactory(proxy.getHost(), proxy.getPort()))
//                        .register("https", new SocketProxySSLConnectionFactory(sslContext, hostnameVerifier, proxy.getHost(), proxy.getPort()))
//                        .build();
//                PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
//                clientBuilder.setConnectionManager(connManager);
//            }
        }
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder
            .setConnectTimeout((int) connectTimeout)
            .setSocketTimeout((int) readTimeout)
            .setCircularRedirectsAllowed(false)
            .setRelativeRedirectsAllowed(false);
        if (maxRedirectCount > 0) {
            configBuilder.setRedirectsEnabled(true).setMaxRedirects(maxRedirectCount);
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

    @Override
    public int clientType() {
        return HttpBlade.CLIENT_TYPE_APACHE_HTTP;
    }

    void setCredentials(String host, int port, String username, String password) {
        cp.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(username, password));
    }

}
