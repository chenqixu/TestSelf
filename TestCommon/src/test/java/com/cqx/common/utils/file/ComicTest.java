package com.cqx.common.utils.file;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comic
 *
 * @author chenqixu
 */
public class ComicTest {
    private static final Logger logger = LoggerFactory.getLogger(ComicTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void comic_scan() {
        // 先扫描目录
        for (String name : FileUtil.listFile("E:\\Photo\\Comic\\爬虫\\image\\")) {
            logger.info("name={}", name);
        }
    }
}
