package com.httpblade.common;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    CONNECT("CONNECT"),
    PATCH("PATCH");

    private String method;

    HttpMethod(String value) {
        this.method = value;
    }

    public String value() {
        return method;
    }

    public static boolean requiresRequestBody(HttpMethod method) {
        return method == PUT || method == POST || method == DELETE || method == PATCH;
    }

}
