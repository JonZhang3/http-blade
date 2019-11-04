package com.httpblade.common;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class GlobalProxyAuth extends Authenticator {

    private final String username;
    private final String password;

    public GlobalProxyAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
