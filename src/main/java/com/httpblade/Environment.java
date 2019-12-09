package com.httpblade;

import com.httpblade.apachehttp.ApacheHttpClientImpl;
import com.httpblade.apachehttp.ApacheHttpRequestImpl;
import com.httpblade.basehttp.BaseHttpClientImpl;
import com.httpblade.basehttp.BaseHttpRequestImpl;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import com.httpblade.okhttp.OkHttpClientImpl;
import com.httpblade.okhttp.OkHttpRequestImpl;

import javax.net.ssl.HostnameVerifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

final class Environment {

    private Environment() {
    }

    static final int CLIENT_TYPE_DEFAULT = -1;
    static final int CLIENT_TYPE_JDK = 0;
    static final int CLIENT_TYPE_OKHTTP = 1;
    static final int CLIENT_TYPE_APACHE_HTTP = 2;

    static int defaultClientType = CLIENT_TYPE_JDK;
    private static Class<? extends Request> requestClass;
    private static Class<? extends HttpClient> clientClass;

    static {
        if (hasOkhttp()) {
            useOkhttp();
        } else if (hasApacheHttp()) {
            useApacheHttp();
        } else {
            useJdkHttp();
        }
    }

    static Request newRequest(HttpClient client) {
        try {
            Constructor<? extends Request> constructor = requestClass.getDeclaredConstructor(HttpClient.class);
            constructor.setAccessible(true);
            return constructor.newInstance(client);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new HttpBladeException(e);
        }
    }

    static HttpClient createDefaultClient() {
        try {
            Constructor<? extends HttpClient> constructor = clientClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new HttpBladeException(e);
        }
    }

    static HttpClient createClient(int clientType, String baseUrl, long connectTimeout, long readTimeout,
                                   long writeTimeout, int maxRedirectCount, CookieHome cookieHome,
                                   HostnameVerifier hostnameVerifier, Proxy proxy, Map<String, Headers> globalHeaders) {
        if (clientType == CLIENT_TYPE_DEFAULT) {
            return createDefaultClient();
        } else if (clientType == CLIENT_TYPE_JDK) {
            return new BaseHttpClientImpl(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount,
                cookieHome, hostnameVerifier, proxy, globalHeaders);
        } else if (clientType == CLIENT_TYPE_OKHTTP) {
            return new OkHttpClientImpl(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount,
                cookieHome, hostnameVerifier, proxy, globalHeaders);
        } else if (clientType == CLIENT_TYPE_APACHE_HTTP) {
            return new ApacheHttpClientImpl(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount,
                cookieHome, hostnameVerifier, proxy, globalHeaders);
        } else {
            throw new HttpBladeException("the client type not supported.");
        }
    }

    private static boolean hasOkhttp() {
        try {
            Class.forName("okhttp3.OkHttpClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean hasApacheHttp() {
        try {
            Class.forName("org.apache.http.client.HttpClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static void useOkhttp() {
        requestClass = OkHttpRequestImpl.class;
        clientClass = OkHttpClientImpl.class;
        defaultClientType = HttpBlade.CLIENT_TYPE_OKHTTP;
    }

    private static void useApacheHttp() {
        requestClass = ApacheHttpRequestImpl.class;
        clientClass = ApacheHttpClientImpl.class;
        defaultClientType = HttpBlade.CLIENT_TYPE_APACHE_HTTP;
    }

    private static void useJdkHttp() {
        requestClass = BaseHttpRequestImpl.class;
        clientClass = BaseHttpClientImpl.class;
        defaultClientType = HttpBlade.CLIENT_TYPE_JDK;
    }

}
