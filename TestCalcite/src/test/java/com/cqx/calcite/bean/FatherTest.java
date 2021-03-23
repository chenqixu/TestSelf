package com.cqx.calcite.bean;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FatherTest {

    @Test
    public void getName() throws Exception {
        Father father = new Father();
        father.setAge(30);
        father.setName("Simon");
        //父类无法强制转换成子类
//        Son son = new Son((Son) father);
//        System.out.println(son.getAge());
//        System.out.println(son.getName());
//        System.out.println(son.getFashion());

        String a = null;
        if ("a".equals(a)) {
            System.out.println("a");
        } else {
            System.out.println(a);
        }

        Date b = null;
        Object b1 = b;
        Date c = (Date) b1;
        System.out.println(c);

        Integer n1 = null;
        System.out.println(n1 == null ? null : BigDecimal.valueOf(n1));

        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>(5);
        for (int i = 0; i < 10; i++) {
//            queue.offer(i, 500, TimeUnit.MILLISECONDS);
            queue.offer(i);
        }
        Integer ret;
        while ((ret = queue.poll()) != null) {
            System.out.println(ret);
        }
    }
}