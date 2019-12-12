package com.httpblade.apachehttp;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

class SocketProxySSLConnectionFactory extends SSLConnectionSocketFactory {

    private Proxy proxy;

    SocketProxySSLConnectionFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier, String proxyHost, int proxyPort) {
        super(sslContext, hostnameVerifier);
        this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
    }

    @Override
    public Socket createSocket(HttpContext context) {
        return new Socket(this.proxy);
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress, HttpContext context) throws IOException {
        return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
    }
}
