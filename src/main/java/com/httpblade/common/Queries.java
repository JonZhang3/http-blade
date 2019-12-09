package com.httpblade.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Queries {

    private Map<String, List<String>> queries = new HashMap<>();

    public void set(String name, String value) {
        List<String> values = new LinkedList<>();
        values.add(value);
        queries.put(name, values);
    }

    public void add(String name, String value) {
        List<String> values = queries.get(name);
        if(values == null) {
            values = new LinkedList<>();
        }
        values.add(value);
        queries.put(name, values);
    }

    public int size() {
        return queries.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, List<String>> entry : queries.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                if (index > 0) {
                    result.append("&");
                }
                result.append(name);
                if (value != null) {
                    result.append('=').append(value);
                }
                index++;
            }
        }
        return result.toString();
    }
}
