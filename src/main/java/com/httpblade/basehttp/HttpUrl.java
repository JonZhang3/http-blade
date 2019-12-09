package com.httpblade.basehttp;

import com.httpblade.HttpBladeException;
import com.httpblade.common.Queries;
import com.httpblade.common.Utils;

import java.net.MalformedURLException;
import java.net.URL;

class HttpUrl {

    private URL url;
    private String protocol;
    private String userInfo;
    private String host;
    private int port;
    private String hash;
    private String path;
    private Queries queries;

    HttpUrl(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        this.protocol = url.getProtocol().toLowerCase();
        this.userInfo = url.getUserInfo();
        this.host = url.getHost();
        this.port = url.getPort();
        if (this.port <= 0) {
            this.port = url.getDefaultPort();
        }
        this.hash = url.getRef();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.url.getPath();
    }

    public Queries getQueries() {
        if (queries == null) {
            queries = new Queries();
        }
        return queries;
    }

    public URL url() {
        if (path == null && queries == null) {
            return url;
        }
        StringBuilder result = new StringBuilder();
        result.append(protocol).append("://");
        if (Utils.isNotEmpty(userInfo)) {
            result.append(userInfo).append("@");
        }
        result.append(host);
        if (port != getDefaultPort(protocol)) {
            result.append(":").append(port);
        }
        if (path == null) {
            result.append(url.getPath());
        } else {
            result.append(path);
        }
        if (queries != null && queries.size() > 0) {
            result.append("?").append(queries.toString());
        } else {
            String queryStr = url.getQuery();
            if (Utils.isNotEmpty(queryStr)) {
                result.append("?").append(queryStr);
            }
        }
        if (Utils.isNotEmpty(hash)) {
            result.append("#").append(hash);
        }
        try {
            this.url = new URL(result.toString());
            this.path = null;
            this.queries = null;
            return url;
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
    }

    private static int getDefaultPort(String protocol) {
        if ("http".equals(protocol)) {
            return 80;
        } else if ("https".equals(protocol)) {
            return 443;
        }
        return -1;
    }

}
