package com.httpblade;

import com.httpblade.common.Body;
import com.httpblade.common.ContentType;
import com.httpblade.common.Headers;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.HttpMethod;
import com.httpblade.common.Utils;
import com.httpblade.common.form.Form;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AbstractRequest<T extends AbstractRequest> implements Request<T> {

    protected final HttpClient client;
    protected Charset charset = StandardCharsets.UTF_8;
    protected String basicUsername;
    protected String basicPassword;
    protected Form form = new Form();
    protected Headers headers = new Headers();
    protected Body body;

    protected AbstractRequest(final HttpClient client) {
        this.client = client;
    }

    @Override
    public T get(String url) {
        this.url(url);
        this.method(HttpMethod.GET);
        return (T) this;
    }

    @Override
    public T post(String url) {
        this.url(url);
        this.method(HttpMethod.POST);
        return (T) this;
    }

    @Override
    public T put(String url) {
        this.url(url);
        this.method(HttpMethod.PUT);
        return (T) this;
    }

    @Override
    public T delete(String url) {
        this.url(url);
        this.method(HttpMethod.DELETE);
        return (T) this;
    }

    @Override
    public T head(String url) {
        this.url(url);
        this.method(HttpMethod.HEAD);
        return (T) this;
    }

    @Override
    public T options(String url) {
        this.url(url);
        this.method(HttpMethod.OPTIONS);
        return (T) this;
    }

    @Override
    public T trace(String url) {
        this.url(url);
        this.method(HttpMethod.TRACE);
        return (T) this;
    }

    @Override
    public T connect(String url) {
        this.url(url);
        this.method(HttpMethod.CONNECT);
        return (T) this;
    }

    @Override
    public T patch(String url) {
        this.url(url);
        this.method(HttpMethod.PATCH);
        return (T) this;
    }

    @Override
    public T charset(Charset charset) {
        if (charset != null) {
            this.charset = charset;
        }
        return (T) this;
    }

    @Override
    public T contentType(String contentType) {
        this.setHeader(HttpHeader.CONTENT_TYPE, contentType);
        return (T) this;
    }

    @Override
    public T contentLength(long length) {
        this.setHeader(HttpHeader.CONTENT_LENGTH, length + "");
        return (T) this;
    }

    @Override
    public T queryString(String name, String value) {
        return form(name, value);
    }

    @Override
    public T queryString(String name, String value, boolean encoded) {
        return formEncoded(name, value);
    }

    @Override
    public T form(Map<String, String> values) {
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                form(entry.getKey(), entry.getValue());
            }
        }
        return (T) this;
    }

    @Override
    public T form(String name, String value) {
        form.add(name, value);
        return (T) this;
    }

    @Override
    public T formEncoded(String name, String value) {
        form.add(name, value, true);
        return (T) this;
    }

    @Override
    public T form(String name, String filePath, String fileName) {
        form.addFile(name, filePath, fileName);
        return (T) this;
    }

    @Override
    public T form(String name, File file) {
        form.addFile(name, file);
        return (T) this;
    }

    @Override
    public T form(String name, File file, String fileName) {
        form.addFile(name, file, fileName);
        return (T) this;
    }

    @Override
    public T form(String name, InputStream in, String fileName) {
        form.addStream(name, in, fileName);
        return (T) this;
    }

    @Override
    public T body(String body) {
        this.body = Body.create(body, ContentType.guessContentType(body));
        return (T) this;
    }

    @Override
    public T body(byte[] bytes) {
        this.body = Body.create(bytes);
        return (T) this;
    }

    @Override
    public T body(InputStream in) {
        this.body = Body.create(in);
        return (T) this;
    }

    @Override
    public T jsonBody(Object value) {
        if (value != null) {
            String result;
            if (value instanceof CharSequence) {
                result = value.toString();
            } else {
                JsonParserFactory factory = HttpBlade.getJsonParserFactory();
                if (factory != null) {
                    result = factory.toJson(value);
                } else {
                    result = value.toString();
                }
            }
            this.body = Body.create(result, ContentType.JSON);
        }
        return (T) this;
    }

    @Override
    public T xmlBody(Object body) {
        if (body != null) {
            String result;
            if (body instanceof CharSequence) {
                result = body.toString();
            } else {
                XmlParserFactory factory = HttpBlade.getXmlParserFactory();
                if (factory != null) {
                    result = factory.toXml(body);
                } else {
                    result = body.toString();
                }
            }
            this.body = Body.create(result, ContentType.XML);
        }
        return (T) this;
    }

    @Override
    public T basicAuth(String username, String password) {
        this.basicUsername = username;
        this.basicPassword = password;
        return (T) this;
    }

    protected void setBasicAuth() {
        if (basicUsername != null) {
            setHeader(HttpHeader.AUTHORIZATION, Utils.basicAuthString(basicUsername, basicPassword));
        }
    }

}
