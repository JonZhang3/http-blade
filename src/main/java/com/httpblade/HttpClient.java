package com.httpblade;

import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.util.Map;

/**
 * Http 客户端。首先构建 {@code Request}，使用
 * {@code HttpClient} 去调用，同步方式或异步方式
 *
 * @author Jon
 * @since 1.0.0
 */
public abstract class HttpClient {

    protected String baseUrl;
    protected long connectTimeout;
    protected long readTimeout;
    protected long writeTimeout;
    protected int maxRedirectCount;
    protected CookieHome cookieHome;
    protected HostnameVerifier hostnameVerifier;
    protected Proxy proxy;
    protected SocketFactory socketFactory;
    protected SSLSocketFactoryBuilder sslSocketFactoryBuilder;
    protected Map<String, Headers> globalHeaders;

    public HttpClient(String baseUrl, long connectTimeout, long readTimeout, long writeTimeout, int maxRedirectCount,
                      CookieHome cookieHome, HostnameVerifier hostnameVerifier, Proxy proxy,
                      SocketFactory socketFactory, Map<String, Headers> globalHeaders) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.maxRedirectCount = maxRedirectCount;
        this.cookieHome = cookieHome;
        this.hostnameVerifier = hostnameVerifier;
        this.proxy = proxy;
        this.socketFactory = socketFactory;
        this.globalHeaders = globalHeaders;
    }

    /**
     * 同步发起请求
     *
     * @param request Http 请求
     * @return 响应内容
     */
    public abstract Response request(Request request);

    /**
     * 异步发起请求
     *
     * @param request Http 请求
     */
    public abstract void requestAsync(Request request, Callback callback);

    public abstract Object raw();

    public String baseUrl() {
        return baseUrl;
    }

    /**
     * 获取连接超时时间
     *
     * @return 连接超时时间，单位：毫秒
     */
    public long connectTimeout() {
        return this.connectTimeout;
    }

    public long readTimeout() {
        return readTimeout;
    }

    public long writeTimeout() {
        return writeTimeout;
    }

    public int maxRedirectCount() {
        return maxRedirectCount;
    }

    public CookieHome cookieHome() {
        return cookieHome;
    }

    public HostnameVerifier hostnameVerifier() {
        return hostnameVerifier;
    }

    public SocketFactory socketFactory() {
        return socketFactory;
    }

    public SSLSocketFactoryBuilder sslSocketFactoryBuilder() {
        return sslSocketFactoryBuilder;
    }

    public Proxy proxy() {
        return proxy;
    }

    public Headers headers() {
        return globalHeaders.get(Defaults.KEY_COMMON);
    }

    public Headers headers(String key) {
        return globalHeaders.get(key);
    }

    public Headers getHeaders() {
        return globalHeaders.get(HttpMethod.GET.value());
    }

    public Headers postHeaders() {
        return globalHeaders.get(HttpMethod.POST.value());
    }

    public Headers putHeaders() {
        return globalHeaders.get(HttpMethod.PUT.value());
    }

    public Headers deleteHeaders() {
        return globalHeaders.get(HttpMethod.DELETE.value());
    }

    public Headers headHeaders() {
        return globalHeaders.get(HttpMethod.HEAD.value());
    }

    public Headers optionsHeaders() {
        return globalHeaders.get(HttpMethod.OPTIONS.value());
    }

    public Headers traceHeaders() {
        return globalHeaders.get(HttpMethod.TRACE.value());
    }

    public Headers connectHeaders() {
        return globalHeaders.get(HttpMethod.CONNECT.value());
    }

    public Headers patchHeaders() {
        return globalHeaders.get(HttpMethod.PATCH.value());
    }

}
