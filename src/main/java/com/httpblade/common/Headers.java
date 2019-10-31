package com.httpblade.common;

import com.httpblade.HttpBlade;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author Jon
 * @since 1.0.0
 */
public class Headers {

    private Map<String, List<String>> headers = new HashMap<>();

    public Headers() {

    }

    public Headers(Map<String, List<String>> values) {
        this.headers.putAll(values);
    }

    public Headers add(String name, String value) {
        checkNameAndValue(name, value);
        List<String> list = headers.get(name);
        if (list == null) {
            list = new LinkedList<>();
        }
        list.add(value);
        return this;
    }

    public Headers set(String name, String value) {
        checkNameAndValue(name, value);
        List<String> list = new LinkedList<>();
        list.add(value);
        headers.put(name, list);
        return this;
    }

    public Headers remove(String name) {
        headers.remove(name);
        return this;
    }

    public String get(String name) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                List<String> values = entry.getValue();
                if (values != null && values.size() > 0) {
                    return values.get(0);
                }
            }
        }
        return null;
    }

    public List<String> getList(String name) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Map<String, List<String>> get() {
        return headers;
    }

    public boolean contain(String name) {
        return get(name) != null;
    }

    public void forEach(BiConsumer<String, List<String>> consumer) {
        headers.forEach(consumer);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        forEach((name, values) -> {
            for (String value : values) {
                sb.append(name).append(": ").append(value).append("\n");
            }
        });
        return sb.toString();
    }

    private static void checkNameAndValue(String name, String value) {
        if (HttpBlade.nowClientType() != HttpBlade.CLIENT_TYPE_OKHTTP) {
            checkName(name);
            checkValue(name, value);
        }
    }

    private static void checkName(String name) {
        if (Utils.isEmpty(name)) {
            throw new IllegalArgumentException("the name is null or empty");
        }
        for (int i = 0, len = name.length(); i < len; i++) {
            char ch = name.charAt(i);
            if (ch <= '\u0020' || ch >= '\u007f') {
                throw new IllegalArgumentException(String.format("Unexpected char %#04x at %d in header name: %s",
                    (int) ch, i, name));
            }
        }
    }

    private static void checkValue(String name, String value) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("value of the name [%s] is null", name));
        }
        for (int i = 0, len = value.length(); i < len; i++) {
            char ch = value.charAt(i);
            if ((ch <= '\u001f' && ch != '\t') || ch >= '\u007f') {
                throw new IllegalArgumentException(String.format("Unexpected char %#04x at %d in %s value: %s",
                    (int) ch, i, name, value));
            }
        }
    }

}
