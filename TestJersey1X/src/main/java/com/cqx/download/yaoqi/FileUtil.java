package com.cqx.download.yaoqi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * FileUtil
 *
 * @author chenqixu
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private int index = 1;
    private String title;
    private String filePath;
    private com.cqx.common.utils.file.FileUtil fileUtil = new com.cqx.common.utils.file.FileUtil();

    public FileUtil() {
    }

    public FileUtil(String filePath) {
        this.filePath = filePath;
    }

    public void increase() {
        index++;
    }

    public void reset() {
        index = 1;
    }

    public int getIndex() {
        return index;
    }

    public int getIndexAndIncrease() {
        int old = index;
        index++;
        return old;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSaveImgName() {
        String dir = filePath + title;
        return dir + File.separator + getIndexAndIncrease() + ".jpg";
    }

    public void mkdir() {
        String dir = filePath + title;
        if (!com.cqx.common.utils.file.FileUtil.isExists(dir)) {
            com.cqx.common.utils.file.FileUtil.CreateDir(dir);
            logger.info("创建目录：{}", dir);
        }
    }

    public void saveTitle(String content) {
        try {
            String dir = filePath + title + File.separator + "readme.txt";
            fileUtil.createFile(dir);
            fileUtil.write(content);
            logger.info("往文件{}写入{}。", dir, content);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } finally {
            fileUtil.closeWrite();
        }
    }

    public void saveEnd() {
        try {
            String dir = filePath + title + File.separator + "ok.txt";
            fileUtil.createFile(dir);
            fileUtil.write("ok");
            logger.info("【生成ok.txt】{}", title);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } finally {
            fileUtil.closeWrite();
        }
    }

    public boolean isBookDown() {
        String dir = filePath + title + File.separator + "ok.txt";
        return com.cqx.common.utils.file.FileUtil.isExists(dir);
    }

    public boolean isImgDown(String imgName) {
        return com.cqx.common.utils.file.FileUtil.isExists(imgName);
    }
}
