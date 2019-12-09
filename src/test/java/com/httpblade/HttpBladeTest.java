package com.httpblade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpBladeTest {

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
        //HttpBlade.use(HttpBlade.CLIENT_TYPE_OKHTTP);
        testGet$();

        //HttpBlade.use(HttpBlade.CLIENT_TYPE_APACHE_HTTP);
        testGet$();

        //HttpBlade.use(HttpBlade.CLIENT_TYPE_JDK);
        testGet$();
    }

    private void testGet$() throws IOException {
//        Request request = HttpBlade.createRequest()
//            .get("http://localhost:8080/getTest")
//            .form("name", "张三")
//            .formEncoded("age", "10");
//        Response response = HttpBlade.request(request);
//        assertTrue(response.isOk());
//        Map<String, String> map = response.json(new TypeToken<Map<String, String>>() {}.getType());
//        assertEquals("张三", map.get("name"));
//        assertEquals("10", map.get("age"));
//        assertNull(map.get("contentType"));
//        response.close();
    }

    @Test
    public void testPost() throws IOException {
        //HttpBlade.use(HttpBlade.CLIENT_TYPE_OKHTTP);
        testPost$();

        //HttpBlade.use(HttpBlade.CLIENT_TYPE_APACHE_HTTP);
        testPost$();

        //HttpBlade.use(HttpBlade.CLIENT_TYPE_JDK);
        testPost$();
    }

    private void testPost$() throws IOException {
//        Request request = HttpBlade.createRequest()
//            .post("http://localhost:8080/postTest")
//            .form("name", "张三")
//            .form("age", "10");
//        Response response = HttpBlade.request(request);
//        System.out.println(response.status());
//        System.out.println(response.string());
//        System.out.println("------------");
    }

    @Test
    public void testPostBody() {
        //HttpBlade.use(HttpBlade.CLIENT_TYPE_OKHTTP);
        testPostBody$();

        //HttpBlade.use(HttpBlade.CLIENT_TYPE_APACHE_HTTP);
        testPostBody$();

        //HttpBlade.use(HttpBlade.CLIENT_TYPE_JDK);
        testPostBody$();
    }

    private void testPostBody$() {
//        Map<String, String> bodyMap = new HashMap<>();
//        bodyMap.put("nickname", "李四");
//        Request request = HttpBlade.createRequest()
//            .post("http://localhost:8080/postBodyTest")
//            .form("name", "张三")
//            .form("age", "10")
//            .jsonBody(bodyMap);
//        Response response = HttpBlade.request(request);
//        System.out.println(response.status());
//        System.out.println(response.string());
//        System.out.println("------------");
    }

    @Test
    public void testPatch() {
//        HttpBlade.use(HttpBlade.CLIENT_TYPE_JDK);
//        Request request = HttpBlade.createRequest()
//            .patch("http://localhost:8080/patchTest")
//            .form("name", "张三");
//        Response response = HttpBlade.request(request);
//        System.out.println(response.status());
//        System.out.println(response.string());
    }

    @Test
    public void testCookie() {

    }

}
