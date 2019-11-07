package com.httpblade.apachehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.common.ContentType;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.HttpUrl;
import com.httpblade.common.form.Field;
import com.httpblade.common.form.Form;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApacheHttpRequestImpl extends AbstractRequest<ApacheHttpRequestImpl> {

    private HttpMethod method;
    private String url;
    private HeaderGroup headerGroup = new HeaderGroup();

    public ApacheHttpRequestImpl() {

    }

    @Override
    public ApacheHttpRequestImpl url(String url) {
        if(url == null) {
            throw new HttpBladeException("the url is null");
        }
        this.url = url;
        return this;
    }

    @Override
    public ApacheHttpRequestImpl method(HttpMethod method) {
        this.method = method;
        return this;
    }

    @Override
    public ApacheHttpRequestImpl setHeader(String name, String value) {
        this.headerGroup.updateHeader(new BasicHeader(name, value));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl addHeader(String name, String value) {
        this.headerGroup.addHeader(new BasicHeader(name, value));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl removeHeader(String name) {
        if(name != null) {
            HeaderIterator iterator = headerGroup.iterator();
            while (iterator.hasNext()) {
                Header header = iterator.nextHeader();
                if(name.equalsIgnoreCase(header.getName())) {
                    iterator.remove();
                }
            }
        }

        return this;
    }

    @Override
    public boolean containsHeader(String name) {
        return headerGroup.containsHeader(name);
    }

    @Override
    public String header(String name) {
        Header header = headerGroup.getFirstHeader(name);
        if(header != null) {
            return header.getValue();
        }
        return null;
    }

    @Override
    public List<String> headers(String name) {
        HeaderIterator iterator = headerGroup.iterator(name);
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.nextHeader().getValue());
        }
        return result;
    }

    @Override
    public Map<String, List<String>> allHeaders() {
        Header[] allHeaders = headerGroup.getAllHeaders();
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
        url = url.replaceAll("\\{ + name + \\}", value);
        return this;
    }

    @Override
    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    HttpUriRequest build(Headers globalHeaders, CookieHome cookieHome) {
        HttpRequestBase request;
        URI uri = URI.create(url);
        HttpEntity entity = null;
        if(!HttpMethod.requiresRequestBody(this.method)) {
            URIBuilder uriBuilder = new URIBuilder(uri).setCharset(this.charset);
            this.form.forEachFields(this.charset, (index, name, value) -> uriBuilder.addParameter(name, value));
            try {
                uri = uriBuilder.build();
            } catch (URISyntaxException ignore) {
            }
        } else if(body != null) {
            entity = this.body.createApacheHttpEntity("", charset);
        } else {
            entity = new MultipartFormEntity(form, charset);
        }
        if(entity == null) {
            request = new HttpRequestImpl(method.value());
        } else {
            request = new HttpEntityRequestImpl(method.value());
            ((HttpEntityRequestImpl) request).setEntity(entity);
        }
        request.setURI(uri);
        setGlobalHeaders(request, globalHeaders);
        request.setHeaders(headerGroup.getAllHeaders());
        addCookie(request, cookieHome);
//        if (body != null) {
//            builder.setEntity(this.body.createApacheHttpEntity(contentType, charset));
//            if (contentType == null) {
//                this.setHeader(HttpHeader.CONTENT_TYPE, body.getContentType());
//            }
//        } else {
//            if (builder.getMethod().equalsIgnoreCase(HttpMethod.GET.value()) || this.form.onlyNormalField()) {
//                for (Field field : form.fields()) {
//                    builder.addParameter(field.name(), field.value());
//                }
//                if (contentType == null) {
//                    this.setHeader(HttpHeader.CONTENT_TYPE, ContentType.FORM);
//                }
//            } else {
//                MultipartFormEntity multipartFormEntity = new MultipartFormEntity(form, charset);
//                if (contentType == null) {
//                    this.setHeader(HttpHeader.CONTENT_TYPE, form.contentType());
//                }
//                builder.setEntity(multipartFormEntity);
//            }
//        }
        return request;
    }

    private static void setGlobalHeaders(HttpRequestBase request, Headers globalHeaders) {
        if (globalHeaders != null) {
            globalHeaders.forEach((name, values) -> {
                if (values.size() == 1) {
                    request.setHeader(name, values.get(0));
                } else {
                    for (String value : values) {
                        request.addHeader(name, value);
                    }
                }
            });
        }
    }

    private static void addCookie(HttpRequestBase request, CookieHome cookieHome) {
        if (cookieHome != null) {
            URL url = null;
            try {
                url = request.getURI().toURL();
            } catch (MalformedURLException ignore) {
            }
            List<Cookie> cookies = cookieHome.load(url);
            request.setHeader(HttpHeader.COOKIE, Cookie.join(cookies));
        }
    }

    private static class HttpRequestImpl extends HttpRequestBase {

        private String method;

        HttpRequestImpl(String method) {
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }
    }

    private static class HttpEntityRequestImpl extends HttpEntityEnclosingRequestBase {

        private String method;

        HttpEntityRequestImpl(String method) {
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }
    }

}
