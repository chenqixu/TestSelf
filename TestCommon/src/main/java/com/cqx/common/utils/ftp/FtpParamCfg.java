package com.cqx.common.utils.ftp;

import org.apache.commons.net.ftp.FTP;

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
    private int fileType = FTP.BINARY_FILE_TYPE;// 文件传输方式，默认二进制
    private boolean isPassiveMode = true;// 是否被动模式，默认是

    public FtpParamCfg() {
    }

    public FtpParamCfg(FtpBean ftpBean) {
        this(ftpBean.getHost(), ftpBean.getPort(), ftpBean.getUser_name(), ftpBean.getPass_word()
                , FtpTypeEnum.SFTP.equals(ftpBean.getType()));
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

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public boolean isPassiveMode() {
        return isPassiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        isPassiveMode = passiveMode;
    }
}
