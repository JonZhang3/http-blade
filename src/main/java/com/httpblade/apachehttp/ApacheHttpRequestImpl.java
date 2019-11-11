package com.httpblade.apachehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.AbstractRequest;
import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;

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
    private HttpEntityRequestImpl request = new HttpEntityRequestImpl();

    public ApacheHttpRequestImpl() {
        Defaults.setDefaultHeaders(request);
    }

    @Override
    public ApacheHttpRequestImpl url(String url) {
        if (url == null) {
            throw new HttpBladeException("must specify a http url");
        }
        this.url = url;
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
        return request.containsHeader(name);
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
        if (url == null) {
            throw new NullPointerException("the url is null");
        }
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
        if (method == null) {
            throw new HttpBladeException("must specify a http method");
        }
        if (url == null) {
            throw new HttpBladeException("must specify a http url");
        }
        String contentType = this.header(HttpHeader.CONTENT_TYPE);
        URI uri = URI.create(url);
        HttpEntity entity = null;
        if (!HttpMethod.requiresRequestBody(this.method)) {
            URIBuilder uriBuilder = new URIBuilder(uri).setCharset(this.charset);
            this.form.forEachFields(this.charset, (index, name, value) -> uriBuilder.addParameter(name, value));
            try {
                uri = uriBuilder.build();
            } catch (URISyntaxException ignore) {
                // It shouldn't happen.
            }
        } else if (body != null) {
            entity = this.body.createApacheHttpEntity(contentType, charset);
            if (this.form.onlyNormalField()) {
                URIBuilder uriBuilder = new URIBuilder(uri).setCharset(this.charset);
                this.form.forEachFields(this.charset, (index, name, value) -> uriBuilder.addParameter(name, value));
                try {
                    uri = uriBuilder.build();
                } catch (URISyntaxException ignore) {
                    // It shouldn't happen.
                }
            } else {
                throw new HttpBladeException("You have provided the request body for the request.");
            }
        } else {
            entity = new MultipartFormEntity(form, charset);
        }

        if (entity != null) {
            request.setEntity(entity);
        }
        request.setURI(uri);
        setGlobalHeaders(request, globalHeaders);
        addCookie(request, cookieHome);
        return request;
    }

    private static void setGlobalHeaders(HttpRequestBase request, Headers globalHeaders) {
        if (globalHeaders != null) {
            globalHeaders.forEach((name, values) -> {
                if (request.containsHeader(name)) {
                    return;
                }
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
                // It shouldn't happen.
            }
            List<Cookie> cookies = cookieHome.load(url);
            request.setHeader(HttpHeader.COOKIE, Cookie.join(cookies));
        }
    }

    private static class HttpEntityRequestImpl extends HttpEntityEnclosingRequestBase {

        private String method;

        public void setMethod(HttpMethod method) {
            this.method = method.value();
        }

        @Override
        public String getMethod() {
            return method;
        }
    }

}
