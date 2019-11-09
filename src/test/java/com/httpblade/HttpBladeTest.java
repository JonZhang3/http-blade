package com.httpblade;

import com.httpblade.base.Request;
import com.httpblade.base.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class HttpBladeTest {

    @Test
    public void test() {
        HttpBlade.use(HttpBlade.CLIENT_TYPE_OKHTTP);
        Request request = HttpBlade.createRequest()
            .get("http://localhost:8080/testGet")
            .form("name", "张三")
            .formEncoded("age", "10");
        Response response = HttpBlade.request(request);
        System.out.println(response.status());
        assertEquals(true, response.isOk());
    }

}
