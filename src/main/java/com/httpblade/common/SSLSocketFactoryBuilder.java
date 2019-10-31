package com.httpblade.common;

import com.httpblade.HttpBladeException;

import javax.net.ssl.*;
import java.security.*;
import java.util.Arrays;

public final class SSLSocketFactoryBuilder {

    public static final String SSL = "SSL";
    public static final String SSLv2 = "SSLv2";
    public static final String SSLv3 = "SSLv3";

    public static final String TLS = "TLS";
    public static final String TLSv1 = "TLSv1";
    public static final String TLSv11 = "TLSv1.1";
    public static final String TLSv12 = "TLSv1.2";

    private String protocol = TLS;
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
        try {
            SSLContext context = SSLContext.getInstance(protocol);
            context.init(new KeyManager[]{this.keyManager},
                new TrustManager[]{this.trustManager}, this.secureRandom);
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new HttpBladeException(e);
        }
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }

}
