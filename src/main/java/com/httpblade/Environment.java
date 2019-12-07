package com.httpblade;

import com.httpblade.apachehttp.ApacheHttpClientImpl;
import com.httpblade.apachehttp.ApacheHttpRequestImpl;
import com.httpblade.basehttp.BaseHttpClientImpl;
import com.httpblade.basehttp.BaseHttpRequestImpl;
import com.httpblade.common.Headers;
import com.httpblade.common.Proxy;
import com.httpblade.okhttp.OkHttpClientImpl;
import com.httpblade.okhttp.OkHttpRequestImpl;

import javax.net.SocketFactory;
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

    static HttpClient defaultClient;

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
        newDefaultClient();
    }

    static Class<? extends Request> getRequestClass() {
        return requestClass;
    }

    static Class<? extends HttpClient> getClientClass() {
        return clientClass;
    }

    static Class<? extends HttpClientBuilder> getClientBuilderClass() {
        return clientBuilderClass;
    }

    static Request newRequest() {
        try {
            return requestClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HttpBladeException(e);
        }
    }

    static HttpClient createDefaultClient() {

        return null;
    }

    static HttpClient createClient(int clientType, String baseUrl, long connectTimeout, long readTimeout, long writeTimeout,
                                   int maxRedirectCount, CookieHome cookieHome, HostnameVerifier hostnameVerifier,
                                   Proxy proxy, SocketFactory socketFactory, Map<String, Headers> globalHeaders) {
        if(clientType == CLIENT_TYPE_DEFAULT) {
            return createDefaultClient();
        } else if(clientType == CLIENT_TYPE_JDK) {
            return null;
        } else if(clientType == CLIENT_TYPE_OKHTTP) {
            return new OkHttpClientImpl(baseUrl, connectTimeout, readTimeout, writeTimeout, maxRedirectCount, cookieHome, hostnameVerifier, proxy, socketFactory, globalHeaders);
        } else if(clientType == CLIENT_TYPE_APACHE_HTTP) {
            return null;
        } else {
            throw new HttpBladeException("");
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

    private static void newDefaultClient() {
        try {
            Constructor<? extends HttpClient> constructor = clientClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            defaultClient = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new HttpBladeException(e);
        }
    }

}
