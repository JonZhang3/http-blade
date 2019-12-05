package com.httpblade;

import com.httpblade.apachehttp.ApacheHttpClientBuilderImpl;
import com.httpblade.apachehttp.ApacheHttpClientImpl;
import com.httpblade.apachehttp.ApacheHttpRequestImpl;
import com.httpblade.basehttp.BaseHttpClientBuilderImpl;
import com.httpblade.basehttp.BaseHttpClientImpl;
import com.httpblade.basehttp.BaseHttpRequestImpl;
import com.httpblade.okhttp.OkHttpClientBuilderImpl;
import com.httpblade.okhttp.OkHttpClientImpl;
import com.httpblade.okhttp.OkHttpRequestImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class Environment {

    private Environment() {
    }

    static final int CLIENT_TYPE_JDK = 0;
    static final int CLIENT_TYPE_OKHTTP = 1;
    static final int CLIENT_TYPE_APACHE_HTTP = 2;

    static HttpClient defaultClient;

    static int nowUseClientType = CLIENT_TYPE_JDK;
    private static Class<? extends Request> requestClass;
    private static Class<? extends HttpClient> clientClass;
    private static Class<? extends HttpClientBuilder> clientBuilderClass;

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

    static void use(int type) {
        if (type == HttpBlade.CLIENT_TYPE_JDK) {
            useJdkHttp();
        } else if (type == HttpBlade.CLIENT_TYPE_OKHTTP) {
            useOkhttp();
        } else if (type == HttpBlade.CLIENT_TYPE_APACHE_HTTP) {
            useApacheHttp();
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

    static HttpClientBuilder newClientBuilder() {
        try {
            return clientBuilderClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HttpBladeException(e);
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
        clientBuilderClass = OkHttpClientBuilderImpl.class;
        nowUseClientType = HttpBlade.CLIENT_TYPE_OKHTTP;
    }

    private static void useApacheHttp() {
        requestClass = ApacheHttpRequestImpl.class;
        clientClass = ApacheHttpClientImpl.class;
        clientBuilderClass = ApacheHttpClientBuilderImpl.class;
        nowUseClientType = HttpBlade.CLIENT_TYPE_APACHE_HTTP;
    }

    private static void useJdkHttp() {
        requestClass = BaseHttpRequestImpl.class;
        clientClass = BaseHttpClientImpl.class;
        clientBuilderClass = BaseHttpClientBuilderImpl.class;
        nowUseClientType = HttpBlade.CLIENT_TYPE_JDK;
    }

    private static void newDefaultClient() {
        try {
            Constructor<? extends HttpClient> constructor =
                clientClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            defaultClient = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException
            | NoSuchMethodException | InvocationTargetException e) {
            throw new HttpBladeException(e);
        }
    }

}
