package com.cqx.common.utils.ftp;

/**
 * 代理
 *
 * @author chenqixu
 */
public class ProxyBean {
    private String proxyHost;
    private int proxyPort;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
