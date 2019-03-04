package com.cqx.write;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * 模拟数据写入
 *
 * @author chenqixu
 */
public class WriteDataCallable implements Callable<Integer> {

    private static Logger logger = LoggerFactory.getLogger(WriteDataCallable.class);
    /**
     * 写入批次
     */
    private int batch_cnt = 0;

    private String data = "0123456789你好，我是测试字符。谢谢！abcdefghijklmnopqrstuvwxyz";
    private String datas;

    public WriteDataCallable() {
        datas = getData();
    }

    private String getData() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 60000; i++) {
            sb.append(data);
        }
        return sb.toString();
    }

    private int getRandomTime() {
        Random random = new Random();
        return random.nextInt(100);
    }

    @Override
    public Integer call() throws Exception {
        FlowControl flowControl = FlowControl.builder();
        SleepUtil.sleepMilliSecond(2000);
        while (batch_cnt > 0) {
            //随机休眠
            long randomtime = getRandomTime();
//            logger.info("random：{}", randomtime);
//            SleepUtil.sleepMilliSecond(randomtime);
            //判断是否需要限流
            flowControl.add(datas);
            batch_cnt--;
        }
        return batch_cnt;
    }

    public void setBatch_cnt(int batch_cnt) {
        this.batch_cnt = batch_cnt;
    }
}
