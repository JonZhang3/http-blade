package com.httpblade.common;

import com.httpblade.HttpBladeException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

public final class SSLBuilder {

    /**
     * SSL 协议 v1.0 版本
     */
    public static final String PROTOCOL_SSL = "SSL";
    /**
     * SSL 协议 v2.0 版本
     */
    public static final String PROTOCOL_SSLV2 = "SSLv2";
    /**
     * SSL 协议 v3.0 版本
     */
    public static final String PROTOCOL_SSLV3 = "SSLv3";
    /**
     * TLS 协议 v1.0，即 SSL 协议 v3.1 版本，被广泛使用
     */
    public static final String PROTOCOL_TLS = "TLS";
    /**
     * TLS 协议 v1.1 版本，即 SSL 协议 v3.2 版本
     */
    public static final String PROTOCOL_TLSV11 = "TLSv1.1";
    /**
     * TLS 协议 v1.2 版本，即 SSL 协议 v3.3 版本
     */
    public static final String PROTOCOL_TLSV12 = "TLSv1.2";

    private String protocol = PROTOCOL_TLS;
    private Provider provider;
    private KeyManager keyManager;
    private X509TrustManager trustManager;
    private SecureRandom secureRandom = new SecureRandom();

    public SSLBuilder() {
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

    public SSLBuilder useProtocol(String protocol) {
        if (Utils.isNotEmpty(protocol)) {
            this.protocol = protocol;
        }
        return this;
    }

    public SSLBuilder setTrustManager(final X509TrustManager trustManager) {
        this.trustManager = trustManager;
        return this;
    }

    public SSLBuilder setTrustManager(final KeyStore truststore) {
        try {
            TrustManagerFactory managerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            managerFactory.init(truststore);
            TrustManager[] trustManagers = managerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new HttpBladeException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            this.trustManager = (X509TrustManager) trustManagers[0];
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new HttpBladeException(e);
        }
        return this;
    }

    public SSLBuilder setTrustManager(File file, char[] password) {
        if (file == null) {
            throw new HttpBladeException("the fils is null");
        }
        FileInputStream fis = null;
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new FileInputStream(file);
            keyStore.load(fis, password);
            setTrustManager(keyStore);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new HttpBladeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }
        return this;
    }

    public SSLBuilder setKeyManager(KeyManager keyManager) {
        this.keyManager = keyManager;
        return this;
    }

    public SSLBuilder setKeyManager(final KeyStore keyStore, char[] password) {
        try {
            final KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore, password);
            KeyManager[] keyManagers = factory.getKeyManagers();
            if(keyManagers.length > 0) {
                this.keyManager = keyManagers[0];
            }
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new HttpBladeException(e);
        }
        return this;
    }

    public SSLBuilder setKeyManager(File file, char[] storePassword, char[] keyPassword) {
        FileInputStream fis = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new FileInputStream(file);
            keyStore.load(fis, storePassword);
            setKeyManager(keyStore, keyPassword);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new HttpBladeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {

                }
            }
        }
        return this;
    }

    public SSLBuilder setSecureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public SSLContext build() {
        try {
            SSLContext context = SSLContext.getInstance(protocol);
            context.init(
                this.keyManager == null ? null : new KeyManager[] {this.keyManager},
                this.trustManager == null ? null : new TrustManager[] {this.trustManager},
                this.secureRandom
            );
            return context;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new HttpBladeException(e);
        }
    }

    public X509TrustManager getTrustManager() {
        return this.trustManager;
    }

}
