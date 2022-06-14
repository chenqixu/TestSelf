package com.bussiness.bi.bigdata.utils.system;

/**
 * TODO
 *
 * @author chenqixu
 */
public interface IRedo {
    void onRetry();

    void retryCondition();

    void retryExceptionCondition();
}
