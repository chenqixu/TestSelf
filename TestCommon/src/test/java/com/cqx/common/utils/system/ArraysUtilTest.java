package com.cqx.common.utils.system;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArraysUtilTest {

    @Test
    public void setToStr() {
        List<String> list = new ArrayList<>();
        list.add("t1");
        list.add("t2");
        list.add("t3");
        System.out.println(ArrayUtil.collectionToStr(list, ','));
    }

    @Test
    public void queueTest() {
        AtomicBoolean isFirst = new AtomicBoolean(true);
        String tmp;
        BlockingDeque<String> queue = new LinkedBlockingDeque<>();
        System.out.println("=======================");
        System.out.println("==先进先出，offer和poll方向要相反");
        System.out.println("=======================");
        System.out.println("==先进先出 offerLast + pollFirst==");
        // offer - offerLast
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        // poll - pollFirst
        while ((tmp = queue.poll()) != null) {
            if (tmp.equals("2") && isFirst.getAndSet(false)) {
                System.out.println("第一次遇到2，丢回栈顶，size：" + queue.size());
                queue.offerFirst("2");
            } else {
                System.out.println(tmp);
            }
        }

        System.out.println("==先进先出2 offerFirst + pollLast==");
        queue.offerFirst("1");
        queue.offerFirst("2");
        queue.offerFirst("3");
        while ((tmp = queue.pollLast()) != null) {
            System.out.println(tmp);
        }

        System.out.println("=======================");
        System.out.println("==先进后出，offer和poll方向要相同");
        System.out.println("=======================");
        System.out.println("==先进后出1 offerFirst + pollFirst==");
        queue.offerFirst("1");
        queue.offerFirst("2");
        queue.offerFirst("3");
        while ((tmp = queue.poll()) != null) {
            System.out.println(tmp);
        }

        System.out.println("==先进后出2 offerLast + pollLast==");
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        while ((tmp = queue.pollLast()) != null) {
            System.out.println(tmp);
        }
    }

    /**
     * 数组比较，只要d2包含d1的某个元素，就为真
     */
    @Test
    public void arrayCompare() {
        String d1 = "1,2,3";
        String d2 = "4,5,6,3";
        String[] a1 = d1.split(",", -1);
        String[] a2 = d2.split(",", -1);
        List<String> l1 = Arrays.asList(a1);
        for (String s2 : a2) {
            boolean ret = l1.contains(s2);
            System.out.println(String.format("s2=%s, ret=%s", s2, ret));
            if (ret) break;
        }
    }
}