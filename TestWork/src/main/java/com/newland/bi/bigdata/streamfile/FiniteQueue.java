package com.newland.bi.bigdata.streamfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 有限队列
 *
 * @author chenqixu
 */
public class FiniteQueue<T> {

    public static final long FINITEQUEUE_TIMEOUT = 60 * 60 * 1000;
    private static Logger logger = LoggerFactory.getLogger(FiniteQueue.class);
    private Map<T, Long> finitequeue;
    private int size = 100;
    private boolean isTimeOut = false;

    public FiniteQueue() {
        finitequeue = new LinkedHashMap<>();
    }

    public FiniteQueue(boolean isTimeOut) {
        this();
        this.isTimeOut = isTimeOut;
    }

    public FiniteQueue(int size) {
        this();
        this.size = size;
    }

    public FiniteQueue(boolean isTimeOut, int size) {
        this();
        this.isTimeOut = isTimeOut;
        this.size = size;
    }

    /**
     * 如果大于finitesize，则从队头移走一个，数据加入队尾
     *
     * @param key
     */
    public synchronized void put(T key) {
        if (finitequeue.size() > size) {
            finitequeue.remove(next());
        }
        finitequeue.put(key, System.currentTimeMillis());
    }

    private synchronized T next() {
        Iterator<T> it = finitequeue.keySet().iterator();
        while (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public synchronized Long remove(T key) {
        return finitequeue.remove(key);
    }

    /**
     * 根据key查找是否存在<br>
     * 如果有超时设置且超时，就从缓存移除
     *
     * @param key
     * @return
     */
    public synchronized boolean find(T key) {
        Long startTime = finitequeue.get(key);
        if (startTime != null && isTimeOut) {
            if (System.currentTimeMillis() - startTime > FINITEQUEUE_TIMEOUT) {
                logger.info("istimeout：{}，remove：{}", System.currentTimeMillis() - startTime, key);
                finitequeue.remove(key);
            }
        }
        return (finitequeue.get(key)) != null;
    }
}
