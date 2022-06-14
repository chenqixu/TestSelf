package com.bussiness.bi.bigdata.file;

import com.bussiness.bi.bigdata.changecode.FileUtil;
import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FileRandomCreateTest {

    private static Logger logger = LoggerFactory.getLogger(FileRandomCreateTest.class);
    private FileRandomCreate fileRandomCreate;
    private String path = "d:\\tmp\\data\\dpi\\dpi_s1mme\\streaminput\\";

    @Before
    public void setUp() {
        fileRandomCreate = new FileRandomCreate();
    }

    @Test
    public void create() throws Exception {
        Set<String> list = new HashSet<>();
        for (int i = 0; i < 1; i++) {
            list.add(fileRandomCreate.create());
        }
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String filename = it.next();
            fileRandomCreate.createFile(path + filename);
        }
    }

    /**
     * 往s1mme目录创建文件，不满足200就创建，直到200为止
     *
     * @throws Exception
     */
    @Test
    public void createS1mme() throws Exception {
        // 获取s1mme目录下S1MME文件个数，不满足200就创到200
        while (true) {
            int fileCnt = FileUtil.listFile(path, "S1MME").length;
            int diffValue = 200 - fileCnt;
//            logger.info("fileCnt：{}，diffValue：{}", fileCnt, diffValue);
            if (diffValue > 0) {
                logger.info("条件满足，造文件：{}", diffValue);
                Set<String> list = new HashSet<>();
                for (int i = 0; i < diffValue; i++) {
                    list.add(fileRandomCreate.create());
                }
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    String filename = it.next();
                    fileRandomCreate.createFile(path + filename);
                }
            }
            SleepUtils.sleepMilliSecond(50);
        }
    }
}