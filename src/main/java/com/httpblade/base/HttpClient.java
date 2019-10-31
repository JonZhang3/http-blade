package com.httpblade.base;

/**
 * Http 客户端。首先构建 {@code Request}，使用
 * {@code HttpClient} 去调用，同步方式或异步方式
 *
 * @author Jon
 * @since 1.0.0
 */
public interface HttpClient {

    /**
     * 同步发起请求
     *
     * @param request Http 请求
     * @return 响应内容
     */
    Response request(Request request);

    /**
     * 异步发起请求
     *
     * @param request Http 请求
     */
    void requestAsync(Request request, Callback callback);

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间，单位：毫秒
     */
    long connectTimeout();

    long readTimeout();

    long writeTimeout();

    int maxRedirectCount();

    CookieHome cookieHome();

}
