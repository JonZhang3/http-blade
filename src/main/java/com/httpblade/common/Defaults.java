package com.httpblade.common;

public interface Defaults {

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

}
