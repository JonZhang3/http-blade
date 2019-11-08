package com.httpblade.common.form;

import com.httpblade.HttpBladeException;
import com.httpblade.common.ContentType;
import com.httpblade.common.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class Form {

    private static final String CRLF = "\r\n";
    private static final String TWO_DASHES = "--";
    private static final String BOUNDARY = "--------------------HttpBlade";
    private static final String BOUNDARY_END = String.format("--%s--\n", BOUNDARY);

    private static final String CONTENT_DISPOSITION_TEMPLATE =
        "Content-Disposition: form-data; name=\"%s\"" + CRLF + CRLF;
    private static final String CONTENT_DISPOSITION_FILE_TEMPLATE =
        "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"" + CRLF;
    private static final String CONTENT_TYPE_MULTIPART_PREFIX = "multipart/form-data; boundary=";
    private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: %s\r\n\r\n";

    private List<Field> fields = new LinkedList<>();
    private List<FileField> fileFields = new LinkedList<>();
    private List<StreamField> streamFields = new LinkedList<>();

    public void add(String name, String value) {
        fields.add(new Field(name, value));
    }

    public void add(String name, String value, boolean encoded) {
        fields.add(new Field(name, value, encoded));
    }

    public void addFile(String name, File file) {
        fileFields.add(new FileField(name, file));
    }

    public void addFile(String name, File file, String fileName) {
        if (!file.exists()) {
            throw new HttpBladeException("the file not found");
        }
        fileFields.add(new FileField(name, file, fileName));
    }

    public void addFile(String name, String filePath, String fileName) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new HttpBladeException("the file not found");
        }
        fileFields.add(new FileField(name, file, fileName));
    }

    public void addStream(String name, InputStream in, String fileName) {
        streamFields.add(new StreamField(name, in, fileName));
    }

    public void addBytes(String name, byte[] bytes, String fileName) {
        streamFields.add(new StreamField(name, new ByteArrayInputStream(bytes), fileName));
    }

    public List<Field> fields() {
        return fields;
    }

    public List<FileField> fileFields() {
        return fileFields;
    }

    public List<StreamField> streamFields() {
        return streamFields;
    }

    public boolean hasNormalField() {
        return !this.fields.isEmpty();
    }

    public boolean hasFileField() {
        return !this.fileFields.isEmpty();
    }

    public boolean hasStreamField() {
        return !this.streamFields.isEmpty();
    }

    public boolean onlyNormalField() {
        return this.fileFields.isEmpty() && this.streamFields.isEmpty();
    }

    public boolean isEmpty() {
        return this.fields.isEmpty() && this.fileFields.isEmpty() && this.streamFields.isEmpty();
    }

    public void clear() {
        this.fields.clear();
        this.fileFields.clear();
        this.streamFields.clear();
    }

    public String contentType() {
        if (onlyNormalField()) {
            return ContentType.FORM;
        }
        return ContentType.MULTIPART + ";boundary=" + BOUNDARY;
    }

    public String getBoundary() {
        return BOUNDARY;
    }

    public void forEachFields(Charset charset, Function<Integer, String, String> function) {
        Field field;
        for(int i = 0, len = fields.size(); i < len; i++) {
            field = fields.get(i);
            String name = field.name();
            String value = field.value();
            if(!field.encoded()) {
                name = Utils.encode(name, charset.name());
                value = Utils.encode(value, charset.name());
            }
            function.apply(i, name, value);
        }
    }

    // 拼接成请求参数格式的字符串
    public String toParams(Charset charset) {
        StringBuilder builder = new StringBuilder();
        forEachFields(charset, (index, name, value) -> {
            if(index != 0) {
                builder.append('&');
            }
            builder.append(name);
            if(value != null) {
                builder.append('=').append(value);
            }
        });
        return builder.toString();
    }

    public void writeTo(OutputStream out, Charset charset) throws IOException {
        if (onlyNormalField()) {
            String content = toParams(charset);
            out.write(content.getBytes(charset));
        } else {
            for (FileField fileField : this.fileFields) {
                appendPart(out, charset, fileField.name(), fileField.file(), fileField.fileName());
            }
            for (StreamField streamField : this.streamFields) {
                appendPart(out, charset, streamField.name(), streamField.inputStream(), streamField.fileName());
            }
            StringBuilder sb = new StringBuilder();
            for (Field field : this.fields) {
                sb.append(TWO_DASHES).append(BOUNDARY).append(CRLF);
                String name = field.name();
                String value = field.value();
                if (!field.encoded()) {
                    name = Utils.encode(name, charset.name());
                    value = Utils.encode(value, charset.name());
                }
                sb.append(String.format(CONTENT_DISPOSITION_TEMPLATE, name)).append(value).append(CRLF);
            }
            Utils.write(out, charset, sb.toString());
            out.write(BOUNDARY_END.getBytes(charset));
            out.flush();
        }
    }

    private void appendPart(OutputStream out, Charset charset, String fieldName, Object content, String fileName) {
        String builder = TWO_DASHES + BOUNDARY + CRLF +
            String.format(CONTENT_DISPOSITION_FILE_TEMPLATE, fieldName, fileName) +
            String.format(CONTENT_TYPE_FILE_TEMPLATE, Utils.guessMediaType(fileName));
        Utils.write(out, charset, builder);
        if (content instanceof File) {
            Utils.copy((File) content, out);
        } else {
            Utils.copy((InputStream) content, out);
        }
        Utils.write(out, charset, CRLF);
    }

    @FunctionalInterface
    public interface Function<A, B, C> {
        void apply(A a, B b, C c);
    }

}
