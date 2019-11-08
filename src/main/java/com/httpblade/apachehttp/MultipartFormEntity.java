package com.httpblade.apachehttp;

import com.httpblade.common.HttpHeader;
import com.httpblade.common.form.Form;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class MultipartFormEntity extends AbstractHttpEntity {

    private Form form;
    private Charset charset;

    MultipartFormEntity(Form form, Charset charset) {
        this.form = form;
        this.charset = charset;
        this.contentType = new BasicHeader(HttpHeader.CONTENT_TYPE, form.contentType());
        this.contentEncoding = new BasicHeader(HttpHeader.CONTENT_ENCODING, charset.name());
        this.chunked = false;
    }

    @Override
    public boolean isRepeatable() {
        return true;
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
    public InputStream getContent() throws IOException {
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

}
