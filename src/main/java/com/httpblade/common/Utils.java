package com.httpblade.common;

import com.httpblade.HttpBladeException;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
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

    public static boolean isWhitespace(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\f' || ch == '\r' || ch == ' ';
    }

    public static String trimSubstring(String str, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            char ch = str.charAt(i);
            if (!isWhitespace(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static int indexOf(String str, int start, int end, char ch) {
        for (int i = start; i < end; i++) {
            if (str.charAt(i) == ch) {
                return i;
            }
        }
        return end;
    }

    public static String encode(final String value, final String charsetName) {
        if(value == null) {
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

    public static void close(Closeable... closeables) {
        if (closeables != null) {
            try {
                for (Closeable closeable : closeables) {
                    if (closeable != null) {
                        closeable.close();
                    }
                }
            } catch (IOException ignore) {
            }
        }
    }

    public static File writeToFile(String path, InputStream in) throws IOException {
        File file = new File(path);
        File parentFile = file.getParentFile();
        FileOutputStream fos = null;
        try {
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            return file;
        } catch (IOException e) {
            throw e;
        } finally {
            close(fos);
        }
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            close(baos, in);
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
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            copy(fis, out);
        } catch (FileNotFoundException e) {
            throw new HttpBladeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
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
        OutputStreamWriter osw;
        InputStreamReader isr = null;
        try {
            osw = charset == null ? new OutputStreamWriter(out) : new OutputStreamWriter(out, charset);
            if (content instanceof InputStream) {
                InputStream in = (InputStream) content;
                isr = charset == null ? new InputStreamReader(in) : new InputStreamReader(in, charset);
                int len;
                char[] buffer = new char[2 * 1024];
                while ((len = isr.read(buffer)) != -1) {
                    osw.write(buffer, 0, len);
                }
            } else if (content instanceof File) {
                File file = (File) content;
                FileInputStream fis = new FileInputStream(file);

            } else {
                String str = content.toString();
                osw.write(str);
            }
            osw.flush();
        } catch (IOException e) {
            throw new HttpBladeException(e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public static String basicAuthString(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes(StandardCharsets.UTF_8));
    }

}
