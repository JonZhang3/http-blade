package com.httpblade.okhttp;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;

public class StreamRequestBody extends RequestBody {

    private InputStream in;

    public StreamRequestBody(InputStream in) {
        this.in = in;
    }

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try (Source source = Okio.source(in)) {
            sink.writeAll(source);
        }
    }
}
