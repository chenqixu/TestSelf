package com.cqx.yaoqi;

/**
 * FileUtil
 *
 * @author chenqixu
 */
public class FileUtil {
    private int index = 1;
    private String title;

    public void increase() {
        index++;
    }

    public void reset() {
        index = 1;
    }

    public int getIndex() {
        return index;
    }

    public int getIndexAndIncrease() {
        int old = index;
        index++;
        return old;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
