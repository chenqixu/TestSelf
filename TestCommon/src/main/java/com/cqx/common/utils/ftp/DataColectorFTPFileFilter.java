package com.cqx.common.utils.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * FTP文件过滤器
 *
 * @author chenqixu
 */
public class DataColectorFTPFileFilter implements FTPFileFilter {

    private static final Logger logger = LoggerFactory.getLogger(DataColectorFTPFileFilter.class);

    @Override
    public boolean accept(FTPFile ftpfile) {
        boolean flag = true;
        try {
            //如果是文件夹
            if (ftpfile.isDirectory()) {
                if (CollectorConfInfo.ifRoundSubdirectory) {
                    //需要递归子目录
                    return true;
                }
            }

            //源端文件名
            String fileName = ftpfile.getName();
            //是否需要正则匹配
            if (CollectorConfInfo.ifUseRegex) {
                if (!Pattern.matches(CollectorConfInfo.dataSourceFileRegex, fileName))
                    return false;
            }
        } catch (Exception e) {
            flag = false;
            logger.info("DataColectorFileFilterException：%%%%采集程序过滤文件时发生异常!!!" + e.getMessage(), e);
        }
        return flag;
    }
}
