package com.httpblade;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Response {

    int status();

    boolean isOk();

    boolean isGzip();

    boolean isDeflate();

    String string();

    <T> T json(Type type);

    <T> T xml(Class<T> type);

    InputStream stream();

    Reader reader();

    File toFile(String path);

    byte[] bytes();

    String header(String name);

    String header(String name, String defaultValue);

    Date dateHeader(String name);

    List<String> headers(String name);

    List<Date> dateHeaders(String name);

    Map<String, List<String>> allHeaders();

    String contentType();

    long contentLength();

    List<Cookie> cookies();

    Cookie cookie(String name);

    boolean hasError();

    Exception exception();

    Object raw();

    void close();

}
