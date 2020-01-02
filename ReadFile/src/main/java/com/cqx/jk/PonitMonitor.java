package com.cqx.jk;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PonitMonitor
 *
 * @author chenqixu
 */
public class PonitMonitor {

    private long last_time = System.currentTimeMillis();
    private long split_time = 500L;
    private int sum = 0;
    private String title = "default";

    public PonitMonitor(String title) {
        this(title, 500L);
    }

    public PonitMonitor(String title, long split_time) {
        this.title = title;
        this.split_time = split_time;
    }

    private boolean isSplit() {
        long now_time = System.currentTimeMillis();
        long diff_value = now_time - last_time;
        if (diff_value >= split_time) {
            last_time = now_time;
            return true;
        } else {
            return false;
        }
    }

    public void addPonit(int value) {
        sum = sum + value;
        if (isSplit()) {
            System.out.println(String.format("%S【%S】：%S",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), title, sum));
        }
    }
}
