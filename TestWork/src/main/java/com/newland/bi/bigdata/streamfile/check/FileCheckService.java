package com.newland.bi.bigdata.streamfile.check;

import com.newland.bi.bigdata.bean.CheckFile;

import java.util.List;

/**
 * 校验文件缓存服务
 *
 * @author chenqixu
 */
public interface FileCheckService {
    /**
     * 校验文件入缓存
     *
     * @param mergerFileName
     * @param sourceFileName
     * @param checkFile
     */
    void putCache(String mergerFileName, String sourceFileName, CheckFile checkFile);

    /**
     * 从缓存读取校验文件清单
     *
     * @param mergerFileName
     * @return
     */
    List<String> getCacheList(String mergerFileName);

    /**
     * 从缓存读取校验文件汇总大小
     *
     * @param mergerFileName
     * @return
     */
    long getSumCache(String mergerFileName);

    /**
     * 从缓存移除校验文件缓存
     *
     * @param sourceFileName
     */
    void removeCache(String sourceFileName);
}