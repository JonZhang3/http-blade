package com.httpblade.okhttp;

import okhttp3.Cookie;

class OkHttpCookieImpl extends com.httpblade.base.Cookie {

    OkHttpCookieImpl(Cookie cookie) {
        super(cookie.name(), cookie.value(), cookie.expiresAt(), cookie.domain(),
            cookie.path(), cookie.secure());
    }
}
