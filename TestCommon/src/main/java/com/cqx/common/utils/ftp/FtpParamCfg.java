package com.cqx.common.utils.ftp;

/**
 * FtpParamCfg
 *
 * @author chenqixu
 */
public class FtpParamCfg {
    private String host;
    private Integer port;
    private String user;
    private String password;
    private boolean isSftp;

    public FtpParamCfg() {
    }

    public FtpParamCfg(String host, Integer port, String user, String password) {
        this(host, port, user, password, true);
    }

    public FtpParamCfg(String host, Integer port, String user, String password, boolean isSftp) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.isSftp = isSftp;
    }

    @Override
    public String toString() {
        return user + "@" + host + ":" + port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSftp() {
        return isSftp;
    }

    public void setSftp(boolean sftp) {
        isSftp = sftp;
    }
}
