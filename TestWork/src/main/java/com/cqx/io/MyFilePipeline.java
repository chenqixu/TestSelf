package com.cqx.io;

import java.io.*;

/**
 * MyFilePipeline
 * <pre>
 *     效果：从io里读取数据流，可以同时写到两个文件，基本不分先后
 *     输入InputStream
 *     输出BufferedReader，和备份文件
 * </pre>
 *
 * @author chenqixu
 */
public class MyFilePipeline {
    public static final int DEFAULT_BUFF_SIZE = 4 * 1024 * 1024; // 4M缓冲
    public static final String CHARSET = "UTF-8";

    /**
     * in只是一个指针，真正是靠输出的BufferedReader来拉动in来进行读取操作
     *
     * @param in
     * @param backFlieName
     * @return
     */
    public BufferedReader copyAndBack(InputStream in, String backFlieName) throws IOException {
        InputStream in1 = new BufferedInputStream(in);

        return new BufferedReader(new InputStreamReader(in1, CHARSET), DEFAULT_BUFF_SIZE);
    }
}
