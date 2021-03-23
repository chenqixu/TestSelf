package com.cqx.io;

import org.junit.Test;

public class MyPipelineTest {

    @Test
    public void request() {
        MyPipeline pipeline = new MyPipeline();
        pipeline.addFirst(new TestHandler1());//添加handler1
        pipeline.addFirst(new TestHandler2());//添加handler2
        for (int i = 0; i < 10; i++) {//提交多个任务
            pipeline.Request("hello" + i);
        }
    }

    class TestHandler1 implements Handler {
        @Override
        public void channelRead(HandlerContext ctx, Object msg) {
            String result = msg + "-handler1";//在字符串后面加特定字符串
            System.out.println(result);
            ctx.write(result);//写入操作，这个操作是必须的，相当于将结果传递给下一个handler
        }
    }

    class TestHandler2 implements Handler {
        @Override
        public void channelRead(HandlerContext ctx, Object msg) {
            String result = msg + "-handler2";//在字符串后面加特定字符串
            System.out.println(result);
            ctx.write(result);//写入操作，这个操作是必须的，相当于将结果传递给下一个handler
        }
    }
}