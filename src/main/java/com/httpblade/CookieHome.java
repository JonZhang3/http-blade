package com.httpblade;

import java.net.URL;
import java.util.List;

/**
 * 为 Http Cookie 提供存储、持久化策略
 *
 * @author Jon
 * @since 1.0.0
 */
public interface CookieHome {

    /**
     * 保存从 Http 响应中获取到的 Cookie
     *
     * @param url     Http 连接
     * @param cookies 响应中的 Cookie （Set-Cookie）
     */
    void save(URL url, List<Cookie> cookies);

    /**
     * 从存储策略中获取 Cookie
     *
     * @param url Http 连接
     * @return 保存的响应中的 Cookie
     */
    List<Cookie> load(URL url);

}
