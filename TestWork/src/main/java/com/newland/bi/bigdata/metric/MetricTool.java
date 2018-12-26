package com.newland.bi.bigdata.metric;

import java.util.HashMap;
import java.util.Map;

/**
 * 监控工具类
 *
 * @author chenqixu
 */
public class MetricTool {

    private static Map<String, MeticChild> childHashMap = new HashMap<>();

    public synchronized static void start(String name) {
        if (childHashMap.get(name) != null) return;
        childHashMap.put(name, new MeticChild());
    }

    public synchronized static long end(String name) {
        return childHashMap.get(name).end();
    }

    static class MeticChild {
        private long startTime;

        public MeticChild() {
            startTime = System.currentTimeMillis();
        }

        public long end() {
            long endTime = System.currentTimeMillis();
            return endTime - startTime;
        }
    }
}
