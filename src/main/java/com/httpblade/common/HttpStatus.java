package com.httpblade.common;

public final class HttpStatus {

    public static final int REDIRECT = 307;
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;

    public static boolean isOk(int status) {
        return status >= OK && status < 300;
    }

    public static boolean isBad(int status) {
        return status >= BAD_REQUEST;
    }

}
