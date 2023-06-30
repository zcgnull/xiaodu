package com.dataport.wellness.http;

import static okhttp3.internal.Util.assertionError;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;

public class SSLSocketClient {

    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        return newSslSocketFactory(platformTrustManager());
    }


    //获取HostnameVerifier
    public static Boolean hostnameVerifier(String hostname, SSLSession session) {
       return OkHostnameVerifier.INSTANCE.verify(hostname,session);
    }
    public static X509TrustManager platformTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw assertionError("No System TLS", e); // The system has no TLS. Just give up.
        }
    }

    private static SSLSocketFactory newSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = Platform.get().getSSLContext();
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw assertionError("No System TLS", e); // The system has no TLS. Just give up.
        }
    }
}