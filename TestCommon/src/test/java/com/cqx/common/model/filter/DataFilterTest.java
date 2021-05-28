package com.cqx.common.model.filter;

import com.cqx.common.bean.model.DataBean;
import com.cqx.common.utils.jdbc.QueryResultETL;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class DataFilterTest {
    private static final Logger logger = LoggerFactory.getLogger(DataFilterTest.class);
    private DataFilter<DataBean> dataFilter;

    @Before
    public void setUp() throws Exception {
        DataFilterActionEnum mode;
        try {
            mode = DataFilterActionEnum.valueOf(System.getenv().get("data_filter_action"));
        } catch (Exception e) {
            //
            mode = DataFilterActionEnum.MEMORY_ACTION;
        }
        Map param = new HashMap<>();
        param.put("filePath", "d:\\tmp\\data\\raffile\\");
        param.put("fileName", "BigMerge");
        param.put("singleFileMaxLength", 100000L);
        param.put("data_filter_file_MaxNum", 2);
        dataFilter = new DataFilter<>((Map<String, ?>) param, new IDataFilterCall<DataBean>() {
            @Override
            public void call(List<DataBean> dataBeans) {
                logger.info("【具体处理】开始");
                for (DataBean dataBean : dataBeans) logger.info("{}", dataBean);
                logger.info("【具体处理】完成");
            }
        }, 5000, mode, DataBean.class);
    }

    @After
    public void tearDown() throws Exception {
        if (dataFilter != null) dataFilter.close();
    }

    @Test
    public void add() throws InterruptedException {
        BaseRunable readBR = new BaseRunable() {
            @Override
            public void exec() throws Exception {
                List<DataBean> results = dataFilter.poll(10L);
                if (results != null && results.size() > 0) {
                    logger.info("poll：{}", results);
                }
            }
        };
        Thread read = new Thread(readBR);
        read.start();
        long current = System.currentTimeMillis() - (10 * 1000L);
        Random random = new Random();
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 120; i += 5) {
            int randomMicro = random.nextInt(999);
            String randomMicroStr;
            if (randomMicro < 10) randomMicroStr = "00" + randomMicro;
            else if (randomMicro < 100) randomMicroStr = "0" + randomMicro;
            else randomMicroStr = randomMicro + "";
            String newTime = TimeUtil.formatTime(current + (i * 1000L), "yyyy-MM-dd'T'HH:mm:ss.SSS")
                    + randomMicroStr;
            if (randomMicro < 500) {
                newTime = TimeUtil.formatTime(current + 1000L, "yyyy-MM-dd'T'HH:mm:ss.SSS")
                        + randomMicroStr;
            }
            list.add(newTime);
        }
        for (String val : list) {
            dataFilter.add(new DataBean("i", val, createQR()));
            SleepUtil.sleepMilliSecond(random.nextInt(1000));
        }
        readBR.stop();
        read.join();
    }

    private List<QueryResultETL> createQR() {
        QueryResultETL qr = new QueryResultETL();
        qr.setValue("你好不好？");
        qr.setColumnType(1);
        qr.setColumnName("content");
        qr.setColumnClassName("java.lang.String");
        List<QueryResultETL> qrs = new ArrayList<>();
        qrs.add(qr);
        return qrs;
    }

    @Test
    public void json() throws ParseException {
        long current = System.currentTimeMillis() - (10 * 1000L);
        Random random = new Random();
        int randomMicro = random.nextInt(999);
        String randomMicroStr;
        if (randomMicro < 10) randomMicroStr = "00" + randomMicro;
        else if (randomMicro < 100) randomMicroStr = "0" + randomMicro;
        else randomMicroStr = randomMicro + "";
        String newTime = TimeUtil.formatTime(current + (1 * 1000L), "yyyy-MM-dd'T'HH:mm:ss.SSS")
                + randomMicroStr;
        List<QueryResultETL> queryResults = new ArrayList<>();
        QueryResultETL queryResult = new QueryResultETL();
        queryResult.setValue(new Timestamp(TimeUtil.getTime("2021-05-21 14:24:38")));
        queryResults.add(queryResult);
        DataBean dataBeanS = new DataBean("i", newTime, queryResults);
        String json = dataBeanS.toJson();
        System.out.println("json：" + json);
        DataBean dataBean = DataBean.jsonToBean(json);
        System.out.println("jsonToBean：" + dataBean);
    }
}