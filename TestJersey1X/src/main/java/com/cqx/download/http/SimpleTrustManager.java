package com.cqx.download.http;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * SimpleTrustManager
 *
 * @author chenqixu
 */
public class SimpleTrustManager implements TrustManager,
        X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        return;
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        return;
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
