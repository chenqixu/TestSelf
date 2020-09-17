package com.cqx.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * Other tools
 *
 * @author chenqixu
 */
public class OtherUtil {
    private static final Logger logger = LoggerFactory.getLogger(OtherUtil.class);

    /**
     * 获取PID
     */
    public static String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }

    /**
     * 获取系统属性
     */
    public static String getSystemProperty(String args) {
        return System.getProperty(args);
    }

    /**
     * 获取字符集，默认返回GB2312
     */
    public static String getFileEncoding() {
        String fileencoding = OtherUtil.getSystemProperty("file.encoding");
        return fileencoding == null ? "GB2312" : fileencoding;
    }

    /**
     * 通过全路径判断是否是文件
     */
    public static boolean isFile(String path) {
        File file = new File(path);
        return file.isFile();
    }

    /**
     * 通过全路径判断是否是目录
     */
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    /**
     * 删除文件
     */
    public static boolean delFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (file.isDirectory()) {
            return flag;
        }
        if (file.isFile()) {
            flag = file.delete();
            logger.info("==步骤【删除工具】：成功删除文件【" + file.getPath() + "】");
        }
        return flag;
    }

    /**
     * 删除文件夹
     */
    public static boolean delDirectory(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (file.isFile()) {
            return flag;
        }
        if (file.isDirectory()) {
            flag = file.delete();
            logger.info("==步骤【删除工具】：成功删除目录【" + file.getPath() + "】");
        }
        return flag;
    }

    /**
     * 删除某个目录下所有文件及目录
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (file.isFile()) {
            flag = delFile(path);
        }
        if (file.isDirectory()) {
            String[] tempList = file.list();
            if (tempList != null) {
                // 删除文件
                for (int i = 0; i < tempList.length; i++) {
                    String temp = null;
                    if (path.endsWith(File.separator)) {
                        temp = path + tempList[i];
                    } else {
                        temp = path + File.separator + tempList[i];
                    }
                    flag = delAllFile(temp);
                }
            }
            // 删除目录
            flag = delDirectory(path);
        }
        return flag;
    }

    public static String reIfNull(String _str) {
        if (_str == null) {
            return "";
        }
        return _str;
    }

    public static boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }
}
