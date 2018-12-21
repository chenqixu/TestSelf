package com.newland.bi.bigdata.utils.math;

import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;

/**
 * 随机工具
 *
 * @author chenqixu
 */
public class RandomUtil {

    private static Logger logger = LogInfoFactory.getInstance(RandomUtil.class);

    public static int getInt() {
        return (int) (Math.random() * 5 + 1);
    }

    public static int getMod(int val, int size) {
        logger.info("val：{}，size：{}，result：{}", val, size, val - (val / size) * size);
        if (val <= 0) return 0;
        return val - (val / size) * size;
    }

    public static void main(String[] args) {
        for (int i = 1; i < 20; i++) {
            getMod(i, 5);
        }
    }
}
