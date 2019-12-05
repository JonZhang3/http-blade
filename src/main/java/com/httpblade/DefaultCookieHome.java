package com.httpblade;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCookieHome implements CookieHome {

    private Map<String, List<Cookie>> cookies = new ConcurrentHashMap<>();

    @Override
    public void save(URL url, List<Cookie> cookies) {
        String host = url.getHost();
        List<Cookie> result = new LinkedList<>();
        for (Cookie cookie : cookies) {
            if(!cookie.hasExpired()) {
                result.add(cookie);
            }
        }
        this.cookies.put(host, result);
    }

    @Override
    public List<Cookie> load(URL url) {
        return null;
    }

}
