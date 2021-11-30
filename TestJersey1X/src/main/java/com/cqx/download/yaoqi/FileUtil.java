package com.cqx.download.yaoqi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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

    public void mkdir(String mkPath) {
        if (!com.cqx.common.utils.file.FileUtil.isExists(mkPath)) {
            com.cqx.common.utils.file.FileUtil.CreateDir(mkPath);
            logger.info("目录缺失，创建目录：{}", mkPath);
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
        saveOk(filePath + title + File.separator + "ok.txt");
    }

    public void saveMonthEnd() {
        saveOk(filePath + "ok.txt");
    }

    public void saveOk(String dir) {
        try {
            fileUtil.createFile(dir);
            fileUtil.write("ok");
            logger.info("【生成ok.txt】{}", dir);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } finally {
            fileUtil.closeWrite();
        }
    }

    public boolean isMonthDown() {
        String dir = filePath + "ok.txt";
        return com.cqx.common.utils.file.FileUtil.isExists(dir);
    }

    public boolean isBookDown() {
        String dir = filePath + title + File.separator + "ok.txt";
        return com.cqx.common.utils.file.FileUtil.isExists(dir);
    }

    public boolean isImgDown(String imgName) {
        return com.cqx.common.utils.file.FileUtil.isExists(imgName);
    }

    /**
     * 从硬盘上读取上次记录的next page
     *
     * @return
     */
    public String getDiskNextPage() {
        String dir = filePath + "nextpage.txt";
        if (com.cqx.common.utils.file.FileUtil.isExists(dir)) {
            List<String> contents = fileUtil.read(dir, "UTF-8");
            if (contents.size() > 0) {
                logger.info("从硬盘上读取上次记录的next page={}", contents.get(0));
                return contents.get(0);
            }
        }
        return null;
    }

    /**
     * 把下一页写入硬盘文件nextpage.txt
     *
     * @param nextPageUrl
     */
    public void saveNextPageToDisk(String nextPageUrl) {
        try {
            String dir = filePath + "nextpage.txt";
            fileUtil.createFile(dir);
            fileUtil.write(nextPageUrl);
            logger.info("把下一页{}写入硬盘文件nextpage.txt", nextPageUrl);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } finally {
            fileUtil.closeWrite();
        }
    }
}
