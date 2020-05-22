package com.cqx.common.utils.hdfs;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HdfsToolTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(HdfsToolTest.class);
    private static final String conf = "d:\\tmp\\etc\\hadoop\\conf75\\";
    private HdfsTool hdfsTool;

    @Before
    public void setUp() throws Exception {
        HdfsTool.setHadoopUser("edc_base");
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(conf, hdfsBean);
    }

    @After
    public void tearDown() throws Exception {
        hdfsTool.closeFileSystem();
    }

    @Test
    public void createFile() throws Exception {
        try (OutputStream os = hdfsTool.createFile("/cqx/data/ouyuan")) {
//            os.write("€".getBytes(StandardCharsets.UTF_8));
            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            os.write("\r".getBytes(StandardCharsets.UTF_8));
//            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            os.write("\n".getBytes(StandardCharsets.UTF_8));
            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            os.write("\r\n".getBytes(StandardCharsets.UTF_8));
            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            os.write("\n".getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void delete() throws IOException {
        hdfsTool.delete("/cqx/data/ouyuan");
    }

    @Test
    public void openFile() throws Exception {
        try (InputStream is = hdfsTool.openFile("/cqx/data/ouyuan");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String str;
            while ((str = br.readLine()) != null) {
                logger.info("{}", str);
            }
        }
    }

    @Test
    public void all() throws Exception {
        delete();
        createFile();
        openFile();
    }

    @Test
    public void ls() throws Exception {
        for (String path : hdfsTool.lsPath("/cqx/data/hbidc/20200520*/nat/*")) {
            logger.info("path：{}", path);
        }
    }
}