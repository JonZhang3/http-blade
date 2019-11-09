package com.httpblade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.httpblade.base.Request;
import com.httpblade.base.Response;
import com.httpblade.common.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpBladeTest {

    private static int clientNum = 3;

    @BeforeClass
    public static void init() {
        HttpBlade.setJsonParserFactory(new JsonParserFactory() {

            private Gson gson = new Gson();

            @Override
            public <T> T fromJson(String json, Type type) {
                return gson.fromJson(json, type);
            }

            @Override
            public String toJson(Object obj) {
                return gson.toJson(obj);
            }
        });
    }

    @Test
    public void testGet() throws IOException {
        HttpBlade.use(HttpBlade.CLIENT_TYPE_OKHTTP);
        testGet$();

        HttpBlade.use(HttpBlade.CLIENT_TYPE_APACHE_HTTP);
        testGet$();

        HttpBlade.use(HttpBlade.CLIENT_TYPE_OKHTTP);
        testGet$();
    }

    private void testGet$() throws IOException {
        Request request = HttpBlade.createRequest()
            .get("http://localhost:8080/getTest")
            .form("name", "张三")
            .formEncoded("age", "10");
        Response response = HttpBlade.request(request);
        assertTrue(response.isOk());
        Reader reader = response.reader();
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024];
        int len = 0;
        while ((len = reader.read(buffer)) != -1) {
            sw.write(buffer, 0, len);
        }
        System.out.println(sw.toString());
        Map<String, List<String>> headers = response.allHeaders();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println(name + ":" + values);
        }
//        Map<String, String> map = response.json(new TypeToken<Map<String, String>>() {}.getType());
//        assertEquals("张三", map.get("name"));
//        assertEquals("10", map.get("age"));
//        assertNull(map.get("contentType"));
        response.close();
    }

    @Test
    public void testPost() {
        System.out.println(Utils.decode("%E5%BC%A0%E4%B8%89", "UTF-8"));
    }

}
