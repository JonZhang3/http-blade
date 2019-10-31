package com.httpblade.base;

import org.junit.Test;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class CookieTest {

    @Test
    public void test() throws MalformedURLException {
        URL urlHttps = new URL("https://www.baidu.com");
        String setCookie = "id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT; Secure";
        Cookie cookie = Cookie.parse(urlHttps, setCookie);
        assertNotNull(cookie);
        assertEquals(cookie.domain(), "www.baidu.com");
        assertEquals(cookie.path(), "/");
        assertEquals(cookie.name(), "id");
        assertEquals(cookie.value(), "a3fWa");
        assertTrue(cookie.secure());
        assertTrue(cookie.hasExpired());

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date now = new Date(System.currentTimeMillis() + 100 * 1000);

        // path 不一致
        setCookie = "id=a3fWa; Expires=" + sdf.format(now) + "; path=/test";
        cookie = Cookie.parse(urlHttps, setCookie);
        assertNull(cookie);// path 不一致

        // Secure 限制
        URL urlHttp = new URL("http://www.baidu.com");
        setCookie = "id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT; Secure";
        cookie = Cookie.parse(urlHttp, setCookie);
        assertNull(cookie);

        setCookie = "id=a3fWa; Expires=" + sdf.format(now) + "; path=/";
        cookie = Cookie.parse(urlHttps, setCookie);
        assertNotNull(cookie);
        assertEquals(now.getTime() / 1000 * 1000, cookie.expiresAt());
        assertFalse(cookie.hasExpired());

        String cookieStrs = "a=A;b=B;c=C";
        List<Cookie> cookies = Cookie.parseAll(urlHttps, new ArrayList<String>(){{
            add("a=A");
            add("b=B");
            add("c=C");
        }});
        assertEquals(cookieStrs, Cookie.join(cookies));
    }

}
