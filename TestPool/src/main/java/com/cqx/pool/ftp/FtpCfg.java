package com.cqx.pool.ftp;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author chenqixu
 * @description Ftp配置类
 * @date 2018/11/28 17:11
 */
public class FtpCfg implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String host;
    private Integer port;
    private String user;
    private String password;
    private boolean useSftp; // 是否使用sftp
    private boolean useActive = false; // 连接模式 主动/被动
    private boolean useBinary = true; // 是否用二进制传输 否则 ascii
    private int activeStartPort;// 主动模式起始端口
    private int activeEndPort;// 主动模式结束端口
    private int timeout;// ftp超时
    private String contorlCharset;// ftp字符集

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

    public boolean isUseSftp() {
        return useSftp;
    }

    public void setUseSftp(boolean useSftp) {
        this.useSftp = useSftp;
    }

    public boolean isUseActive() {
        return useActive;
    }

    public void setUseActive(boolean useActive) {
        this.useActive = useActive;
    }

    public boolean isUseBinary() {
        return useBinary;
    }

    public void setUseBinary(boolean useBinary) {
        this.useBinary = useBinary;
    }

    public int getActiveStartPort() {
        return activeStartPort;
    }

    public void setActiveStartPort(int activeStartPort) {
        this.activeStartPort = activeStartPort;
    }

    public int getActiveEndPort() {
        return activeEndPort;
    }

    public void setActiveEndPort(int activeEndPort) {
        this.activeEndPort = activeEndPort;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getContorlCharset() {
        return contorlCharset;
    }

    public void setContorlCharset(String contorlCharset) {
        this.contorlCharset = contorlCharset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj instanceof FtpCfg) {
                FtpCfg target = (FtpCfg) obj;
                if (target.host.equals(host)) {
                    if (target.port == port) {
                        if (target.user.equals(user)) {
                            if (target.useActive == useActive) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
