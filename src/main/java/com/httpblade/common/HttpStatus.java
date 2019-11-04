package com.httpblade.common;

public final class HttpStatus {

    public static final int OK = 200;
    public static final int MULT_CHOICE = 300;
    public static final int MOVED_PERM = 301;
    public static final int MOVED_TEMP = 302;
    public static final int SEE_OTHER = 303;
    public static final int TEMP_REDIRECT = 307;
    public static final int PERM_REDIRECT = 308;
    public static final int BAD_REQUEST = 400;

    public static boolean isOk(int status) {
        return status >= OK && status < 300;
    }

    public static boolean isBad(int status) {
        return status >= BAD_REQUEST;
    }

}
