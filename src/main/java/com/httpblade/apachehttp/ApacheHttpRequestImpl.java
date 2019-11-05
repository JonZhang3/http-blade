package com.httpblade.apachehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.common.ContentType;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.form.Field;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApacheHttpRequestImpl extends AbstractRequest<ApacheHttpRequestImpl> {

    private HttpRequestImpl request;
    private HttpMethod method;

    public ApacheHttpRequestImpl() {
        request = new HttpRequestImpl();
    }

    @Override
    public ApacheHttpRequestImpl url(String url) {
        request.setURI(URI.create(url));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl method(HttpMethod method) {
        this.method = method;
        request.setMethod(method);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl setHeader(String name, String value) {
        request.setHeader(name, value);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl removeHeader(String name) {
        request.removeHeaders(name);
        return this;
    }

    @Override
    public boolean containsHeader(String name) {
        return this.request.containsHeader(name);
    }

    @Override
    public String header(String name) {
        Header header = request.getFirstHeader(name);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    @Override
    public List<String> headers(String name) {
        HeaderIterator iterator = request.headerIterator(name);
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.nextHeader().getValue());
        }
        return result;
    }

    @Override
    public Map<String, List<String>> allHeaders() {
        Header[] allHeaders = request.getAllHeaders();
        Headers headers = new Headers();
        if (allHeaders != null) {
            for (Header header : allHeaders) {
                headers.add(header.getName(), header.getValue());
            }
        }
        return headers.get();
    }

    @Override
    public ApacheHttpRequestImpl pathVariable(String name, String value) {
        return this;
    }

    @Override
    public URL getUrl() {
        try {
            return request.getURI().toURL();
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    HttpUriRequest build(Headers globalHeaders, CookieHome cookieHome) {
        RequestBuilder builder = RequestBuilder.copy(request);
        setGlobalHeaders(builder, globalHeaders);
        addCookie(builder, cookieHome);
        String contentType = this.header(HttpHeader.CONTENT_TYPE);
        if (body != null) {
            builder.setEntity(this.body.createApacheHttpEntity(contentType, charset));
            if (contentType == null) {
                this.setHeader(HttpHeader.CONTENT_TYPE, body.getContentType());
            }
        } else {
            if (builder.getMethod().equalsIgnoreCase(HttpMethod.GET.value()) || this.form.onlyNormalField()) {
                for (Field field : form.fields()) {
                    builder.addParameter(field.name(), field.value());
                }
                if (contentType == null) {
                    this.setHeader(HttpHeader.CONTENT_TYPE, ContentType.FORM);
                }
            } else {
                MultipartFormEntity multipartFormEntity = new MultipartFormEntity(form, charset);
                if (contentType == null) {
                    this.setHeader(HttpHeader.CONTENT_TYPE, form.contentType());
                }
                builder.setEntity(multipartFormEntity);
            }
        }
        return builder.build();
    }

    private static void setGlobalHeaders(RequestBuilder builder, Headers globalHeaders) {
        if (globalHeaders != null) {
            globalHeaders.forEach((name, values) -> {
                if (values.size() == 1) {
                    builder.setHeader(name, values.get(0));
                } else {
                    for (String value : values) {
                        builder.addHeader(name, value);
                    }
                }
            });
        }
    }

    private static void addCookie(RequestBuilder builder, CookieHome cookieHome) {
        if (cookieHome != null) {
            URL url = null;
            try {
                url = builder.getUri().toURL();
            } catch (MalformedURLException ignore) {
            }
            List<Cookie> cookies = cookieHome.load(url);
            builder.setHeader(HttpHeader.COOKIE, Cookie.join(cookies));
        }
    }

    private static class HttpRequestImpl extends HttpRequestBase {

        private String method;

        HttpRequestImpl() {

        }

        void setMethod(HttpMethod method) {
            this.method = method.value();
        }

        @Override
        public String getMethod() {
            return method;
        }
    }

}
