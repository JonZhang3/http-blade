package com.httpblade;

import com.httpblade.base.Cookie;
import com.httpblade.base.CookieHome;

import java.net.URL;
import java.util.List;

public class DefaultCookieHome implements CookieHome {

    @Override
    public void save(URL url, List<Cookie> cookies) {

    }

    @Override
    public List<Cookie> load(URL url) {
        return null;
    }

}
