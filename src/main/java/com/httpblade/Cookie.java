package com.httpblade;

import com.httpblade.common.Utils;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author Jon
 * @since 1.0.0
 */
public class Cookie {

    private static final long MAX_DATE = 253402300799999L;

    protected String name;
    protected String value;
    protected long expiresAt;
    protected String domain;
    protected String path;
    protected boolean secure;

    protected Cookie(String name, String value, long expiresAt,
                     String domain, String path, boolean secure) {
        this.name = name;
        this.value = value;
        this.expiresAt = expiresAt;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    /**
     * 指定了该 Cookie 在何时会过期
     *
     * @return 存货的最大时间
     */
    public long expiresAt() {
        return expiresAt;
    }

    public boolean hasExpired() {
        return expiresAt <= System.currentTimeMillis();
    }

    public String domain() {
        return domain;
    }

    public String path() {
        return path;
    }

    public boolean secure() {
        return secure;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name())
            .append("=\"")
            .append(value()).append('"');
        if (path() != null) {
            sb.append(";$Path=\"").append(path()).append('"');
        }
        if (domain() != null) {
            sb.append(";$Domain=\"").append(domain()).append('"');
        }
        return sb.toString();
    }

    public static String join(List<Cookie> cookies) {
        StringBuilder result = new StringBuilder();
        if(cookies != null) {
            Cookie cookie;
            for(int i = 0, len = cookies.size(); i < len; i++) {
                cookie = cookies.get(i);
                result.append(cookie.name()).append("=").append(cookie.value());
                if(i < len - 1) {
                    result.append(";");
                }
            }
        }
        return result.toString();
    }

    public static String join(Cookie... cookies) {
        if(cookies == null) {
            return "";
        }
        return join(Arrays.asList(cookies));
    }

    public static Cookie parse(URL url, String cookieStr) {
        String[] strings = cookieStr.split(";");
        if (strings.length == 0) {
            return null;
        }
        String[] nameAndValue = strings[0].trim().split("=");
        if (nameAndValue.length <= 1) {
            return null;
        }
        String cookieName = nameAndValue[0];
        if (Utils.isEmpty(cookieName)) {
            return null;
        }
        String cookieValue = nameAndValue[1];

        long expiresAt = MAX_DATE;
        long maxAge = -1L;
        String domain = null;
        String path = null;
        boolean secure = false;

        for (int i = 1, len = strings.length; i < len; i++) {
            String str = strings[i];
            String name;
            String value = "";
            if (!str.isEmpty()) {
                String[] keyAndValue = str.split("=");
                if (keyAndValue.length == 1) {
                    name = keyAndValue[0].trim();
                } else if (keyAndValue.length == 2 && !keyAndValue[0].isEmpty()) {
                    name = keyAndValue[0].trim();
                    value = keyAndValue[1] != null ? keyAndValue[1].trim() : "";
                } else {
                    continue;
                }
                if (name.equalsIgnoreCase("expires")) {
                    expiresAt = parseExpires(value);
                } else if (name.equalsIgnoreCase("max-age")) {
                    maxAge = parseMaxAge(value);
                } else if (name.equalsIgnoreCase("domain")) {
                    domain = parseDomain(value);
                } else if (name.equalsIgnoreCase("path")) {
                    path = value;
                } else if (name.equalsIgnoreCase("secure")) {
                    secure = true;
                }
            }
        }
        // 如果指定了 Max-Age，则覆盖掉 expires
        if (maxAge == 0) {
            expiresAt = Long.MIN_VALUE;
        } else if (maxAge > 0) {
            long milliseconds = maxAge <= (Long.MAX_VALUE / 1000) ? maxAge * 1000 : Long.MAX_VALUE;
            expiresAt = System.currentTimeMillis() + milliseconds;
        }

        String currentHost = url.getHost();
        String protocol = url.getProtocol();
        if (domain == null) {
            domain = currentHost;
        } else if (!domainMatch(currentHost, domain)) {
            return null;
        }

        if (secure && !protocol.equalsIgnoreCase("https")) {
            return null;
        }
        if (path == null || !path.startsWith("/")) {
            path = url.getPath();
            if (Utils.isEmpty(path)) {
                path = "/";
            }
        }
        if(!pathMatch(url.getPath(), path)) {
            return null;
        }
        return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secure);
    }

    public static List<Cookie> parseAll(URL url, List<String> cookieStrings) {
        if(cookieStrings == null) {
            return Collections.emptyList();
        }
        List<Cookie> cookies = new LinkedList<>();
        for (String cookieString : cookieStrings) {
            Cookie cookie = parse(url, cookieString);
            if (cookie != null) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    public static boolean domainMatch(String urlHost, String domain) {
        if (urlHost.equals(domain)) {
            return true;
        }
        return urlHost.endsWith(domain) && urlHost.charAt(urlHost.length() - domain.length() - 1) == '.';
    }

    public static boolean pathMatch(String urlPath, String path) {
        if(Utils.isEmpty(urlPath)) {
            urlPath = "/";
        }
        if(urlPath.equals(path)) {
            return true;
        }
        if(urlPath.startsWith(path)) {
            if(path.endsWith("/")) {
                return true;
            }
            return urlPath.charAt(path.length()) == '/';
        }
        return false;
    }

    private static long parseMaxAge(String str) {
        try {
            long maxAge = Long.parseLong(str);
            return maxAge <= 0 ? 0 : maxAge;
        } catch (NumberFormatException e) {
            return Long.MIN_VALUE;
        }
    }

    private static long parseExpires(String str) {
        if (Utils.isEmpty(str)) {
            return 0L;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
            return sdf.parse(str).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    private static String parseDomain(String str) {
        if (str.endsWith(".")) {
            throw new IllegalArgumentException();
        }
        if (str.startsWith(".")) {
            str = str.substring(1);
        }
        return str;
    }

}
