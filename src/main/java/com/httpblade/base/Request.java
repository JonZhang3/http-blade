package com.httpblade.base;

import com.httpblade.common.HttpMethod;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    T queryString(String name, Collection<String> values);

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

    URL getUrl();

    HttpMethod getMethod();

}
