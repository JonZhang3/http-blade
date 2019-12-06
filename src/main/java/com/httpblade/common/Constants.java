package com.httpblade.common;

import org.apache.http.client.methods.HttpRequestBase;

public interface Constants {

    /**
     * 默认的连接超时时间
     */
    int CONNECT_TIMEOUT = 10 * 1000;

    /**
     * 默认的读超时时间
     */
    int READ_TIMEOUT = 10 * 1000;

    /**
     * 默认的写超时时间
     */
    int WRITE_TIMEOUT = 10 * 1000;

    /**
     * 默认的允许最大重定向次数
     */
    int MAX_REDIRECT_COUNT = 1;

    /**
     * 默认 User-Agent 请求头的值
     */
    String USER_AGENT_STRING = "http-blade/1.0.0";

    String KEY_COMMON = "COMMON";

    static void setDefaultHeaders(Headers headers) {
        headers.set(HttpHeader.USER_AGENT, USER_AGENT_STRING);
        headers.set(HttpHeader.ACCEPT, "*/*");
        headers.set(HttpHeader.ACCEPT_ENCODING, "gzip,deflate");
    }

    static void setDefaultHeaders(HttpRequestBase request) {
        request.setHeader(HttpHeader.USER_AGENT, USER_AGENT_STRING);
        request.setHeader(HttpHeader.ACCEPT, "*/*");
        request.setHeader(HttpHeader.ACCEPT_ENCODING, "gzip,deflate");
    }

}
