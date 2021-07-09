package com.cqx.common.bean.javabean;

import com.cqx.common.utils.Utils;

/**
 * ErrorBean
 *
 * @author chenqixu
 */
public class ErrorBean {
    private static final int MAX_LEN = 5;
    private int index = 0;

    public int newError() {
        index++;
        if (index > MAX_LEN) index = 1;
        return index;
    }

    public String getErrorVal(String errorMsg) {
        long current = System.currentTimeMillis();
        String time = Utils.formatTime(current);
        return current + "," + time + "," + errorMsg;
    }

    public void setIndex(int index) {
        if (index > 0 && index <= MAX_LEN) {
            this.index = index;
        }
    }
}
