package com.cqx.distributed.bean;

import com.newland.bd.model.cfg.FtpCfg;

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

    public FtpParamCfg(FtpCfg ftpCfg) {
        host = ftpCfg.getHost();
        port = ftpCfg.getPort();
        user = ftpCfg.getUser();
        password = ftpCfg.getPassword();
        isSftp = ftpCfg.isUseSftp();
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
