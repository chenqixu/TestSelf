package com.cqx.download.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * SimpleHostnameVerifier
 *
 * @author chenqixu
 */
public class SimpleHostnameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
