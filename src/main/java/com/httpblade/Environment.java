package com.httpblade;

import com.httpblade.apachehttp.ApacheHttpClientBuilderImpl;
import com.httpblade.apachehttp.ApacheHttpClientImpl;
import com.httpblade.apachehttp.ApacheHttpRequestImpl;
import com.httpblade.base.HttpClient;
import com.httpblade.base.HttpClientBuilder;
import com.httpblade.base.Request;
import com.httpblade.basehttp.BaseHttpClientBuilderImpl;
import com.httpblade.basehttp.BaseHttpClientImpl;
import com.httpblade.basehttp.BaseHttpRequestImpl;
import com.httpblade.okhttp.OkHttpClientBuilderImpl;
import com.httpblade.okhttp.OkHttpClientImpl;
import com.httpblade.okhttp.OkHttpRequestImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class Environment {

    static final int CLIENT_TYPE_JDK = 0;
    static final int CLIENT_TYPE_OKHTTP = 1;
    static final int CLIENT_TYPE_APACHE_HTTP = 2;

    static HttpClient defaultClient;

    static int nowUseClientType = CLIENT_TYPE_JDK;
    private static Class<? extends Request> REQUEST_CLASS;
    private static Class<? extends HttpClient> CLIENT_CLASS;
    private static Class<? extends HttpClientBuilder> CLIENT_BUILDER_CLASS;

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
        if(type == HttpBlade.CLIENT_TYPE_JDK) {
            useJdkHttp();
        } else if(type == HttpBlade.CLIENT_TYPE_OKHTTP) {
            useOkhttp();
        } else if(type == HttpBlade.CLIENT_TYPE_APACHE_HTTP) {
            useApacheHttp();
        }
        newDefaultClient();
    }

    static Class<? extends Request> getRequestClass() {
        return REQUEST_CLASS;
    }

    static Class<? extends HttpClient> getClientClass() {
        return CLIENT_CLASS;
    }

    static Class<? extends HttpClientBuilder> getClientBuilderClass() {
        return CLIENT_BUILDER_CLASS;
    }

    static Request newRequest() {
        try {
            return REQUEST_CLASS.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new HttpBladeException(e);
        }
    }

    static HttpClientBuilder newClientBuilder() {
        try {
            return CLIENT_BUILDER_CLASS.newInstance();
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
        REQUEST_CLASS = OkHttpRequestImpl.class;
        CLIENT_CLASS = OkHttpClientImpl.class;
        CLIENT_BUILDER_CLASS = OkHttpClientBuilderImpl.class;
        nowUseClientType = HttpBlade.CLIENT_TYPE_OKHTTP;
    }

    private static void useApacheHttp() {
        REQUEST_CLASS = ApacheHttpRequestImpl.class;
        CLIENT_CLASS = ApacheHttpClientImpl.class;
        CLIENT_BUILDER_CLASS = ApacheHttpClientBuilderImpl.class;
        nowUseClientType = HttpBlade.CLIENT_TYPE_APACHE_HTTP;
    }

    private static void useJdkHttp() {
        REQUEST_CLASS = BaseHttpRequestImpl.class;
        CLIENT_CLASS = BaseHttpClientImpl.class;
        CLIENT_BUILDER_CLASS = BaseHttpClientBuilderImpl.class;
        nowUseClientType = HttpBlade.CLIENT_TYPE_JDK;
    }

    private static void newDefaultClient() {
        try {
            Constructor<? extends HttpClient> constructor =
                CLIENT_CLASS.getDeclaredConstructor();
            constructor.setAccessible(true);
            defaultClient = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException
            | NoSuchMethodException | InvocationTargetException e) {
            throw new HttpBladeException(e);
        }
    }

}
