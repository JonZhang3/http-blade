package com.httpblade.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HeaderTest {

    @Test
    public void test() {
        Headers headers = new Headers();
        headers.set(HttpHeader.CONTENT_TYPE, ContentType.JSON);
        headers.add(HttpHeader.CONTENT_TYPE, ContentType.XML);
        headers.set(HttpHeader.USER_AGENT, Constants.USER_AGENT_STRING);
        assertHeaders(headers);

        Map<String, List<String>> map = new HashMap<>();
        map.put(HttpHeader.CONTENT_TYPE, new ArrayList<String>(){
            {
                add(ContentType.JSON);
                add(ContentType.XML);
            }
        });
        map.put(HttpHeader.USER_AGENT, new ArrayList<String>(){
            {
                add(Constants.USER_AGENT_STRING);
            }
        });
        headers = new Headers(map);
        assertHeaders(headers);
    }

    private void assertHeaders(Headers headers) {
        assertEquals(ContentType.JSON, headers.get(HttpHeader.CONTENT_TYPE));
        assertEquals(Constants.USER_AGENT_STRING, headers.get(HttpHeader.USER_AGENT));
        assertNull(headers.get(HttpHeader.CONTENT_ENCODING));
        List<String> values = headers.getList(HttpHeader.CONTENT_TYPE);
        assertEquals(2, values.size());
        assertEquals(ContentType.XML, values.get(1));
        assertTrue(headers.contain(HttpHeader.CONTENT_TYPE));

        headers.remove(HttpHeader.CONTENT_TYPE);
        assertFalse(headers.contain(HttpHeader.CONTENT_TYPE));
        assertEquals(0, headers.getList(HttpHeader.CONTENT_TYPE).size());
    }

}
