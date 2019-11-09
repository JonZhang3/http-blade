package com.httpblade.common;

import com.httpblade.HttpBladeException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Utils {

    private Utils() {
    }

    public static boolean isEmpty(String src) {
        return src == null || src.isEmpty();
    }

    public static boolean isNotEmpty(String src) {
        return !isEmpty(src);
    }

    public static String encode(final String value, final String charsetName) {
        if (value == null) {
            return null;
        }
        try {
            return URLEncoder.encode(value, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String decode(String value, String charsetName) {
        try {
            return URLDecoder.decode(value, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static File writeToFile(String path, InputStream in) throws IOException {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("error creat new dir");
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("error creat new file");
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            return file;
        }
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }

    public static String guessMediaType(final String filename) {
        if (isEmpty(filename)) {
            return ContentType.OCTET_STREAM;
        }
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String newFilename = filename.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(newFilename);
        if (isEmpty(contentType)) {
            contentType = ContentType.OCTET_STREAM;
        }
        return contentType;
    }

    public static <T> T getValue(T src, T defaultValue) {
        if (src == null) {
            return defaultValue;
        }
        return src;
    }

    public static String encodeBlank(String str) {
        if (isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0, len = str.length(); i < len; i++) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                sb.append("%20");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void copy(File file, OutputStream out) {
        try (FileInputStream fis = new FileInputStream(file)) {
            copy(fis, out);
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    public static void copy(InputStream in, OutputStream out) {
        try {
            int size;
            byte[] buffer = new byte[1024 * 2];
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            out.flush();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        }
    }

    public static void write(OutputStream out, Charset charset, Object content) {
        InputStream in = null;
        try {
            if (content instanceof InputStream) {
                in = (InputStream) content;
                copy(in, out);
            } else if (content instanceof File) {
                copy((File) content, out);
            } else {
                String str = content.toString();
                out.write(str.getBytes(charset));
            }
            out.flush();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                    // It shouldn't happen.
                }
            }
        }
    }

    public static String basicAuthString(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes(StandardCharsets.UTF_8));
    }

}
