package com.httpblade;

import com.httpblade.base.Callback;
import com.httpblade.base.HttpClient;
import com.httpblade.base.Request;
import com.httpblade.base.Response;
import com.httpblade.common.HttpMethod;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

final class RequestWrapper {

    private HttpClient client;
    private final Request request;

    RequestWrapper(HttpClient client, Request request, String url, HttpMethod method) {
        this.client = client;
        this.request = request;
        request.url(url);
        request.method(method);
    }

    public RequestWrapper client(HttpClient client) {
        this.client = client;
        return this;
    }

    public RequestWrapper charset(Charset charset) {
        request.charset(charset);
        return this;
    }

    public RequestWrapper contentType(String contentType) {
        request.contentType(contentType);
        return this;
    }

    public RequestWrapper contentLength(long length) {
        request.contentLength(length);
        return this;
    }

    public RequestWrapper setHeader(String name, String value) {
        request.setHeader(name, value);
        return this;
    }

    public RequestWrapper addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    public RequestWrapper removeHeader(String name) {
        request.removeHeader(name);
        return this;
    }

    public boolean containsHeader(String name) {
        request.containsHeader(name);
        return false;
    }

    public String header(String name) {
        return request.header(name);
    }

    @SuppressWarnings("unchecked")
    public List<String> headers(String name) {
        return request.headers(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<String>> allHeaders() {
        return request.allHeaders();
    }

    public RequestWrapper form(Map<String, String> values) {
        request.form(values);
        return this;
    }

    public RequestWrapper form(String name, String value) {
        request.form(name, value);
        return this;
    }

    public RequestWrapper formEncoding(String name, String value) {
        request.formEncoded(name, value);
        return this;
    }

    public RequestWrapper form(String name, String filePath, String fileName) {
        request.form(name, filePath, fileName);
        return this;
    }

    public RequestWrapper form(String name, File file) {
        request.form(name, file);
        return this;
    }

    public RequestWrapper form(String name, File file, String fileName) {
        request.form(name, file, fileName);
        return this;
    }

    public RequestWrapper form(String name, InputStream in, String fileName) {
        request.form(name, in, fileName);
        return this;
    }

    public RequestWrapper body(String body) {
        request.body(body);
        return this;
    }

    public RequestWrapper body(byte[] bytes) {
        request.body(bytes);
        return this;
    }

    public RequestWrapper body(InputStream in) {
        request.body(in);
        return this;
    }

    public RequestWrapper jsonBody(String body) {
        request.jsonBody(body);
        return this;
    }

    public RequestWrapper xmlBody(String body) {
        request.xmlBody(body);
        return this;
    }

    public RequestWrapper basicAuth(String username, String password) {
        request.basicAuth(username, password);
        return this;
    }

    public Response request() {
        return client.request(this.request);
    }

    public void requestAsync(Callback callback) {
        client.requestAsync(this.request, callback);
    }

}
