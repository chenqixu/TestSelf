package com.cqx.common.utils.sftp;

/**
 * SftpFileFilter
 *
 * @author chenqixu
 */
public class SftpFileFilter {
    //数据源文件是否使用正则表达式匹配,只用于只采集数据文件的情况
    private boolean ifUseRegex = false;
    //数据源文件正则表达式,只用于只采集数据文件的情况
    private String dataSourceFileRegex;
    //文件包含关键字
//    private String fileNameInclude;

    public SftpFileFilter() {
    }

    public SftpFileFilter(String dataSourceFileRegex) {
        this.ifUseRegex = true;
        this.dataSourceFileRegex = dataSourceFileRegex;
    }

    public boolean isIfUseRegex() {
        return ifUseRegex;
    }

    public void setIfUseRegex(boolean ifUseRegex) {
        this.ifUseRegex = ifUseRegex;
    }

    public String getDataSourceFileRegex() {
        return dataSourceFileRegex;
    }

    public void setDataSourceFileRegex(String dataSourceFileRegex) {
        this.dataSourceFileRegex = dataSourceFileRegex;
    }
}
