package com.cqx.write;

import com.cqx.common.utils.string.StringUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 限流控制
 *
 * @author chenqixu
 */
public class FlowControl {

    /**
     * 默认流量控制到1秒
     */
    public static final long INTERVAL_TIME = 1000l;
    /**
     * 默认每秒80MB
     */
    public static final long MAX_BYTE_SIZE = 80 * 1024 * 1024l;
    private static final Logger logger = LoggerFactory.getLogger(FlowControl.class);
    private long startTime;
    private long msg_length;

    private FlowControl() {
        init();
    }

    public static FlowControl builder() {
        return new FlowControl();
    }

    private void init() {
        setStartTime();
        initMsg_length();
    }

    /**
     * 打印byte长度
     *
     * @param msg
     * @return
     */
    public long getMsgLength(String msg) {
        if (StringUtil.isEmpty(msg)) throw new NullPointerException("msg is null.");
        return msg.getBytes().length;
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void setStartTime() {
        startTime = currentTimeMillis();
    }

    public long getLeftTime() {
        return currentTimeMillis() - startTime;
    }

    private void initMsg_length() {
        this.msg_length = 0l;
    }

    private void addMsg_length(long msg_length) {
        this.msg_length += msg_length;
    }

    public long getMsg_length() {
        return msg_length;
    }

    public void setMsg_length(long msg_length) {
        this.msg_length = msg_length;
    }

    /**
     * 以某个时间点为基点，获取的下一个时间点去和基点进行比较，如果超过间隔时间就重新计算
     *
     * @param msg
     * @return
     */
    public void add(String msg) {
        long data_length = getMsgLength(msg);
        /**
         * 超过间隔，重新设置基点时间
         */
        long leftTime = getLeftTime();
        if (leftTime > INTERVAL_TIME) {
            //超过间隔，需要初始化
            init();
        } else {
            addMsg_length(data_length);
        }
        /**
         * 超过流量，需要休眠剩余时间
         */
        long sleep = 0;
        if (this.msg_length > MAX_BYTE_SIZE) {
            //leftTime > INTERVAL_TIME条件已经在上面过滤掉了
            sleep = INTERVAL_TIME - leftTime;
            //需要限流休眠
            if (sleep > 0)
                SleepUtil.sleepMilliSecond(sleep);
            //限流后需要初始化
            init();
        }
        logger.info("data_length：{}，leftTime：{}，getMsg_length：{}，sleep：{}", data_length, leftTime, getMsg_length(), sleep);
    }
}
