package com.httpblade.apachehttp;

import com.httpblade.common.HttpHeader;
import com.httpblade.common.form.Form;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;
import java.nio.charset.Charset;

public class MultipartFormEntity implements HttpEntity {

    private Form form;
    private Charset charset;

    MultipartFormEntity(Form form, Charset charset) {
        this.form = form;
        this.charset = charset;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeTo(baos);
            baos.flush();
            return baos.size();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public Header getContentType() {
        return new BasicHeader(HttpHeader.CONTENT_TYPE, form.contentType());
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeTo(baos);
        baos.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        this.form.writeTo(outstream, charset);
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Deprecated
    @Override
    public void consumeContent() {
    }
}
