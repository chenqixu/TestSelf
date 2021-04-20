package com.cqx.algorithm.bit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class BitCalcTest {
    private static final Logger logger = LoggerFactory.getLogger(BitCalcTest.class);

    @Test
    public void xor() {
        // n^0=n
        // n^n=0
        int a = 5;
        int b = 2;
        int c = 3;
        int d = BitCalc.xor(BitCalc.xor(a, b), c);
        logger.info("d：{}", d);
        logger.info("a：{}", BitCalc.xor(BitCalc.xor(d, b), c));
        logger.info("{} binary：{}", d, Integer.toBinaryString(d));
        // 1-1000放在含有1001个元素的数组中，只有唯一的一个元素值重复，其它均只出现一次。
        // 每个数组元素只能访问一次，设计一个算法，将它找出来
        // 假设：1^2^3......^n.....^1000=T
        // 而： 1^2^3......^n^n.....^1000 = T^n
        // T^T^n = 0^n = n
        int num = 100;
        Integer[] arrays = new Integer[num + 1];
        Integer result1 = null;
        Integer result2 = null;
        for (int i = 1; i < num; i++) {
            arrays[i - 1] = i;
            result1 = (result1 == null ? i : result1 ^ (i + 1));
        }
        // 因为是从0开始，到num-2结束，所以这里要补一个num-1，然后在num位置填上一个随机数
        arrays[num - 1] = num;
        arrays[num] = new Random().nextInt(num);
        for (int i = 0; i < num; i++) {
            result2 = (result2 == null ? arrays[i] : result2 ^ arrays[i + 1]);
        }
        logger.info("random：{}，result1 ^ result2：{}", arrays[num], result1 ^ result2);
    }
}