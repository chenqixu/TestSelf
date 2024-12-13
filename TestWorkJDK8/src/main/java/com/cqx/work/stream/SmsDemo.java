package com.cqx.work.stream;

import com.cqx.common.utils.system.SleepUtil;

import java.util.concurrent.CompletableFuture;

/**
 * TODO
 *
 * @author chenqixu
 */
public class SmsDemo {

    public static void main(String[] args) {
        CompletableFuture<String> orgFuture = CompletableFuture.supplyAsync(
                ()->{
                    while (true) {
                        SleepUtil.sleepMilliSecond(1000L);
                        System.out.println("sleep.");
                    }
                }
        );

        orgFuture.thenApplyAsync(x->{
            throw new RuntimeException("test exception.");
        }).exceptionally(e->{
            System.out.println("submitResp异步写入redis失败：");
            return null;
        });


        orgFuture.join();
    }
}
