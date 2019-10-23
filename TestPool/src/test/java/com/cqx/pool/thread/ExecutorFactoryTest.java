package com.cqx.pool.thread;

import com.cqx.pool.util.SleepUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorFactoryTest {

    private ExecutorFactory<Boolean> executorFactory;
    private ExecutorFactory<Integer> executorFactoryWriter;
    private ExecutorFactory<Integer> executorFactoryReader;
    //    private LinkedBlockingQueue<Integer> queue;
    private ContentQueue contentQueue;
    private AtomicInteger cnt = new AtomicInteger();

    @Before
    public void setUp() throws Exception {
        executorFactory = new ExecutorFactory<>(50);
        executorFactoryWriter = new ExecutorFactory<>(44);
        executorFactoryReader = new ExecutorFactory<>(1);
    }

    @After
    public void tearDown() throws Exception {
        executorFactory.shutdonw();
        executorFactoryWriter.shutdonw();
        executorFactoryReader.shutdonw();
    }

    @Test
    public void get() {
        ThreadPoolMonitor threadPoolMonitor = new ThreadPoolMonitor(executorFactory.getExecutor());
        threadPoolMonitor.startPoolMonitor();
        TestCall testCall = new TestCall();
        for (int i = 0; i < 50; i++) {
            executorFactory.addCallable(new TestCallable(testCall));
        }
        for (int i = 0; i < 100; i++) {
            executorFactory.submit();
            SleepUtils.sleepMilliSecond(200);
//            executorFactory.get(executorFactory.submit(), 3, TimeUnit.SECONDS);
        }
        SleepUtils.sleepSecond(5);
        System.out.println(testCall.getCnt());
    }

    /**
     * 队列测试，多个写，一个读
     */
    @Test
    public void queueTest() throws Exception {
        contentQueue = new ContentQueue();
        // 启动线程
        QueueWriterRunable queueWriterRunable = new QueueWriterRunable(contentQueue);
        for (int i = 0; i < 2; i++) {
            executorFactoryWriter.addCallable(queueWriterRunable);
        }
        executorFactoryWriter.submit();
        // 把队列写满
        for (int i = 0; i < 100; i++) {
            contentQueue.push(i);
            System.out.println(i);
        }
    }

    /**
     * 模拟nextTuple
     */
    @Test
    public void queueNextTupleTest() {
        contentQueue = new ContentQueue();
        for (int i = 0; i < 2; i++) {
            QueueWriterRunable queueWriterRunable = new QueueWriterRunable(contentQueue);
            executorFactoryWriter.addCallable(queueWriterRunable);
        }
        // 执行10秒
        for (int i = 0; i < 10000; i++) {
            nextTuple();
            SleepUtils.sleepMilliSecond(1);
        }
    }

    private void nextTuple() {
        executorFactoryWriter.submitNoReturn();
        Integer result;
        if ((result = contentQueue.poll()) != null) {
            System.out.println("poll：" + result);
            SleepUtils.sleepMilliSecond(500);
        }
    }

    public class TestCall {
        volatile int cnt = 0;

        public void add() {
            cnt++;
        }

        public int getCnt() {
            return cnt;
        }
    }

    public class TestCallable implements Callable<Boolean> {

        ConcurrentHashMap<Integer, Integer> modMap = new ConcurrentHashMap<>();
        AtomicInteger ai = new AtomicInteger();
        TestCall testCall;

        public TestCallable(TestCall testCall) {
            this.testCall = testCall;
        }

        @Override
        public Boolean call() throws Exception {
//            TimeCostUtil timeCostUtil = new TimeCostUtil();
//            timeCostUtil.start();
//            int key = ai.incrementAndGet();
//            if (key > 3) {
//                ai.set(0);
//                key = 1;
//            }
//            Integer mod = modMap.get(key);
//            if (mod == null) {
//                modMap.put(key, key);
//            }
//            Random random = new Random();
//            SleepUtils.sleepSecond(random.nextInt(10));
//            timeCostUtil.end();
//            System.out.println(this + "，cost：" + timeCostUtil.getCost());
            testCall.add();
            return true;
        }
    }

    public class ContentQueue {
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(50);

        synchronized void push(int value) throws InterruptedException {
            queue.put(value);
        }

        Integer poll() {
            return queue.poll();
        }

        int getSize() {
            return queue.size();
        }
    }

    private class QueueAbs {
        ContentQueue queue;

        QueueAbs(ContentQueue queue) {
            this.queue = queue;
        }
    }

    public class QueueWriterRunable extends QueueAbs implements Callable<Integer> {

        public QueueWriterRunable(ContentQueue queue) {
            super(queue);
        }

        @Override
        public Integer call() throws Exception {
            int result = cnt.getAndIncrement();
            SleepUtils.sleepMilliSecond(500);
            queue.push(result);
            System.out.println(this + "，put：" + result + "，size：" + queue.getSize());
            return result;
        }
    }

    public class QueueReaderRunable extends QueueAbs implements Callable<Integer> {

        public QueueReaderRunable(ContentQueue queue) {
            super(queue);
        }

        @Override
        public Integer call() throws Exception {
            Integer result;
            if ((result = queue.poll()) != null) {
                System.out.println(this + "，poll：" + result);
                SleepUtils.sleepSecond(3);
            }
            return result;
        }
    }

    public class QueueReaderValueRunable implements Callable<Integer> {

        int value;

        public QueueReaderValueRunable(int value) {
            this.value = value;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println(this + "，value：" + value);
            SleepUtils.sleepMilliSecond(500);
            return value;
        }
    }
}