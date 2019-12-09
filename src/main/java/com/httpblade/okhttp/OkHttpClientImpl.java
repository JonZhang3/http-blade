package com.httpblade.okhttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpClient;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Proxy;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientImpl extends HttpClient {

    private OkHttpClient client;

    public OkHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout,
                            int maxRedirectCount, CookieHome cookieHome, HostnameVerifier hostnameVerifier,
                            Proxy proxy, Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier,
            proxy, globalHeaders);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(connectTimeout,
            TimeUnit.MILLISECONDS).readTimeout(readTimeout, TimeUnit.MILLISECONDS).writeTimeout(writeTimeout,
            TimeUnit.MILLISECONDS);
        builder.followRedirects(false);
        builder.followSslRedirects(false);
        if (maxRedirectCount > 0) {
            List<Interceptor> interceptors = builder.interceptors();
            interceptors.removeIf(interceptor -> interceptor.getClass().equals(RedirectInterceptor.class));
            builder.addInterceptor(new RedirectInterceptor(maxRedirectCount));
        }
        if (cookieHome != null) {
            builder.cookieJar(new DefaultCookieJar(cookieHome));
        }
        if (hostnameVerifier != null) {
            builder.hostnameVerifier(hostnameVerifier);
        }
        if (proxy != null) {
            builder.proxy(Proxy.toJavaProxy(proxy));
            if (proxy.hasAuth()) {
                builder.proxyAuthenticator(createAuthenticator(proxy.getUsername(), proxy.getPassword()));
            }
        }
        this.client = builder.build();
    }

    @Override
    public Object raw() {
        return client;
    }

    @Override
    public CookieHome cookieHome() {
        DefaultCookieJar cookieJar = (DefaultCookieJar) client.cookieJar();
        CookieHome cookieHome = null;
        if (cookieJar != null) {
            cookieHome = cookieJar.getCookieHome();
        }
        return cookieHome;
    }

    static Authenticator createAuthenticator(String username, String password) {
        return (route, response) -> {
            String credential = Credentials.basic(username, password, StandardCharsets.UTF_8);
            return response.request().newBuilder().header(HttpHeader.PROXY_AUTHORIZATION, credential).build();
        };
    }

}
