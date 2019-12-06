package com.httpblade.okhttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpClient;
import com.httpblade.Request;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Proxy;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientImpl extends HttpClient {

    private OkHttpClient client;

    OkHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout,
                            int maxRedirectCount, CookieHome cookieHome, HostnameVerifier hostnameVerifier,
                            Proxy proxy, SocketFactory socketFactory, Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier,
            proxy, socketFactory, globalHeaders);
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
        if (socketFactory != null) {
            builder.socketFactory(socketFactory);
        }
        this.client = builder.build();
    }

    @Override
    public com.httpblade.Response request(Request request) {
        OkHttpRequestImpl requestImpl = (OkHttpRequestImpl) request;
//        try {
//            Response response = client.newCall(requestImpl.build(this.globalHeaders)).execute();
//            return new OkHttpResponseImpl(response);
//        } catch (IOException e) {
//            throw new HttpBladeException(e);
//        }
        return null;
    }

    @Override
    public void requestAsync(Request request, com.httpblade.Callback callback) {
//        OkHttpRequestImpl requestImpl = (OkHttpRequestImpl) request;
//        client.newCall(requestImpl.build(this.globalHeaders)).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (callback != null) {
//                    callback.error(e);
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (callback != null) {
//                    callback.success(new OkHttpResponseImpl(response));
//                }
//            }
//        });
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
