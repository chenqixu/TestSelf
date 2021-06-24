package com.cqx.common.utils.list;

import com.cqx.common.bean.model.DataBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class KVListTest {
    private static final Logger logger = LoggerFactory.getLogger(KVListTest.class);

    @Test
    public void put() {
        KVList<String, String> kvList = new KVList<>();
        kvList.put("1", "2");
        kvList.put("1", "3");
        logger.info("{}", kvList.size());
        for (IKVList.Entry<String, String> entry : kvList.entrySet()) {
            logger.info("entry：{}", entry);
        }
        logger.info("{}", kvList.get(0));
        logger.info("keys：{}", kvList.keys());
        logger.info("values：{}", kvList.values());
    }

    @Test
    public void dist() {
        Random random = new Random();
        LinkedHashMap<Integer, String> lhm = new LinkedHashMap<>();
        for (int i = 0; i < 50; i++) {
//            lhm.put(random.nextInt(10), i + "");
            lhm.put(i, i + "");
        }
//        for (Map.Entry<Integer, String> entry : lhm.entrySet()) {
//            logger.info("{} {}", entry.getKey(), entry.getValue());
//        }
        for (String entry : new ArrayList<>(lhm.values())) {
            logger.info("{}", entry);
        }
    }

    private List<DataBean> distList(List<DataBean> list) {
        // 使用LinkedHashMap去重
        LinkedHashMap<String, DataBean> lhm = new LinkedHashMap<>();
        for (DataBean dataBean : list) {
            lhm.put(dataBean.getDistKey(), dataBean);
        }
        // 返回去重后的List
        return new ArrayList<>(lhm.values());
    }
}