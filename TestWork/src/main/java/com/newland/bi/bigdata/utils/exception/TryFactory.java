package com.newland.bi.bigdata.utils.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TryFactory
 *
 * @author chenqixu
 */
public class TryFactory {

    private static Logger logger = LoggerFactory.getLogger(TryFactory.class);
    private ITry iTry;

    private TryFactory(ITry iTry) {
        this.iTry = iTry;
    }

    public static TryFactory builder(ITry iTry) {
        return new TryFactory(iTry);
    }

    public void start() {
        try {
            iTry.run();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
