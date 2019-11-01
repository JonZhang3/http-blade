package com.httpblade.common;

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

    public Proxy(java.net.Proxy.Type type, String host, int port, String username, String password) {
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

}
