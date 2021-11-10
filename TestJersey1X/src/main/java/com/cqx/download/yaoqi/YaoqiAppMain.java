package com.cqx.download.yaoqi;

import com.cqx.download.http.FileStreamDeal;
import com.cqx.download.yaoqi.work.ThreadManagerment;

/**
 * 启动入口
 *
 * @author chenqixu
 */
public class YaoqiAppMain {

    public static void main(String[] args) {
        String FILE_PATH = "e:\\Photo\\Comic\\爬虫\\image\\";
        FileStreamDeal.setFile_path(FILE_PATH);
        new ThreadManagerment(FILE_PATH, true, 12, 7).exec();
    }

}
