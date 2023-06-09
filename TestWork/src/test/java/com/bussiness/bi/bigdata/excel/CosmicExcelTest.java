package com.bussiness.bi.bigdata.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class CosmicExcelTest {

    @Test
    public void readCosmicExcel() {
        new CosmicExcel().readCosmicExcel("d:\\Work\\割接\\202212-迁移X9\\cosmic\\X4O域实时位置配置迁改大数据库-附件4、COSMIC工作量拆分.xlsx");
    }

    @Test
    public void jsonToMap() {
        Object object = JSON.parse("{\"id\":123, \"name\":\"test\"}");
        Map<String, Object> jsonObject = (JSONObject) object;
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue());
        }
    }

    @Test
    public void timeFormatOut() {
        String time = "2023-06-08 15:0%s:%s";
        Random random = new Random(System.currentTimeMillis());
        // 每30秒打印一次
        // 拿到一个时间，输出是属于哪个时间段，比如00[00-29]还是30[30-59]
        final Map<String, AtomicLong> sum = new HashMap<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<SumBean> sumBeanList = new ArrayList<>();
                while (true) {
                    sumBeanList.clear();
                    for (Map.Entry<String, AtomicLong> entry : sum.entrySet()) {
                        sumBeanList.add(new SumBean(entry));
                    }
                    Collections.sort(sumBeanList);
                    for (SumBean sumBean : sumBeanList) {
                        System.out.println(sumBean);
                    }
                    System.out.println("=====================");
                    SleepUtil.sleepMilliSecond(500L);
                }
            }
        }).start();
        for (int i = 0; i < 1000000; i++) {
            int m = random.nextInt(9);
            int ss = random.nextInt(59);
            String _ss = ss + "";
            if (ss < 10) {
                _ss = "0" + ss;
            }
            // 判断秒值是在00[00-29]还是30[30-59]
            String key;
            if (0 <= ss && ss <= 29) {// 00
                key = String.format(time, m, "00");
            } else {// 30
                key = String.format(time, m, "30");
            }
            AtomicLong cnt = sum.get(key);
            if (cnt == null) {
                cnt = new AtomicLong(0L);
                sum.put(key, cnt);
            }
            cnt.incrementAndGet();
        }
        SleepUtil.sleepSecond(5L);
    }
}