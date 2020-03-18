package com.cqx.yaoqi;

import com.cqx.util.LogLEVEL;
import com.cqx.util.LogUtil;
import com.cqx.yaoqi.work.ThreadManagerment;

/**
 * 启动入口
 *
 * @author chenqixu
 */
public class AppMain {

    public static final String FILE_PATH = "e:\\Photo\\Comic\\爬虫\\image\\";

    public static void main(String[] args) {
        LogUtil.setLogLevel(LogLEVEL.INFO);
        new ThreadManagerment(true, 12, 7).exec();
    }

}
