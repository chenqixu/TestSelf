package com.newland.bi.bigdata.file;

import com.cqx.common.utils.file.FileMangerCenter;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.newland.bi.bigdata.time.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MyFileReaderTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(MyFileReaderTest.class);
    private MyFileReader myFileReader;
    private String file_name = "d:\\tmp\\data\\syncos\\2.txt";
//    private String file_name = "d:\\tmp\\data\\jk\\jk1.data";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        if (myFileReader != null) myFileReader.close();
    }

    @Test
    public void testAll() throws IOException {
        writeFile();
        readLine();
    }

    @Test
    public void readLine() throws IOException {
        myFileReader = new MyFileReader(file_name);
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        int num = 0;
        String tmp;
        while ((tmp = myFileReader.readLine()) != null) {
//            logger.info(tmp + " " + StringUtil.byteArrayToList(tmp.getBytes()));
            num++;
        }
        timeCostUtil.stop();
        logger.info("read num：{}，read cost：{}", num, timeCostUtil.getCost());
//        logger.info("line：" + StringUtil.byteArrayToList("\n".getBytes()));
//        logger.info("line：" + StringUtil.byteArrayToList("\r".getBytes()));
    }

    @Test
    public void writeFile() throws IOException {
        FileUtil.del(file_name);
        FileMangerCenter fileMangerCenter = null;
        try {
            fileMangerCenter = new FileMangerCenter(file_name);
            fileMangerCenter.initWriter();
            String split = "\n";
            //支持以下3种
            // \r    13  回车
            // \n   10  换行
            // \r\n 13 10  回车换行
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            int num = 0;
            for (int i = 0; i < 10; i++) {
                num++;
                fileMangerCenter.writeSingle("1234567890你\n");
            }
//            fileMangerCenter.writeSingle(split + "你好");
            timeCostUtil.stop();
            logger.info("write num：{}，cost：{}", num, timeCostUtil.getCost());
        } finally {
            if (fileMangerCenter != null) fileMangerCenter.close();
        }
    }
}