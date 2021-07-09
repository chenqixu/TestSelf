package com.cqx.common.utils.ftp;

/**
 * FtpTypeEnum
 *
 * @author chenqixu
 */
public enum FtpTypeEnum {
    FTP("FTP"),
    SFTP("SFTP"),
    ;

    private String type;

    FtpTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
