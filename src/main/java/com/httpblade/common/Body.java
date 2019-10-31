package com.httpblade.common;

import com.httpblade.okhttp.StreamRequestBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Body {

    private static final int TYPE_STRING = 1;
    private static final int TYPE_BYTES = 2;
    private static final int TYPE_STREAM = 3;

    private Object data;
    private int dataType;
    private String contentType;

    private Body(Object data, int dataType, String contentType) {
        this.data = data;
        this.dataType = dataType;
        this.contentType = contentType;
    }

    public boolean isBytes() {
        return this.dataType == TYPE_BYTES;
    }

    public boolean isString() {
        return this.dataType == TYPE_STRING;
    }

    public boolean isStream() {
        return this.dataType == TYPE_STREAM;
    }

    public String getStringData() {
        return isString() ? (String) data : "";
    }

    public byte[] getBytesData() {
        return getBytesData(StandardCharsets.UTF_8);
    }

    public InputStream getStreamData() {
        return isStream() ? (InputStream) data : null;
    }

    public byte[] getBytesData(Charset charset) {
        return isBytes() ? (byte[]) data : ((String) data).getBytes(charset);
    }

    public String getContentType() {
        return contentType;
    }

    public RequestBody createOkhttpRequestBody(String contentType, Charset charset) {
        if(data == null) {
            return null;
        }
        if(contentType == null) {
            contentType = this.contentType;
        }
        if(contentType == null && isString()) {
            contentType = ContentType.guessContentType(getStringData());
        }
        if(isBytes()) {
            return RequestBody.create(MediaType.parse(contentType), getBytesData());
        } else if(isString()) {
            return RequestBody.create(MediaType.parse(ContentType.addCharset(contentType, charset)), getStringData());
        } else if(isStream()) {
            return new StreamRequestBody(getStreamData());
        }
        return null;
    }

    public HttpEntity createApacheHttpEntity(String contentType, Charset charset) {
        if(data == null) {
            return null;
        }
        if(contentType == null) {
            contentType = this.contentType;
        }
        if(contentType == null && isString()) {
            contentType = ContentType.guessContentType(getStringData());
            this.contentType = contentType;
        }
        if(contentType == null) {
            contentType = "";
        }
        if(isBytes()) {
            return new ByteArrayEntity(getBytesData(), org.apache.http.entity.ContentType.parse(contentType));
        } else if(isString()) {
            return new StringEntity(getStringData(),
                org.apache.http.entity.ContentType.parse(ContentType.addCharset(contentType, charset)));
        } else if(isStream()) {
            return new InputStreamEntity(getStreamData(), org.apache.http.entity.ContentType.parse(contentType));
        }
        return null;
    }



    public static Body create(String data) {
        return create(data, null);
    }

    public static Body create(String data, String contentType) {
        return new Body(data, TYPE_STRING, contentType);
    }

    public static Body create(byte[] data) {
        return new Body(data, TYPE_BYTES, null);
    }

    public static Body create(InputStream data) {
        return new Body(data, TYPE_STREAM, null);
    }

}
