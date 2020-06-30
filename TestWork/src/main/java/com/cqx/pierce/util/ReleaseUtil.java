package com.cqx.pierce.util;

/**
 * ReleaseUtil
 *
 * @author chenqixu
 */
public class ReleaseUtil {
    private int cnt;
    private int max = 5;

    public ReleaseUtil() {
    }

    public ReleaseUtil(int max) {
        this.max = max;
    }

    public boolean add() {
        cnt++;
        if (cnt >= max) {
            clean();
            return true;
        }
        return false;
    }

    public void clean() {
        cnt = 0;
    }
}
