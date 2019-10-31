package com.httpblade.common.form;

import java.io.InputStream;

public class StreamField {

    private String name;
    private String fileName;
    private InputStream in;

    public StreamField(String name, InputStream in, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.in = in;
    }

    public String name() {
        return name;
    }

    public String fileName() {
        return fileName;
    }

    public InputStream inputStream() {
        return in;
    }

}
