package com.httpblade.apachehttp;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class SocketProxyConnectionFactory extends PlainConnectionSocketFactory {

    private Proxy proxy;
    private CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    public SocketProxyConnectionFactory(String proxyHost, int proxyPort) {
        this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {

        return new Socket(proxy);
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress, HttpContext context) throws IOException {
        InetSocketAddress unresolvedAddr = InetSocketAddress.createUnresolved(host.getHostName(), host.getPort());
        return super.connectSocket(connectTimeout, socket, host, unresolvedAddr, localAddress, context);
    }
}
