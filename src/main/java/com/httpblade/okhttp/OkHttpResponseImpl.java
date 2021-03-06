package com.httpblade.okhttp;

import com.httpblade.HttpBladeException;
import com.httpblade.base.Cookie;
import com.httpblade.common.HttpHeader;
import com.httpblade.common.Utils;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;
import java.util.List;

public class OkHttpResponseImpl implements com.httpblade.base.Response {

    private Response response;

    OkHttpResponseImpl(Response response) {
        this.response = response;
    }

    @Override
    public int status() {
        return response.code();
    }

    @Override
    public boolean isOk() {
        return response.isSuccessful();
    }

    @Override
    public boolean isGzip() {
        String contentEncoding = response.header(HttpHeader.CONTENT_ENCODING);
        return "gzip".equalsIgnoreCase(contentEncoding);
    }

    @Override
    public boolean isDeflate() {
        String contentEncoding = response.header(HttpHeader.CONTENT_ENCODING);
        return "deflate".equalsIgnoreCase(contentEncoding);
    }

    @Override
    public String string() {
        ResponseBody body = response.body();
        if (body != null) {
            try {
                return body.string();
            } catch (IOException e) {
                throw new HttpBladeException(e);
            }
        }
        return "";
    }

    @Override
    public InputStream stream() {
        ResponseBody body = response.body();
        if (body != null) {
            return body.byteStream();
        }
        return null;
    }

    @Override
    public Reader reader() {
        ResponseBody body = response.body();
        if (body != null) {
            return body.charStream();
        }
        return null;
    }

    @Override
    public File toFile(String path) {
        ResponseBody body = response.body();
        if (body != null) {
            try {
                return Utils.writeToFile(path, body.byteStream());
            } catch (IOException e) {
                throw new HttpBladeException(e);
            }
        }
        return null;
    }

    @Override
    public byte[] bytes() {
        ResponseBody body = response.body();
        if (body != null) {
            try {
                return body.bytes();
            } catch (IOException e) {
                throw new HttpBladeException(e);
            }
        } else {
            return new byte[0];
        }
    }

    @Override
    public String header(String name) {
        return response.header(name);
    }

    @Override
    public String header(String name, String defaultValue) {
        return response.header(name, defaultValue);
    }

    @Override
    public List<String> headers(String name) {
        return response.headers(name);
    }

    @Override
    public String contentType() {
        return response.header(HttpHeader.CONTENT_TYPE, "");
    }

    @Override
    public long contentLength() {
        String len = response.header(HttpHeader.CONTENT_LENGTH, "0");
        return Integer.parseInt(len);
    }

    @Override
    public List<Cookie> cookies() {
        return null;
    }

    @Override
    public Cookie cookie(String name) {
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
        response.close();
    }
}
