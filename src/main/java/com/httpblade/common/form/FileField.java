package com.httpblade.common.form;

import java.io.File;

public class FileField {

    private String name;
    private String fileName;
    private File file;

    public FileField(String name, File file) {
        this.name = name;
        this.file = file;
        this.fileName = file.getName();
    }

    public FileField(String name, File file, String fileName) {
        this.name = name;
        this.file = file;
        this.fileName = fileName;
    }

    public String name() {
        return name;
    }

    public String fileName() {
        return fileName;
    }

    public File file() {
        return file;
    }

}
