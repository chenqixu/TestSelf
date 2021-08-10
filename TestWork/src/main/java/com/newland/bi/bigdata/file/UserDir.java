package com.newland.bi.bigdata.file;

import java.io.File;

/**
 * UserDir，打印程序启动或jar包启动所在目录
 * <pre>
 *     cd D:\Document\Workspaces\Git\TestSelf\TestWork\target
 *     java -cp .;TestWork-1.0.0.jar com.newland.bi.bigdata.file.UserDir
 *     输出：
 *     D:\Document\Workspaces\Git\TestSelf\TestWork\target
 * </pre>
 *
 * @author chenqixu
 */
public class UserDir {

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        String userdir =
                System.getProperty("user.dir")
                        + File.separator
                        + "src"
                        + File.separator
                        + "main"
                        + File.separator
                        + "resources"
                        + File.separator;
        String prop = userdir + "configServer.properties";
        System.out.println(prop);
        System.out.println("prop file exists :" + new File(prop).exists());
    }
}
