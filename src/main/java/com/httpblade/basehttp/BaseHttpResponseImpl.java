package com.httpblade.basehttp;

import com.httpblade.HttpBlade;
import com.httpblade.HttpBladeException;
import com.httpblade.JsonParserFactory;
import com.httpblade.XmlParserFactory;
import com.httpblade.Cookie;
import com.httpblade.CookieHome;
import com.httpblade.Response;
import com.httpblade.common.ContentType;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpStatus;
import com.httpblade.common.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

public class BaseHttpResponseImpl implements Response {

    private int statusCode;
    private InputStream inputStream;
    private Headers headers;
    private List<Cookie> cookies;
    private BaseHttpConnection connection;
    private String charset = StandardCharsets.UTF_8.name();

    BaseHttpResponseImpl(BaseHttpConnection conn) throws IOException {
        this.connection = conn;
        init();
    }

    private void init() throws IOException {
        HttpURLConnection conn = this.connection.getConnection();
        CookieHome cookieHome = this.connection.getCookieHome();
        URL url = this.connection.getUrl();
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        this.headers = new Headers(headerFields);
        List<Cookie> cookies = Cookie.parseAll(url, headerFields.get(HttpHeader.SET_COOKIE));
        this.cookies = cookies;
        if (cookieHome != null) {
            cookieHome.save(url, cookies);
        }

        int status = conn.getResponseCode();
        this.statusCode = status;
        InputStream in = HttpStatus.isBad(status) ? conn.getErrorStream() : conn.getInputStream();
        if (in != null) {
            if (isGzip() && !(in instanceof GZIPInputStream)) {
                try {
                    in = new GZIPInputStream(in);
                } catch (IOException ignore) {
                    // It shouldn't happen.
                }
            } else if (isDeflate() && !(in instanceof DeflaterInputStream)) {
                in = new DeflaterInputStream(in);
            }
        }
        this.inputStream = in;
        ContentType contentType = ContentType.parse(headers.get(HttpHeader.CONTENT_TYPE));
        if (contentType != null && contentType.getCharset() != null) {
            this.charset = contentType.getCharset();
        }
    }

    @Override
    public int status() {
        return statusCode;
    }

    @Override
    public boolean isOk() {
        return HttpStatus.isOk(statusCode);
    }

    @Override
    public boolean isGzip() {
        return "gzip".equalsIgnoreCase(headers.get(HttpHeader.CONTENT_ENCODING));
    }

    @Override
    public boolean isDeflate() {
        return "deflate".equalsIgnoreCase(headers.get(HttpHeader.CONTENT_ENCODING));
    }

    @Override
    public String string() {
        try {
            return new String(bytes(), charset);
        } catch (UnsupportedEncodingException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public <T> T json(Type type) {
        JsonParserFactory factory = HttpBlade.getJsonParserFactory();
        String result = string();
        if (factory != null) {
            return factory.fromJson(result, type);
        }
        throw new HttpBladeException("you must specify a JsonParserFactory");
    }

    @Override
    public <T> T xml(Class<T> type) {
        XmlParserFactory factory = HttpBlade.getXmlParserFactory();
        if (factory != null) {
            return factory.fromXml(string(), type);
        }
        throw new HttpBladeException("you must specify a XmlParserFactory");
    }

    @Override
    public InputStream stream() {
        return this.inputStream;
    }

    @Override
    public Reader reader() {
        try {
            return new InputStreamReader(stream(), charset);
        } catch (UnsupportedEncodingException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public File toFile(String path) {
        InputStream stream = stream();
        try {
            return Utils.writeToFile(path, stream);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public byte[] bytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Utils.copy(this.inputStream, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException ignore) {
            // It shouldn't happen.
        }
        return bytes;
    }

    @Override
    public String header(String name) {
        return headers.get(name);
    }

    @Override
    public String header(String name, String defaultValue) {
        String value = headers.get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public Date dateHeader(String name) {
        return null;
    }

    @Override
    public List<String> headers(String name) {
        return headers.getList(name);
    }

    @Override
    public List<Date> dateHeaders(String name) {
        return null;
    }

    @Override
    public Map<String, List<String>> allHeaders() {
        return headers.get();
    }

    @Override
    public String contentType() {
        return headers.get(HttpHeader.CONTENT_TYPE);
    }

    @Override
    public long contentLength() {
        String length = headers.get(HttpHeader.CONTENT_LENGTH);
        if (Utils.isEmpty(length)) {
            return 0;
        }
        return Long.parseLong(length);
    }

    @Override
    public List<Cookie> cookies() {
        return cookies;
    }

    @Override
    public Cookie cookie(String name) {
        for (Cookie cookie : this.cookies) {
            if (cookie.name().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public Exception exception() {
        return null;
    }

    @Override
    public Object raw() {
        return connection.getConnection();
    }

    @Override
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new HttpBladeException(e);
            }
        }
        if (connection != null) {
            connection.close();
        }
    }

}
