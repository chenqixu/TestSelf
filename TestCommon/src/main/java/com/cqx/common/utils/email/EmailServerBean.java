package com.cqx.common.utils.email;

/**
 * EmailServerBean
 *
 * @author chenqixu
 */
public class EmailServerBean {
    private String serverHost;
    private String smtpPort;
    private String protocol;

    public EmailServerBean() {
    }

    public EmailServerBean(String serverHost, String smtpPort, String protocol) {
        this.serverHost = serverHost;
        this.smtpPort = smtpPort;
        this.protocol = protocol;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
