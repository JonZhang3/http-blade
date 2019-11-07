package com.httpblade.common.api;

import com.httpblade.base.HttpClient;
import com.httpblade.base.Request;

import java.lang.reflect.Method;

public class ApiMethod {

    private Method method;
    private Request request;

    public ApiMethod(Method method) {
        this.method = method;
    }

    private void init() {

    }

    public Object execute(Object... args) {
        return null;
    }

}
