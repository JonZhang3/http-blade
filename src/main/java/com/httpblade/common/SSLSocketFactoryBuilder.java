package com.httpblade.common;

import com.httpblade.HttpBladeException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public final class SSLSocketFactoryBuilder {

    /** SSL 协议 v1.0 版本 */
    public static final String PROTOCOL_SSL = "SSL";
    /** SSL 协议 v2.0 版本 */
    public static final String PROTOCOL_SSLv2 = "SSLv2";
    /** SSL 协议 v3.0 版本 */
    public static final String PROTOCOL_SSLv3 = "SSLv3";
    /** TLS 协议 v1.0，即 SSL 协议 v3.1 版本，被广泛使用 */
    public static final String PROTOCOL_TLS = "TLS";
    /** TLS 协议 v1.1 版本，即 SSL 协议 v3.2 版本 */
    public static final String PROTOCOL_TLSv11 = "TLSv1.1";
    /** TLS 协议 v1.2 版本，即 SSL 协议 v3.3 版本 */
    public static final String PROTOCOL_TLSv12 = "TLSv1.2";

    public static final String KEY_STORE_TYPE_JKS = "JKS";
    public static final String KEY_STORE_TYPE_PKCS8 = "PKCS8";
    public static final String KEY_STORE_TYPE_PKCS12 = "PKCS12";

    private String protocol = PROTOCOL_TLS;
    private KeyManager keyManager;
    private X509TrustManager trustManager;
    private SecureRandom secureRandom = new SecureRandom();

    public SSLSocketFactoryBuilder() {
        try {
            TrustManagerFactory managerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            managerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = managerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            this.trustManager = (X509TrustManager) trustManagers[0];
        } catch (NoSuchAlgorithmException | KeyStoreException ignore) {
        }
    }

    public SSLSocketFactoryBuilder useProtocol(String protocol) {
        if (Utils.isNotEmpty(protocol)) {
            this.protocol = protocol;
        }
        return this;
    }

    public SSLSocketFactoryBuilder setKeyManager(KeyManager keyManager) {
        this.keyManager = keyManager;
        return this;
    }

    public SSLSocketFactoryBuilder setTrustManager(X509TrustManager trustManager) {
        if (trustManager != null) {
            this.trustManager = trustManager;
        }
        return this;
    }

    public SSLSocketFactoryBuilder setSecureRandom(SecureRandom secureRandom) {
        if (secureRandom != null) {
            this.secureRandom = secureRandom;
        }
        return this;
    }

    public SSLSocketFactory build() {
        return buildContext().getSocketFactory();
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }

    public SSLContext buildContext() {
        try {
            SSLContext context = SSLContext.getInstance(protocol);
            context.init(new KeyManager[]{ this.keyManager }, new TrustManager[]{ this.trustManager }, this.secureRandom);
            return context;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new HttpBladeException(e);
        }
    }

}
