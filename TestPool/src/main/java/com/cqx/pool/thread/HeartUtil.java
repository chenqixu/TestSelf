package com.cqx.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 心跳工具
 *
 * @author chenqixu
 */
public class HeartUtil {
    private static Logger logger = LoggerFactory.getLogger(HeartUtil.class);
    private volatile Map<Integer, String> heartMap;
    private AtomicInteger heartMod = new AtomicInteger();

    public HeartUtil() {
        this.heartMap = new HashMap<>();
    }

    /**
     * 获取心跳的标志，从0开始
     *
     * @return
     */
    public int getHeartMod() {
        int mod = heartMod.incrementAndGet();
        mod--;
        return mod;
    }

    /**
     * 心跳初始化
     *
     * @param mod
     */
    public void init(int mod) {
        heart(mod);
    }

    /**
     * 心跳
     */
    public void heart(int mod) {
        synchronized (heartMap) {
            heartMap.put(mod, "" + System.currentTimeMillis());
        }
        logger.info("heart {}，heartMap：{}", mod, heartMap);
    }

    /**
     * 打印心跳状态
     */
    public void printStatus() {
        logger.info("heartMap：{}", heartMap);
    }
}
