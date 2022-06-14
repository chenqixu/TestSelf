package com.bussiness.bi.bigdata.utils.math;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;



/**
 * 随机工具
 *
 * @author chenqixu
 */
public class RandomUtil {

    private static MyLogger logger = MyLoggerFactory.getLogger(RandomUtil.class);

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
