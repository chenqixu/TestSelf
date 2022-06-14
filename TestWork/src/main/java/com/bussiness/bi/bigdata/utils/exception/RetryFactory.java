package com.bussiness.bi.bigdata.utils.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RetryFactory
 *
 * @author chenqixu
 */
public class RetryFactory {

    private static Logger logger = LoggerFactory.getLogger(RetryFactory.class);
    private IDeal iDeal;
    private ICatch iCatch;
    private int retryCnt = 0;

    private RetryFactory(IDeal iDeal, ICatch iCatch) {
        this.iDeal = iDeal;
        this.iCatch = iCatch;
    }

    public static RetryFactory builder(IDeal iDeal, ICatch iCatch) {
        return new RetryFactory(iDeal, iCatch);
    }

    private void checkStatus() {
        if (iDeal == null || iCatch == null || getRetryCnt() < 0)
            throw new NullPointerException(String.format("checkStatus fail！iDeal：%s，iCatch：%s，retryCnt：%s", iDeal, iCatch, retryCnt));
    }

    private void run(int runcnt) {
        logger.info("runcnt：{}", runcnt);
        try {
            iDeal.dealEvent();
        } catch (Exception e) {
            iCatch.catchEvent(e);
            iCatch.release();
            if (--runcnt > 0)
                run(runcnt);
        }
    }

    public void start() {
        checkStatus();
        run(getRetryCnt());
    }

    public int getRetryCnt() {
        return retryCnt;
    }

    public RetryFactory setRetryCnt(int retryCnt) {
        this.retryCnt = retryCnt;
        return this;
    }
}
