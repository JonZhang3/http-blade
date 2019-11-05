package com.httpblade.base;

import com.httpblade.common.HttpStatus;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

public interface Response {

    int status();

    boolean isOk();

    boolean isGzip();

    boolean isDeflate();

    String string();

    <T> T json(Class<T> type);

    <T> T xml(Class<T> type);

    InputStream stream();

    Reader reader();

    File toFile(String path);

    byte[] bytes();

    String header(String name);

    String header(String name, String defaultValue);

    List<String> headers(String name);

    String contentType();

    long contentLength();

    List<Cookie> cookies();

    Cookie cookie(String name);

    boolean hasError();

    Exception exception();

    Object raw();

    void close();

    default boolean isOk(int code) {
        return code >= HttpStatus.OK && code < 300;
    }

}
