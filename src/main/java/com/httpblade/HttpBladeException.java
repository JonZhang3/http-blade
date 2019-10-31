package com.httpblade;

public class HttpBladeException extends RuntimeException {

    public HttpBladeException() {
    }

    public HttpBladeException(String message) {
        super(message);
    }

    public HttpBladeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpBladeException(Throwable cause) {
        super(cause);
    }
}
