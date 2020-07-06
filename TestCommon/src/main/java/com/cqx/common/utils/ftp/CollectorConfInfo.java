package com.cqx.common.utils.ftp;

/**
 * CollectorConfInfo
 *
 * @author chenqixu
 */
public class CollectorConfInfo {
    //扫描FTP是否循环递归子目录，默认不递归
    public static boolean ifRoundSubdirectory = false;
    //数据源文件是否使用正则表达式匹配，默认不匹配
    public static boolean ifUseRegex = false;
    //数据源文件正则表达式，默认*
    public static String dataSourceFileRegex = "*";
}
