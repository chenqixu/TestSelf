package com.cqx.localcache.mod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ModBDWork
 *
 * @author chenqixu
 */
public class ModDBWork {
    private static final Logger logger = LoggerFactory.getLogger(ModDBWork.class);
    private int count;
//    private Map<>

    public ModDBWork(int count) {
        this.count = count;
    }

    public void add(long val) {
        long mod = val % count;
        logger.info("mod：{}", mod);
    }
}
