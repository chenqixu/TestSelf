package com.cqx.common.utils.http.auth;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 主机名验证程序
 *
 * @author chenqixu
 */
public class MyHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if ("127.0.0.1".equals(hostname) || "localhost".equals(hostname)) {
            return true;
        } else {
            return false;
        }
    }
}
