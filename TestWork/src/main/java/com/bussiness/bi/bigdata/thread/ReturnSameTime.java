package com.bussiness.bi.bigdata.thread;

import com.bussiness.bi.bigdata.metric.MetricsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * ReturnSameTime
 *
 * @author chenqixu
 */
public class ReturnSameTime {
    public static void main(String[] args) throws Exception {

        Callable<Integer> a = new CallableTest();
        Callable<Integer> b = new CallableTest2();
        Callable<Integer> c = new CallableTest3();

        List<FutureTask<Integer>> futureTaskList = new ArrayList<>();
        FutureTask<Integer> aTask = new FutureTask<Integer>(a);
        futureTaskList.add(aTask);
        FutureTask<Integer> bTask = new FutureTask<Integer>(b);
        futureTaskList.add(bTask);
        FutureTask<Integer> cTask = new FutureTask<Integer>(c);
        futureTaskList.add(cTask);

        new Thread(aTask).start();
        new Thread(bTask).start();
        new Thread(cTask).start();

        MetricsUtil metricTool = MetricsUtil.builder();
        metricTool.addTimeTag();
        for (FutureTask<Integer> futureTask : futureTaskList) {
            int num = futureTask.get();
            System.out.println(num);
        }
        System.out.println(metricTool.getTimeOut());

//        int num1 = aTask.get();
//        System.out.println(num1);
//        int num2 = bTask.get();
//        System.out.println(num2);
//        int num3 = cTask.get();
//        System.out.println(num3);
//        System.out.println("==================");
//        int result = num1 + num2 + num3;
//
//        System.out.println(result);
    }

    private static class CallableTest implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("test");
            return 1;
        }
    }

    private static class CallableTest2 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("test2 start");
            Thread.sleep(3 * 1000);
            System.out.println("test2 end");
            return 2;
        }
    }

    private static class CallableTest3 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("test3 start");
            Thread.sleep(2 * 1000);
            System.out.println("test3 end");
            return 3;
        }
    }
}
