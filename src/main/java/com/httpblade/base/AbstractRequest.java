package com.httpblade.base;

import com.httpblade.common.*;
import com.httpblade.common.form.Form;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AbstractRequest<T extends AbstractRequest> implements Request<T> {

    protected Charset charset = StandardCharsets.UTF_8;
    protected String basicUsername;
    protected String basicPassword;
    protected Form form = new Form();
    protected Headers headers = new Headers();
    protected Body body;

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
    public T formEncoding(String name, String value) {
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
    public T jsonBody(String body) {
        this.body = Body.create(body, ContentType.JSON);
        return (T) this;
    }

    @Override
    public T xmlBody(String body) {
        this.body = Body.create(body, ContentType.XML);
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
            final String data = basicUsername.concat(":").concat(basicPassword);
            setHeader(HttpHeader.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(data.getBytes(this.charset)));
        }
    }

}
