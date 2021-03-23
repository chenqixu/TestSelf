package com.cqx.io;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HandlerContext
 *
 * @author chenqixu
 */
public class HandlerContext {
    private ExecutorService executor = Executors.newCachedThreadPool();//线程池
    private Handler handler;
    private HandlerContext next;//下一个context的引用

    public HandlerContext(Handler handler) {
        this.handler = handler;
    }

    public void setNext(HandlerContext ctx) {
        this.next = ctx;
    }

    public void doWork(final Object msg) {//执行任务的时候向线程池提交一个runnable的任务，任务中调用handler
        executor.submit(new Runnable() {
            @Override
            public void run() {
                handler.channelRead(next, msg);//把下一个handler的context穿个handler来实现回调
            }
        });
        //handler.channelRead(next,msg);
    }

    public void write(Object msg) {//这里的write操作是给handler调用的，实际上是一个回调方法，当handler处理完数据之后，调用一下nextcontext.write，此时就把任务传递给下一个handler了。
        doWork(msg);
    }
}
