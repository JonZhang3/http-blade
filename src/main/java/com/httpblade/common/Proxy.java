package com.httpblade.common;

import com.httpblade.HttpBladeException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class Proxy {

    private final java.net.Proxy.Type type;
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public Proxy(String host, int port) {
        this(host, port, null, null);
    }

    public Proxy(String host, int port, String username, String password) {
        this(java.net.Proxy.Type.HTTP, host, port, username, password);
    }

    private Proxy(java.net.Proxy.Type type, String host, int port, String username, String password) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public java.net.Proxy.Type getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasAuth() {
        return username != null && password != null;
    }

    public static java.net.Proxy toJavaProxy(final Proxy proxy) {
        try {
            InetAddress inetAddress = InetAddress.getByName(proxy.getHost());
            return new java.net.Proxy(proxy.getType(), new InetSocketAddress(inetAddress, proxy.getPort()));
        } catch (UnknownHostException e) {
            throw new HttpBladeException(e);
        }
    }

    public static java.net.Proxy newJavaProxy(String host, int port) {
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(inetAddress, port));
        } catch (UnknownHostException e) {
            throw new HttpBladeException(e);
        }
    }

}
