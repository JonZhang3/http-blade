package com.httpblade.base;

import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Http 客户端构建者
 *
 * @param <T> 方便链式调用
 * @author Jon
 * @since 1.0.0
 */
public interface HttpClientBuilder<T extends HttpClientBuilder> {

    /**
     * 为新连接设置默认的连接超时时间。设置为 0 表示没有限制。
     * 内部实际的时间单位为毫秒，所以该值在转换为毫秒后的取值范围为 1 到 {@link Integer#MAX_VALUE} 之间。
     * 该超时时间将会运用到 TCP 连接时。
     * 默认为 10 秒。
     *
     * @param time 超时时间
     * @param unit 时间单位
     * @return {@code this}
     */
    T connectTimeout(long time, TimeUnit unit);

    /**
     * 为新连接设置默认的读取超时时间。设置为 0 表示没有限制。
     * 内部实际的时间单位为毫秒，所以该值在转换为毫秒后的取值范围为 1 到 {@link Integer#MAX_VALUE} 之间。
     * 该超时时间将会运用到读取 IO 操作上。
     * 默认为 10 秒。
     *
     * @param time 超时时间
     * @param unit 时间单位
     * @return {@code this}
     */
    T readTimeout(long time, TimeUnit unit);

    /**
     * 为新连接设置默认的写超时时间。设置为 0 表示没有限制。
     * 内部实际的时间单位为毫秒，所以该值在转换为毫秒后的取值范围为 1 到 {@link Integer#MAX_VALUE} 之间。
     * 该超时时间将会运用到写 IO 操作上。
     * 默认为 10 秒。
     *
     * @param time 超时时间
     * @param unit 时间单位
     * @return {@code this}
     */
    T writeTimeout(long time, TimeUnit unit);

    /**
     * 为 HTTP Cookie 提供存储策略
     *
     * @param cookieHome Cookie 存储策略
     * @return {@code this}
     */
    T cookieHome(CookieHome cookieHome);

    /**
     * 设置允许的最大重定向次数，如果给定的值小于或等于 0，表示不允许重定向，直接返回响应结果
     *
     * @param max 最大重定向次数
     * @return {@code this}
     */
    T maxRedirectCount(int max);

    /**
     * 设置域名验证器，只针对 HTTPS 请求
     *
     * @param hostnameVerifier 域名验证器
     * @return {@code this}
     */
    T hostnameVerifier(HostnameVerifier hostnameVerifier);

    /**
     * 设置 {@code SSLSocketFactory}，只针对 HTTPS 请求
     *
     * @return {@code this}
     */
    T sslSocketFactory(SSLSocketFactoryBuilder builder);

    /**
     * 设置默认的请求头，这将会添加在每次请求中
     *
     * @param name 请求头名称
     * @param value 请求头的值
     * @return {@code this}
     */
    T setDefaultHeader(String name, String value);

    /**
     * 添加默认的请求头，这将会添加在每次请求中。如果已经存在同名的请求头，则会将值添加在后面
     *
     * @param name 请求头名称
     * @param value 请求头的值
     * @return {@code this}
     */
    T addDefaultHeader(String name, String value);

    T proxy(Proxy proxy);

    HttpClient build();

}
