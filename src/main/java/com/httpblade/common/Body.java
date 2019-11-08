package com.httpblade.common;

import com.httpblade.okhttp.StreamRequestBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public String getContentType() {
        return contentType;
    }

    public RequestBody createOkhttpRequestBody(String contentType, Charset charset) {
        if (data == null) {
            return null;
        }
        if (contentType != null) {
            this.contentType = contentType;
        }
        if (isBytes()) {
            if (this.contentType == null) {
                this.contentType = ContentType.OCTET_STREAM;
            }
            return RequestBody.create(MediaType.parse(this.contentType), getBytesData());
        } else if (isString()) {
            String result = getStringData();
            if (this.contentType == null) {
                this.contentType = ContentType.guessContentType(result);
            }
            return RequestBody.create(MediaType.parse(ContentType.addCharset(this.contentType, charset)), result);
        } else if (isStream()) {
            if (this.contentType == null) {
                this.contentType = ContentType.OCTET_STREAM;
            }
            return new StreamRequestBody(getStreamData());
        }
        return null;
    }

    public HttpEntity createApacheHttpEntity(String contentType, Charset charset) {
        if (data == null) {
            return null;
        }
        if (contentType != null) {
            this.contentType = contentType;
        }
        if (isBytes()) {
            if (this.contentType == null) {
                this.contentType = ContentType.OCTET_STREAM;
            }
            return new ByteArrayEntity(getBytesData(), org.apache.http.entity.ContentType.parse(this.contentType));
        } else if (isString()) {
            String result = getStringData();
            if (this.contentType == null) {
                this.contentType = ContentType.guessContentType(result);
            }
            return new StringEntity(result,
                org.apache.http.entity.ContentType.parse(ContentType.addCharset(this.contentType, charset)));
        } else if (isStream()) {
            if (this.contentType == null) {
                this.contentType = ContentType.OCTET_STREAM;
            }
            return new InputStreamEntity(getStreamData(), org.apache.http.entity.ContentType.parse(this.contentType));
        }
        return null;
    }

    public void writeTo(String contentType, OutputStream out, Charset charset) throws IOException {
        if (data == null) {
            return;
        }
        if (contentType != null) {
            this.contentType = contentType;
        }
        if (isString()) {
            String result = getStringData();
            if (this.contentType == null) {
                this.contentType = ContentType.guessContentType(result);
            }
            out.write(getStringData().getBytes(charset));
        } else if (isBytes()) {
            if (this.contentType == null) {
                this.contentType = ContentType.OCTET_STREAM;
            }
            out.write(getBytesData());
        } else {
            if (this.contentType == null) {
                this.contentType = ContentType.OCTET_STREAM;
            }
            Utils.copy(getStreamData(), out);
        }
    }

    private String getStringData() {
        return isString() ? (String) data : "";
    }

    private byte[] getBytesData() {
        return getBytesData(StandardCharsets.UTF_8);
    }

    private InputStream getStreamData() {
        return (InputStream) data;
    }

    private byte[] getBytesData(Charset charset) {
        return isBytes() ? (byte[]) data : ((String) data).getBytes(charset);
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
