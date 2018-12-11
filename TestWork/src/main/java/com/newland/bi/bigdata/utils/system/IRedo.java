package com.newland.bi.bigdata.utils.system;

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
