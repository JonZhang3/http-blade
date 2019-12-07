package com.httpblade.okhttp;

import com.httpblade.CookieHome;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.LinkedList;
import java.util.List;

class DefaultCookieJar implements CookieJar {

    private final CookieHome cookieHome;

    DefaultCookieJar(CookieHome cookieHome) {
        this.cookieHome = cookieHome;
    }

    CookieHome getCookieHome() {
        return cookieHome;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        List<com.httpblade.Cookie> baseCookies = new LinkedList<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                baseCookies.add(new OkHttpCookieImpl(cookie));
            }
        }
        cookieHome.save(url.url(), baseCookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<com.httpblade.Cookie> baseCookies = cookieHome.load(url.url());
        List<Cookie> cookies = new LinkedList<>();
        if (baseCookies != null) {
            for (com.httpblade.Cookie baseCookie : baseCookies) {
                Cookie.Builder builder =
                    new Cookie.Builder()
                        .name(baseCookie.name())
                        .value(baseCookie.value())
                        .expiresAt(baseCookie.expiresAt())
                        .domain(baseCookie.domain())
                        .path(baseCookie.path());
                if (baseCookie.secure()) {
                    builder.secure();
                }
                cookies.add(builder.build());
            }
        }
        return cookies;
    }
}
