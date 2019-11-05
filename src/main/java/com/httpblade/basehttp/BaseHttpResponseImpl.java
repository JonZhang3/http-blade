package com.httpblade.basehttp;

import com.httpblade.HttpBlade;
import com.httpblade.HttpBladeException;
import com.httpblade.JsonParserFactory;
import com.httpblade.XmlParserFactory;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.base.Response;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

public class BaseHttpResponseImpl implements Response {

    private int status;
    private InputStream in;
    private Headers headers;
    private List<Cookie> cookies;
    private BaseHttpConnection conn;
    private Charset charset;

    BaseHttpResponseImpl(BaseHttpConnection conn) throws IOException {
        this.conn = conn;
        init();
    }

    private void init() throws IOException {
        HttpURLConnection conn = this.conn.getConnection();
        CookieHome cookieHome = this.conn.getCookieHome();
        URL url = this.conn.getUrl();
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        this.headers = new Headers(headerFields);
        List<Cookie> cookies = Cookie.parseAll(url, headerFields.get(HttpHeader.SET_COOKIE));
        this.cookies = cookies;
        if (cookieHome != null) {
            cookieHome.save(url, cookies);
        }

        int status = conn.getResponseCode();
        this.status = status;
        InputStream in = HttpStatus.isBad(status) ? conn.getErrorStream() : conn.getInputStream();
        if (in != null) {
            if (isGzip() && !(in instanceof GZIPInputStream)) {
                try {
                    in = new GZIPInputStream(in);
                } catch (IOException ignore) {
                }
            } else if (isDeflate() && !(in instanceof DeflaterInputStream)) {
                in = new DeflaterInputStream(in);
            }
        }
        this.in = in;

    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public boolean isOk() {
        return HttpStatus.isOk(status);
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
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return new String(bytes(), charset);
    }

    @Override
    public <T> T json(Class<T> type) {
        JsonParserFactory factory = HttpBlade.getJsonParserFactory();
        if (factory != null) {
            return factory.fromJson(string(), type);
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
        return this.in;
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(stream());
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
        Utils.copy(this.in, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException ignore) {
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
    public List<String> headers(String name) {
        return headers.getList(name);
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
        return conn.getConnection();
    }

    @Override
    public void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                throw new HttpBladeException(e);
            }
        }
        if (conn != null) {
            conn.close();
        }
    }

}
