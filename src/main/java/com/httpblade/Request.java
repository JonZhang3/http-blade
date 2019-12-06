package com.httpblade;

import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.SSLSocketFactoryBuilder;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 请求
 *
 * @param <T> 方便链式调用
 * @author Jon
 * @since 1.0.0
 */
public interface Request<T extends Request> {

    /**
     * 指定 Http Url
     * @param url Http Url
     * @return {@code this}
     */
    T url(String url);

    URL getUrl();

    /**
     * 指定
     * @param method
     * @return {@code this}
     */
    T method(HttpMethod method);

    T get(String url);

    T post(String url);

    T put(String url);

    T delete(String url);

    T head(String url);

    T options(String url);

    T trace(String url);

    T connect(String url);

    T patch(String url);

    HttpMethod getMethod();

    T charset(Charset charset);

    T contentType(String contentType);

    T contentLength(long length);

    T setHeader(String name, String value);

    T setDateHeader(String name, Date date);

    T addHeader(String name, String value);

    T addDateHeader(String name, Date date);

    T removeHeader(String name);

    boolean containsHeader(String name);

    String header(String name);

    List<String> headers(String name);

    Map<String, List<String>> allHeaders();

    T pathVariable(String name, String value);

    T queryString(String name, String value);

    T queryString(String name, String value, boolean encoded);

    T form(Map<String, String> values);

    T form(String name, String value);

    T formEncoded(String name, String value);

    T form(String name, String filePath, String fileName);

    T form(String name, File file);

    T form(String name, File file, String fileName);

    T form(String name, InputStream in, String fileName);

    T body(String body);

    T body(byte[] bytes);

    T body(InputStream in);

    T jsonBody(Object obj);

    T xmlBody(Object body);

    T basicAuth(String username, String password);

    T proxy(Proxy proxy);

    T proxy(String host, int port);

    T proxy(String host, int port, String username, String password);

    T connectTimeout(long time, TimeUnit unit);

    T readTimeout(long time, TimeUnit unit);

    T writeTimeout(long time, TimeUnit unit);

    /**
     * 设置最大的重定向次数，如果设置为 0 表示不进行自动重定向操作。
     * 默认值为 1，即自动进行一次重定向操作。
     * @param maxCount 重定向次数
     * @return this
     */
    T maxRedirectCount(int maxCount);

    T hostnameVerifier(HostnameVerifier hostnameVerifier);

    T socketFactory(SocketFactory socketFactory);

    T sslSocketFactory(SSLSocketFactoryBuilder builder);

    Response request();

    void requestAsync(Callback callback);

}
