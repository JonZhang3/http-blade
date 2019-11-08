package com.httpblade.common;

import com.httpblade.HttpBladeException;

import java.nio.charset.Charset;
import java.util.Locale;

public final class ContentType {

    public static final String JSON = "application/json";
    public static final String XML = "application/xml";
    public static final String TEXT = "text/plain";
    public static final String HTML = "text/html";
    public static final String FORM = "application/x-www-form-urlencoded";
    public static final String MULTIPART = "multipart/form-data";
    public static final String OCTET_STREAM = "application/octet-stream";

    private String raw;
    private String type;
    private String charset;

    public static ContentType parse(String contentType) {
        if(Utils.isEmpty(contentType)) {
            return null;
        }
        ContentType content = new ContentType();
        String[] splitStrs = contentType.split(";");
        String charset = null;
        content.raw = contentType;
        if(splitStrs.length == 1) {
            content.type = splitStrs[0].toLowerCase(Locale.US);
        } else if(splitStrs.length >= 2) {
            if(Utils.isEmpty(splitStrs[0])) {
                throw new HttpBladeException("No subtype found for: \"" + contentType + '"');
            }
            content.type = splitStrs[0];
            String parameter;
            for(int i = 1, len = splitStrs.length; i < len; i++) {
                parameter = splitStrs[i].trim();
                int equalIndex = parameter.indexOf('=');
                if(equalIndex > 0) {
                    String name = parameter.substring(0, equalIndex);
                    String value = parameter.substring(equalIndex + 1);
                    if("charset".equalsIgnoreCase(name)) {
                        charset = value;
                        break;
                    }
                }
            }
        }
        content.charset = charset;
        return content;
    }

    public String getMediaType() {
        return type;
    }

    public String getContentType() {
        return raw;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public String toString() {
        String result = type;
        if(Utils.isNotEmpty(charset)) {
            result += "; charset=" + charset;
        }
        return result;
    }

    public static String addCharset(String contentType, Charset charset) {
        if (contentType == null) {
            return "";
        }
        if (!contentType.contains("charset")) {
            return contentType + ";charset=" + charset.name();
        }
        return contentType;
    }

    public static String guessContentType(String body) {
        String contentType = null;
        if (Utils.isNotEmpty(body)) {
            char firstChar = body.charAt(0);
            switch (firstChar) {
                case '[':
                case '{':
                    contentType = JSON;
                    break;
                case '<':
                    contentType = XML;
                    break;
                default:
                    contentType = TEXT;
            }
        }
        return contentType;
    }

}
