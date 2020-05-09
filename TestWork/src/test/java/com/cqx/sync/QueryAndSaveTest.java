package com.cqx.sync;

import com.cqx.common.utils.file.FileMangerCenter;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QueryAndSaveTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(QueryAndSaveTest.class);
    private QueryAndSave queryAndSave;

    @Before
    public void setUp() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("edc_addressquery");
        dbBean.setPass_word("edc_addressquery");
        dbBean.setPool(false);
        queryAndSave = new QueryAndSave(dbBean);
    }

    @After
    public void tearDown() throws Exception {
        if (queryAndSave != null) queryAndSave.release();
    }

    @Test
    public void exec() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("table_name", "oper_history");
        params.put("query_fields", "oper_acct,oper_type,oper,oper_time");
        params.put("where", "oper_time");
        params.put("query_time", "'2020-04-30 16:56:43'");
        params.put("interval", "3/(24*60*60)");
        params.put("data_save_path", "d:\\tmp\\data\\syncos\\");
        params.put("split_str", "|");
        queryAndSave.exec(params);
    }

    @Test
    public void writeAndRead() throws Exception {
        String fileName = "d:\\tmp\\data\\syncos\\1.txt";
        final FileMangerCenter fileMangerCenterW = new FileMangerCenter(fileName);
        fileMangerCenterW.initWriter();
        final FileMangerCenter fileMangerCenterR = new FileMangerCenter(fileName);
        fileMangerCenterR.initReader();
        try {
            Thread writeT = new Thread() {
                public void run() {
                    int i = 0;
                    while (i < 100005) {
                        try {
                            if (i == 100004) {
                                fileMangerCenterW.write(i + "", true);
                            } else {
                                fileMangerCenterW.write(i + "");
                            }
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                            break;
                        }
                        i++;
                        if (i % 20000 == 0) SleepUtil.sleepMilliSecond(500);
                    }
                    logger.info("write cnt：{}", i);
                }
            };
            Thread readT = new Thread() {
                public void run() {
                    int i = 0;
                    while (i < 10) {
                        try {
                            int read = 0;
                            String last_msg = "";
                            for (String msg : fileMangerCenterR.read()) {
                                read++;
                                last_msg = msg;
                            }
                            logger.info("read cnt：{}，last msg：{}", read, last_msg);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                            break;
                        }
                        SleepUtil.sleepMilliSecond(500);
                        i++;
                    }
                }
            };
            writeT.start();
            readT.start();
            writeT.join();
            readT.join();
        } finally {
            fileMangerCenterW.close();
            fileMangerCenterR.close();
        }
    }
}