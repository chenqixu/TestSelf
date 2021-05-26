package com.cqx.common.utils.serialize.impl;

import com.cqx.common.bean.model.DataBean;
import com.cqx.common.utils.jdbc.QueryResultETL;
import com.cqx.common.utils.serialize.AbstractSerializer;
import com.cqx.common.utils.system.TimeUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ProtoStuffSerializerTest {
    private AbstractSerializer<DataBean> serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new ProtoStuffSerializer<>(DataBean.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void t2bs() {
        byte[] buffer = serializer.t2bs(generator());
        DataBean dataBean = serializer.bs2t(buffer);
        System.out.println(dataBean);
    }

    private DataBean generator() {
        try {
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
            queryResult.setValue(new Timestamp(new Date().getTime()));
            queryResults.add(queryResult);
            return new DataBean("i", newTime, queryResults);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}