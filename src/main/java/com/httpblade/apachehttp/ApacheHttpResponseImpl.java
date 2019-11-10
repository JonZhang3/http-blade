package com.httpblade.apachehttp;

import com.httpblade.HttpBlade;
import com.httpblade.HttpBladeException;
import com.httpblade.JsonParserFactory;
import com.httpblade.XmlParserFactory;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.base.Response;
import com.httpblade.common.ContentType;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpStatus;
import com.httpblade.common.Utils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ApacheHttpResponseImpl implements Response {

    private CloseableHttpResponse response;
    private URL url;
    private List<Cookie> cookies = new LinkedList<>();
    private String charset = StandardCharsets.UTF_8.name();

    ApacheHttpResponseImpl(CloseableHttpResponse response, URL url, CookieHome cookieHome) {
        this.response = response;
        this.url = url;
        initCookies(cookieHome);
    }

    private void initCookies(CookieHome cookieHome) {
        Header[] headers = this.response.getHeaders(HttpHeader.SET_COOKIE);
        if (headers != null && headers.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Header header : headers) {
                HeaderElement[] elements = header.getElements();
                for (int i = 0, len = elements.length; i < len; i++) {
                    HeaderElement el = elements[i];
                    sb.append(el.getName()).append("=").append(el.getValue());
                    if (i < len - 1) {
                        sb.append(";");
                    }
                }
                Cookie cookie = Cookie.parse(this.url, sb.toString());
                if (cookie != null) {
                    cookies.add(cookie);
                }
            }
        }
        if (cookieHome != null && !cookies.isEmpty()) {
            cookieHome.save(this.url, cookies);
        }
        ContentType contentType = ContentType.parse(contentType());
        if (contentType != null && contentType.getCharset() != null) {
            this.charset = contentType.getCharset();
        }
    }

    @Override
    public int status() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public boolean isOk() {
        return HttpStatus.isOk(status());
    }

    @Override
    public boolean isGzip() {
        String encoding = header(HttpHeader.CONTENT_ENCODING);
        return "gzip".equalsIgnoreCase(encoding);
    }

    @Override
    public boolean isDeflate() {
        String encoding = header(HttpHeader.CONTENT_ENCODING);
        return "deflate".equalsIgnoreCase(encoding);
    }

    @Override
    public String string() {
        try {
            return Utils.decode(EntityUtils.toString(response.getEntity(), charset), charset);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public <T> T json(Type type) {
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
        try {
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public Reader reader() {
        try {
            InputStream stream = response.getEntity().getContent();
            return new InputStreamReader(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public File toFile(String path) {
        try {
            InputStream stream = response.getEntity().getContent();
            return Utils.writeToFile(path, stream);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public byte[] bytes() {
        try {
            return EntityUtils.toByteArray(response.getEntity());
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public String header(String name) {
        Header header = response.getFirstHeader(name);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    @Override
    public String header(String name, String defaultValue) {
        Header header = response.getFirstHeader(name);
        if (header != null) {
            return header.getValue();
        }
        return defaultValue;
    }

    @Override
    public List<String> headers(String name) {
        Header[] headers = response.getHeaders(name);
        List<String> result = new LinkedList<>();
        if (headers != null) {
            for (Header header : headers) {
                result.add(header.getValue());
            }
        }
        return result;
    }

    @Override
    public Map<String, List<String>> allHeaders() {
        Map<String, List<String>> result = new HashMap<>();
        HeaderIterator iterator = response.headerIterator();
        while (iterator.hasNext()) {
            Header header = iterator.nextHeader();
            String name = header.getName().toLowerCase(Locale.US);
            String value = header.getValue();
            List<String> values = result.computeIfAbsent(name, k -> new LinkedList<>());
            values.add(value);
        }
        return result;
    }

    @Override
    public String contentType() {
        return this.header(HttpHeader.CONTENT_TYPE);
    }

    @Override
    public long contentLength() {
        String length = this.header(HttpHeader.CONTENT_LENGTH);
        if (length != null) {
            try {
                return Long.parseLong(length);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public List<Cookie> cookies() {
        return this.cookies;
    }

    @Override
    public Cookie cookie(String name) {
        if (this.cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.name().equals(name)) {
                    return cookie;
                }
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
        return response;
    }

    @Override
    public void close() {
        try {
            response.close();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }
}
