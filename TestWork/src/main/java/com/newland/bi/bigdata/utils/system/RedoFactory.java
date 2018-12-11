package com.newland.bi.bigdata.utils.system;

/**
 * TODO
 *
 * @author chenqixu
 */
public class RedoFactory {

    int maxAttempts = 1;

    public void exec(IRedo iRedo) {
        exec(iRedo, maxAttempts);
    }

    public void exec(IRedo iRedo, int maxAttempts) {
        try {
            iRedo.onRetry();
        } catch (Exception e) {

        }
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
