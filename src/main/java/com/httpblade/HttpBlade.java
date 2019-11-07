package com.httpblade;

import com.httpblade.base.HttpClient;
import com.httpblade.base.HttpClientBuilder;
import com.httpblade.base.Request;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.api.ApiMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpBlade {

    private static JsonParserFactory jsonParserFactory;
    private static XmlParserFactory xmlParserFactory;

    private static final Map<Method, ApiMethod> apiMethods = new ConcurrentHashMap<>();

    /**
     * JDK 自带客户端类型
     */
    public static final int CLIENT_TYPE_JDK = Environment.CLIENT_TYPE_JDK;

    /**
     * OKHttp 客户端类型
     */
    public static final int CLIENT_TYPE_OKHTTP = Environment.CLIENT_TYPE_OKHTTP;

    /**
     * Apache http components 客户端类型
     */
    public static final int CLIENT_TYPE_APACHE_HTTP = Environment.CLIENT_TYPE_APACHE_HTTP;

    public static void use(int clientType) {
        Environment.use(clientType);
    }

    public static Request createRequest() {
        return Environment.newRequest();
    }

    public static HttpClientBuilder createClientBuilder() {
        return Environment.newClientBuilder();
    }

    public static int nowClientType() {
        return Environment.nowUseClientType;
    }

    /**
     * 设置默认的全局 Http 客户端
     *
     * @param httpClient 自定义的全局 Http 客户端
     */
    public static void setDefaultClient(HttpClient httpClient) {
        if (httpClient == null) {
            throw new NullPointerException("the parameter is null");
        }
        Environment.defaultClient = httpClient;
    }

    /**
     * 获取当前默认的全局 Http 客户端
     *
     * @return Http 客户端
     */
    public static HttpClient defaultClient() {
        return Environment.defaultClient;
    }

    public static void setJsonParserFactory(JsonParserFactory parserFactory) {
        HttpBlade.jsonParserFactory = parserFactory;
    }

    public static JsonParserFactory getJsonParserFactory() {
        return jsonParserFactory;
    }

    public static XmlParserFactory getXmlParserFactory() {
        return xmlParserFactory;
    }

    public static void setXmlParserFactory(XmlParserFactory xmlParserFactory) {
        HttpBlade.xmlParserFactory = xmlParserFactory;
    }

    public static RequestWrapper get(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.GET);
    }

    public static RequestWrapper post(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.POST);
    }

    public static RequestWrapper put(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.PUT);
    }

    public static RequestWrapper delete(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.DELETE);
    }

    public static RequestWrapper head(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.HEAD);
    }

    public static RequestWrapper options(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.OPTIONS);
    }

    public static RequestWrapper trace(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.TRACE);
    }

    public static RequestWrapper connect(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.CONNECT);
    }

    public static RequestWrapper patch(String url) {
        return new RequestWrapper(defaultClient(), createRequest(), url, HttpMethod.PATCH);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(this, args);
                    }
                    if (method.isDefault()) {
                        method.invoke(proxy, args);
                    }
                    ApiMethod apiMethod = apiMethods.get(method);
                    if (apiMethod != null) {
                        return apiMethod.execute(args);
                    }
                    synchronized (apiMethods) {
                        apiMethod = apiMethods.get(method);
                        if (apiMethod == null) {
                            apiMethod = new ApiMethod(method);
                            apiMethods.put(method, apiMethod);
                        }
                    }
                    return apiMethod.execute(args);
                }
            });
    }

}
