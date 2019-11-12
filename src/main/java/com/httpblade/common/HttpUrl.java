package com.httpblade.common;

import com.httpblade.HttpBladeException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class HttpUrl {

    private String rawUrl;
    private String newUrl;
    private String protocol;
    private String username;
    private String password;
    private String host;
    private int port;
    private String path;
    private String hash;
    private String queryString;
    private Map<String, List<String>> queries = new LinkedHashMap<>();

    private boolean queryChanged = false;
    private boolean hasChanged = false;

    public HttpUrl(String url) {
        this.newUrl = this.rawUrl = url;
        try {
            init(new URL(url));
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
    }

    public HttpUrl(URL url) {
        if (url == null) {
            throw new NullPointerException("the parameter is null");
        }
        this.newUrl = this.rawUrl = url.toString();
        init(url);
    }

    public HttpUrl(okhttp3.HttpUrl httpUrl) {
        if (httpUrl == null) {
            throw new NullPointerException("the parameter is null");
        }
        this.newUrl = this.rawUrl = httpUrl.toString();
    }

    private void init(URL url) {
        this.protocol = url.getProtocol().toLowerCase();
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            String[] userInfoSplit = userInfo.split(":");
            if (userInfoSplit.length == 1) {
                this.username = userInfoSplit[0];
            } else if (userInfoSplit.length == 2) {
                this.username = userInfoSplit[0];
                this.password = userInfoSplit[1];
            }
        }
        this.host = url.getHost();
        this.port = url.getPort();
        if (this.port <= 0) {
            this.port = url.getDefaultPort();
        }
        this.path = url.getPath();
        this.hash = url.getRef();
        this.queryString = url.getQuery();
        if (this.queryString == null) {
            this.queryString = "";
        }
        parseQueryString(this.queryString, this.queries);
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getHash() {
        return hash;
    }

    public int getPort() {
        return port;
    }

    public String getQueryString() {
        if (queryChanged) {
            StringBuilder result = new StringBuilder();
            joinQueries(queries, result);
            queryString = result.toString();
            queryChanged = false;
        }
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
        this.queries.clear();
        parseQueryString(queryString, queries);
        this.hasChanged = true;
    }

    public String getQuery(String name) {
        return this.queries.get(name).get(0);
    }

    public List<String> getQueryList(String name) {
        return this.queries.get(name);
    }

    public void setQuery(String name, String value) {
        List<String> values = new LinkedList<>();
        values.add(value);
        this.queries.put(name, values);
        this.queryChanged = true;
        this.hasChanged = true;
    }

    public void addQuery(String name, String value) {
        List<String> values = this.queries.get(name);
        if (values == null) {
            values = new LinkedList<>();
        }
        values.add(value);
        this.queries.put(name, values);
        this.queryChanged = true;
        this.hasChanged = true;
    }

    public void setPathVariable(String name, String value) {
        if (name == null || value == null) {
            return;
        }
        this.path = path.replaceAll("\\{ + name + \\}", Utils.encode(value, "UTF-8"));
        this.hasChanged = true;
    }

    public void setProtocol(String protocol) {
        String pro = protocol.toLowerCase();
        if (!"http".equals(pro) && !"https".equals(pro)) {
            throw new HttpBladeException("the protocol must http or https");
        }
        this.protocol = pro;
        this.hasChanged = true;
    }

    public void setProtocolAndResetPort(String protocol) {
        setProtocol(protocol);
        setPort(getDefaultPort(protocol, this.port));
    }

    public void setUsername(String username) {
        this.username = username;
        this.hasChanged = true;
    }

    public void setPassword(String password) {
        this.password = password;
        this.hasChanged = true;
    }

    public void setHost(String host) {
        this.host = host;
        this.hasChanged = true;
    }

    public void setPath(String path) {
        this.path = path;
        this.hasChanged = true;
    }

    public void setPort(int port) {
        if (port <= 0 || port > 65536) {
            throw new IllegalArgumentException("the port must be between 0 and 65536");
        }
        this.port = port;
        this.hasChanged = true;
    }

    public void setHash(String hash) {
        this.hash = hash;
        this.hasChanged = true;
    }

    public String raw() {
        return rawUrl;
    }

    public URL toURL() {
        try {
            return new URL(toString());
        } catch (MalformedURLException e) {
            throw new HttpBladeException(e);
        }
    }

    public okhttp3.HttpUrl toOkHttpUrl() {
        okhttp3.HttpUrl.Builder builder = new okhttp3.HttpUrl.Builder()
            .scheme(protocol)
            .username(username)
            .password(password)
            .host(host)
            .addPathSegment(path);
        if (queries.size() > 0) {
            for (Map.Entry<String, List<String>> entry : queries.entrySet()) {
                String name = entry.getKey();
                List<String> values = entry.getValue();
                for (String value : values) {
                    builder.addQueryParameter(name, value);
                }
            }
        }
        builder.fragment(hash);
        return builder.build();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HttpUrl)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        if (!hasChanged) {
            return newUrl;
        }
        StringBuilder result = new StringBuilder();
        if (protocol != null) {
            result.append(protocol).append("://");
        } else {
            result.append("//");
        }

        if (Utils.isNotEmpty(username)) {
            result.append(username);
            if (Utils.isNotEmpty(password)) {
                result.append(":").append(password);
            }
            result.append("@");
        }

        if (host != null) {
            if (host.indexOf(':') >= 0) {
                result.append("[").append(host).append("]");
            } else {
                result.append(host);
            }
        }

        if (port != getDefaultPort(protocol)) {
            result.append(":").append(port);
        }

        if (Utils.isNotEmpty(path)) {
            if (!path.startsWith("/")) {
                result.append("/");
            }
            result.append(path);
        }

        if (queries.size() > 0) {
            result.append('?');
            joinQueries(queries, result);
        }

        if (hash != null) {
            result.append('#').append(hash);
        }
        newUrl = result.toString();
        hasChanged = false;
        return newUrl;
    }

    private static void parseQueryString(final String queryString, final Map<String, List<String>> queries) {
        if (Utils.isNotEmpty(queryString)) {
            String[] nameAndValues = queryString.split("&");
            for (String nameAndValue : nameAndValues) {
                int equalIndex = nameAndValue.indexOf('=');
                String name;
                String value;
                if (equalIndex < 0) {
                    name = nameAndValue;
                    value = null;
                } else {
                    name = nameAndValue.substring(0, equalIndex);
                    value = nameAndValue.substring(equalIndex + 1);
                }
                List<String> values = queries.get(name);
                if (values == null) {
                    values = new LinkedList<>();
                }
                values.add(value);
                queries.put(name, values);
            }
        }
    }

    private static int getDefaultPort(String protocol) {
        return getDefaultPort(protocol, -1);
    }

    private static int getDefaultPort(String protocol, int defaultPort) {
        if ("http".equals(protocol)) {
            return 80;
        } else if ("https".equals(protocol)) {
            return 443;
        }
        return defaultPort;
    }

    private static void joinQueries(final Map<String, List<String>> queries, final StringBuilder out) {
        int index = 0;
        for (Map.Entry<String, List<String>> entry : queries.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                if (index > 0) {
                    out.append("&");
                }
                out.append(Utils.encode(name, "UTF-8"));
                if (value != null) {
                    out.append('=').append(Utils.encode(value, "UTF-8"));
                }
                index++;
            }
        }
    }

}
