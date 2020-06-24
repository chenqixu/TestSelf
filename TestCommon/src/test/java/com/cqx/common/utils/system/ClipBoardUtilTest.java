package com.cqx.common.utils.system;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClipBoardUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(ClipBoardUtilTest.class);

    @Test
    public void getSysClipBoardText() throws IOException, UnsupportedFlavorException {
        logger.info("get text，{}", ClipBoardUtil.getSysClipBoardText());
        logger.info("get file list，{}", ClipBoardUtil.getSysClipBoardFileList());
        String setText = "123";
        logger.info("set，{}", setText);
        ClipBoardUtil.setSysClipBoardText(setText);
        logger.info("get text，{}", ClipBoardUtil.getSysClipBoardText());
        List<File> fileList = new ArrayList<>();
        fileList.add(new File("d:\\Work\\ETL\\天空\\组件开发\\批-FTP采集到HDFS\\"));
        logger.info("set，{}", fileList);
        ClipBoardUtil.setSysClipBoardFileList(fileList);
        logger.info("get file list，{}", ClipBoardUtil.getSysClipBoardFileList());
    }
}