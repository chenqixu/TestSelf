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
    private boolean is_ssl;

    public EmailServerBean() {
    }

    public EmailServerBean(String serverHost, String smtpPort, String protocol) {
        this(serverHost, smtpPort, protocol, true);
    }

    public EmailServerBean(String serverHost, String smtpPort, String protocol, boolean is_ssl) {
        this.serverHost = serverHost;
        this.smtpPort = smtpPort;
        this.protocol = protocol;
        this.is_ssl = is_ssl;
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

    public boolean isIs_ssl() {
        return is_ssl;
    }

    public void setIs_ssl(boolean is_ssl) {
        this.is_ssl = is_ssl;
    }
}
