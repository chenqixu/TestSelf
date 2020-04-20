package com.cqx.etlcollect.bean;

/**
 * FTP文件
 *
 * @author chenqixu
 */
public class FileBean {
    private String fileName;
    private String ftpHost;
    private String filePath;
    private int fileSize;
    private String fileDate;

    @Override
    public String toString() {
        return "ftpHost：" + ftpHost + "，fileName：" + fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFtpHost() {
        return ftpHost;
    }

    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
}
