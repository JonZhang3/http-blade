package com.httpblade.common;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpUrlTest {

    @Test
    public void test() {
        String urlStr = "https://username:password@www.baidu.com/test?a=A&b=B&c#hash";
        HttpUrl httpUrl = new HttpUrl(urlStr);

        assertEquals("https", httpUrl.getProtocol());
        assertEquals("username", httpUrl.getUsername());
        assertEquals("password", httpUrl.getPassword());
        assertEquals("www.baidu.com", httpUrl.getHost());
        assertEquals(443, httpUrl.getPort());
        assertEquals("/test", httpUrl.getPath());
        assertEquals("a=A&b=B&c", httpUrl.getQueryString());
        assertEquals("A", httpUrl.getQuery("a"));
        assertEquals("B", httpUrl.getQuery("b"));
        assertNull(httpUrl.getQuery("c"));
        assertEquals("hash", httpUrl.getHash());
        assertEquals(urlStr, httpUrl.toString());

        urlStr = "http://[1080:0:0:0:8:800:200C:417A]/index.html";
        httpUrl = new HttpUrl(urlStr);
        assertEquals("[1080:0:0:0:8:800:200C:417A]", httpUrl.getHost());
        assertEquals("/index.html", httpUrl.getPath());
    }

}
