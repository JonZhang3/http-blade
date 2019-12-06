package com.httpblade;

import com.httpblade.common.Proxy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Http 客户端。首先构建 {@code Request}，使用
 * {@code HttpClient} 去调用，同步方式或异步方式
 *
 * @author Jon
 * @since 1.0.0
 */
public abstract class HttpClient {

    /**
     * 同步发起请求
     *
     * @param request Http 请求
     * @return 响应内容
     */
    protected abstract Response request(Request request);

    /**
     * 异步发起请求
     *
     * @param request Http 请求
     */
    abstract void requestAsync(Request request, Callback callback);

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间，单位：毫秒
     */
    public abstract long connectTimeout();

    public abstract long readTimeout();

    public abstract long writeTimeout();

    public abstract int maxRedirectCount();

    public abstract CookieHome cookieHome();

    public abstract HostnameVerifier hostnameVerifier();

    public abstract SSLSocketFactory sslSocketFactory();

    public abstract Proxy proxy();

}
