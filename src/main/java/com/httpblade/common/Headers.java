package com.httpblade.common;

import com.httpblade.HttpBlade;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Jon
 * @since 1.0.0
 */
public final class Headers {

    private Map<String, List<String>> headers = new HashMap<>();

    public Headers() {
    }

    public Headers(Map<String, List<String>> values) {
        if (values != null) {
            values.forEach((name, values1) -> {
                if (name != null) {
                    headers.put(name.toLowerCase(Locale.US), values1);
                }
            });
        }
    }

    public Headers add(String name, String value) {
        checkNameAndValue(name, value);
        String lowerCaseName = name.toLowerCase(Locale.US);
        List<String> list = headers.computeIfAbsent(lowerCaseName, k -> new LinkedList<>());
        list.add(value);
        return this;
    }

    public Headers set(String name, String value) {
        checkNameAndValue(name, value);
        List<String> list = new LinkedList<>();
        list.add(value);
        String lowerCaseName = name.toLowerCase(Locale.US);
        headers.put(lowerCaseName, list);
        return this;
    }

    public Headers remove(String name) {
        String lowerCaseName = name.toLowerCase(Locale.US);
        headers.remove(lowerCaseName);
        return this;
    }

    public String get(String name) {
        String lowerCaseName = name.toLowerCase(Locale.US);
        List<String> values = headers.get(lowerCaseName);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public List<String> getList(String name) {
        String lowerCaseName = name.toLowerCase(Locale.US);
        List<String> values = headers.get(lowerCaseName);
        return values != null ? values : Collections.emptyList();
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

    public Headers merge(Headers other) {
        final Headers that = this;
        if (other != null) {
            other.forEach((name, values) -> {
                if (!that.contain(name)) {
                    that.addAll(name, values);
                }
            });
        }
        return this;
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

    private void addAll(String name, List<String> values) {
        headers.put(name, values);
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
