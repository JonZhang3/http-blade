package com.httpblade.apachehttp;

import com.httpblade.Callback;
import com.httpblade.HttpBladeException;
import com.httpblade.AbstractRequest;
import com.httpblade.Cookie;
import com.httpblade.CookieHome;
import com.httpblade.HttpClient;
import com.httpblade.Response;
import com.httpblade.common.Defaults;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Proxy;
import com.httpblade.common.Utils;
import com.httpblade.common.form.Field;
import com.httpblade.common.form.Form;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApacheHttpRequestImpl extends AbstractRequest<ApacheHttpRequestImpl> {

    private URI uri = null;
    private HttpMethod method;
    private URIBuilder uriBuilder;
    private HttpEntityRequestImpl request = new HttpEntityRequestImpl();
    private RequestConfig.Builder requestConfigBuilder;
    private CloseableHttpClient rawClient;

    public ApacheHttpRequestImpl(final HttpClient client) {
        super(client);
        rawClient = (CloseableHttpClient) client.raw();
        Defaults.setDefaultHeaders(request);
        setHeaders(request, client.headers());
    }

    @Override
    public ApacheHttpRequestImpl url(final String url) {
        String resultUrl = this.configUrl(url);
        try {
            this.uriBuilder = new URIBuilder(resultUrl);
        } catch (URISyntaxException e) {
            throw new HttpBladeException(e);
        }
        return this;
    }

    @Override
    public ApacheHttpRequestImpl method(HttpMethod method) {
        this.method = method;
        request.setMethod(method);
        setHeaders(request, client.headers(method.name()));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl setHeader(String name, String value) {
        request.setHeader(name, value);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl setDateHeader(String name, Date date) {
        setHeader(name, Utils.formatHttpDate(date));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl addDateHeader(String name, Date date) {
        addHeader(name, Utils.formatHttpDate(date));
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
        String path = uriBuilder.getPath();
        if (path == null) {
            path = "/";
        }
        uriBuilder.setPath(path.replaceAll("\\{ + name + \\}", Utils.encode(value, "UTF-8")));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl queryString(String name, String value) {
        uriBuilder.addParameter(name, value);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl queryString(String name, String value, boolean encoded) {
        uriBuilder.addParameter(name, value);
        return this;
    }

    @Override
    public ApacheHttpRequestImpl proxy(Proxy proxy) {
        if (requestConfigBuilder == null) {
            requestConfigBuilder = RequestConfig.custom();
        }
        if (proxy != null) {
            requestConfigBuilder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort()));
            if (proxy.hasAuth()) {

            }
        }
        return this;
    }

    @Override
    public ApacheHttpRequestImpl proxy(String host, int port) {
        if (requestConfigBuilder == null) {
            requestConfigBuilder = RequestConfig.custom();
        }
        if (Utils.isNotEmpty(host)) {
            requestConfigBuilder.setProxy(new HttpHost(host, port));
        }
        return this;
    }

    @Override
    public ApacheHttpRequestImpl proxy(String host, int port, String username, String password) {
        if (requestConfigBuilder == null) {
            requestConfigBuilder = RequestConfig.custom();
        }
        Proxy proxy = new Proxy(host, port, username, password);
        requestConfigBuilder.setProxy(new HttpHost(host, port));
        if (proxy.hasAuth()) {

        }
        return this;
    }

    @Override
    public ApacheHttpRequestImpl connectTimeout(long time, TimeUnit unit) {
        if (requestConfigBuilder == null) {
            requestConfigBuilder = RequestConfig.custom();
        }
        requestConfigBuilder.setConnectTimeout((int) unit.toMillis(time));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl readTimeout(long time, TimeUnit unit) {
        if (requestConfigBuilder == null) {
            requestConfigBuilder = RequestConfig.custom();
        }
        requestConfigBuilder.setSocketTimeout((int) unit.toMillis(time));
        return this;
    }

    @Override
    public ApacheHttpRequestImpl writeTimeout(long time, TimeUnit unit) {
        return this;
    }

    @Override
    public ApacheHttpRequestImpl maxRedirectCount(int maxCount) {
        if (requestConfigBuilder == null) {
            requestConfigBuilder = RequestConfig.custom();
        }
        requestConfigBuilder.setMaxRedirects(maxCount);
        return this;
    }

    @Override
    public Response request() {
        build();
        try {
            return new ApacheHttpResponseImpl(rawClient.execute(request), uri.toURL(), client.cookieHome());
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    @Override
    public void requestAsync(Callback callback) {
        build();
        // TODO
    }

    @Override
    public URL getUrl() {
        try {
            return uriBuilder.build().toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new HttpBladeException();
        }
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    void build() {
        if (method == null) {
            throw new HttpBladeException("must specify a http method");
        }
        String contentType = this.header(HttpHeader.CONTENT_TYPE);
        uriBuilder.setCharset(charset);
        HttpEntity entity = null;
        if (!HttpMethod.requiresRequestBody(this.method)) {
            addParameter(uriBuilder, this.form, this.charset.name());
            try {
                uri = uriBuilder.build();
            } catch (URISyntaxException ignore) {
                // It shouldn't happen.
            }
        } else if (body != null) {
            entity = this.body.createApacheHttpEntity(contentType, charset);
            if (this.form.onlyNormalField()) {
                addParameter(uriBuilder, this.form, this.charset.name());
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
            try {
                uri = uriBuilder.build();
            } catch (URISyntaxException ignore) {
                // It shouldn't happen.
            }
        }

        if (entity != null) {
            request.setEntity(entity);
        }
        request.setURI(uri);
        addCookie(request, client.cookieHome());
    }

    private static void addParameter(URIBuilder uriBuilder, Form form, String charset) {
        List<Field> fields = form.fields();
        for (Field field : fields) {
            String name = field.name();
            String value = field.value();
            if (field.encoded()) {
                name = Utils.decode(name, charset);
                value = Utils.decode(value, charset);
            }
            uriBuilder.addParameter(name, value);
        }
    }

    private static void setHeaders(HttpRequestBase request, Headers headers) {
        if (headers != null) {
            headers.forEach((name, values) -> {
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
