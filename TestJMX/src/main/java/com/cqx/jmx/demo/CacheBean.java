package com.cqx.jmx.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 缓存
 *
 * @author chenqixu
 */
public class CacheBean {
    private List<String> msgList1 = new ArrayList<>();
    private List<String> msgList2 = new ArrayList<>();
    private List<String> nowList;
    private boolean flag;

    public void init() {
        Random random = new Random();
        int s1 = random.nextInt(1000);
        int s2 = random.nextInt(1000);
        for (int i = 0; i < s1; i++) msgList1.add("123");
        for (int i = 0; i < s2; i++) msgList2.add("123");
        reflush();
    }

    public void reflush() {
        if (flag) {
            flag = false;
            nowList = msgList1;
        } else {
            flag = true;
            nowList = msgList2;
        }
    }

    public int size() {
        return nowList == null ? 0 : nowList.size();
    }
}
