package com.cqx.common.utils.compress;

import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GZUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(GZUtilTest.class);
    private static final String conf = "d:\\tmp\\etc\\hadoop\\conf75\\";
    private HdfsTool hdfsTool;

//    @Before
//    public void setUp() throws Exception {
//        HdfsTool.setHadoopUser("edc_base");
//        HdfsBean hdfsBean = new HdfsBean();
//        hdfsTool = new HdfsTool(conf, hdfsBean);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        hdfsTool.closeFileSystem();
//    }

    @Test
    public void write() throws IOException {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        int max_size = 10 * 1024 * 1024;
        String fileName = "d:\\tmp\\data\\jk\\jk2.data.gz";
        GZUtil gzUtil;
//        gzUtil = GZUtil.buildMemory(true);
        gzUtil = GZUtil.buildFile(fileName, true);
        for (int i = 0; i < 100000; i++) {
            gzUtil.write("您好，北京欢迎您！我们一起相约在2008北京奥运会！".getBytes());
            if (i % 1000 == 0) {
//                logger.info("before {}", gzUtil.size());
                gzUtil.flush();
//                logger.info("after {}", gzUtil.size());
                if (gzUtil.size() > max_size) {
                    logger.info("after {}", gzUtil.size());
                    //切换文件
                    break;
                }
            }
        }
        gzUtil.flush();
        logger.info("flush size {}", gzUtil.size());
        gzUtil.close();
        timeCostUtil.stop();
        logger.info("cost {}，close size {}", timeCostUtil.getCost(), gzUtil.size());
//        gzUtil.saveMemoryToFile(fileName);
    }

    @Test
    public void hdfs_write() throws Exception {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        String hdfs_file_path = "/cqx/data/hbidc/202005200000/nat/000000_0";
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setReader(hdfsTool.openFile(hdfs_file_path));
            FileResult<String> fileResult = new FileResult<String>() {
                @Override
                public void run(String s) {
                    count("read");
                }
            };
            fileUtil.read(fileResult);
            timeCostUtil.stop();
            logger.info("cost：{}，cnt：{}", timeCostUtil.getCost(), fileResult.getCount("read"));
        } finally {
            fileUtil.closeRead();
        }
    }

    @Test
    public void local_read_write() throws Exception {
        String fileName = "d:\\tmp\\data\\jk\\000000_0";
        final String gzFilePath = "d:\\tmp\\data\\jk\\";
        final int max_size = 30 * 1024 * 1024;
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setReader(fileName);
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            FileResult<String> fileResult = new FileResult<String>() {

                int num = 0;
                GZUtil gzUtil = GZUtil.buildFile(getName("%sjk2_%s.data.gz"), true);

                private String getName(String format) {
                    num++;
                    return String.format(format, gzFilePath, num);
                }

                @Override
                public void run(String s) throws IOException {
                    gzUtil.write(s.getBytes());
                    count("read");
                    if (getCount("read") % 1000 == 0) {
                        gzUtil.flush();
                        if (gzUtil.size() > max_size) {
                            logger.info("达到切割的文件大小：{}", gzUtil.size());
                            gzUtil.close();
                            gzUtil = GZUtil.buildFile(getName("%sjk2_%s.data.gz"), true);
                        }
                    }
                }

                @Override
                public void tearDown() throws IOException {
                    if (gzUtil != null) gzUtil.close();
                }
            };
            fileUtil.read(fileResult);
            timeCostUtil.stop();
            logger.info("cost：{}，cnt：{}", timeCostUtil.getCost(), fileResult.getCount("read"));
        } finally {
            fileUtil.closeRead();
        }
    }
}