package com.httpblade.common;

import org.junit.Test;
import static org.junit.Assert.*;

public class ContentTypeTest {

    @Test
    public void test() {
        ContentType contentType = ContentType.parse("application/json; charset=utf-8");
        assertNotNull(contentType);
        assertEquals("application/json", contentType.getMediaType());
        assertEquals("utf-8", contentType.getCharset());

        contentType = ContentType.parse("");
        assertNull(contentType);

        try {
            contentType = ContentType.parse(";charset=utf-8");
        } catch (Exception e) {
            assertNotNull(e);
        }

        contentType = ContentType.parse("application/json; charset=utf-8;a=A;b=B");
        assertNotNull(contentType);
        assertEquals("application/json", contentType.getMediaType());
        assertEquals("utf-8", contentType.getCharset());
    }

}
