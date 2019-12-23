package com.cqx.oozie.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;

/**
 * 文件工具
 *
 * @author chenqixu
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static final char[] dataStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private Random random = new Random();

    /**
     * 文件是否以"/"结尾
     *
     * @param path
     * @return
     */
    public static String endWith(String path) {
        String result = path;
        String end = "/";
        if (result.endsWith(end)) {
            return result;
        } else {
            return result + end;
        }
    }

    /**
     * 根据长度获取随机字符串
     *
     * @param length
     * @return
     */
    public String getRandomStr(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += dataStr[random.nextInt(52)];
        }
        return result;
    }

    /**
     * 创建文件并写入随机内容
     *
     * @param file_name
     * @param file_content_size
     * @param file_content_length
     */
    public void createRandomFile(String file_name, int file_content_size, int file_content_length) {
        File file = new File(file_name);
        BufferedWriter writer = null;
        String write_code = "UTF-8";
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), write_code));
            for (int i = 0; i < file_content_size; i++) {
                writer.write(getRandomStr(file_content_length));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
