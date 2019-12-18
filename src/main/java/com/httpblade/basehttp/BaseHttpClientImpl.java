package com.httpblade.basehttp;

import com.httpblade.CookieHome;
import com.httpblade.HttpBlade;
import com.httpblade.HttpClient;
import com.httpblade.common.Headers;
import com.httpblade.common.SSLBuilder;

import javax.net.ssl.HostnameVerifier;
import java.util.Map;

public class BaseHttpClientImpl extends HttpClient {

    BaseHttpClientImpl() {
        super();
    }

    public BaseHttpClientImpl(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout,
                              int maxRedirectCount, CookieHome cookieHome, HostnameVerifier hostnameVerifier,
                              com.httpblade.common.Proxy proxy, SSLBuilder sslBuilder,
                              Map<String, Headers> globalHeaders) {
        super(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier,
            proxy, sslBuilder, globalHeaders);
    }

    @Override
    public Object raw() {
        return null;
    }

    @Override
    public int clientType() {
        return HttpBlade.CLIENT_TYPE_JDK;
    }

}
