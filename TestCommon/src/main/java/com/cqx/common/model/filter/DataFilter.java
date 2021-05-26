package com.cqx.common.model.filter;

import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.model.filter.action.FileAction;
import com.cqx.common.model.filter.action.IDataFilterAction;
import com.cqx.common.model.filter.action.MemoryAction;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据过滤器
 * <pre>
 *     秒级
 *     默认30秒超时
 * </pre>
 *
 * @author chenqixu
 */
public class DataFilter<T extends IDataFilterBean> {
    private static final Logger logger = LoggerFactory.getLogger(DataFilter.class);
    private AtomicBoolean isFirst = new AtomicBoolean(true);
    private AtomicBoolean isFirstWaterLineSet = new AtomicBoolean(true);
    private long firstTime;
    private DealDataQueueRunnable runnable;
    private Thread thread;
    private long timeOut;//默认30秒
    // 最后个周期是否切换
    private AtomicBoolean lastCycleNotChange = new AtomicBoolean(false);
    // 处理动作
    private IDataFilterAction<T> iDataFilterAction;

    public DataFilter(IDataFilterCall<T> iDataFilterCall) {
        this(iDataFilterCall, 30000);
    }

    public DataFilter(IDataFilterCall<T> iDataFilterCall, long timeOut) {
        this(new HashMap<>(), iDataFilterCall, timeOut, DataFilterActionEnum.MEMORY_ACTION);
    }

    public DataFilter(Map<String, ?> param, IDataFilterCall<T> iDataFilterCall, DataFilterActionEnum actionEnum) {
        this(param, iDataFilterCall, 30000, actionEnum);
    }

    public DataFilter(Map<String, ?> param, IDataFilterCall<T> iDataFilterCall, long timeOut, DataFilterActionEnum actionEnum) {
        this.timeOut = timeOut;
        runnable = new DealDataQueueRunnable();
        thread = new Thread(runnable);
        thread.start();
        switch (actionEnum) {
            case MEMORY_ACTION:
                iDataFilterAction = new MemoryAction();
                break;
            case FILE_ACTION:
                iDataFilterAction = new FileAction();
                break;
            default:
                break;
        }
        iDataFilterAction.init(param, iDataFilterCall);
        logger.info("【过滤器】过滤器启动……");
    }

    public void close() {
        logger.info("【过滤器】准备停止过滤器……");
        if (thread != null && runnable != null) {
            runnable.stop();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (iDataFilterAction != null) iDataFilterAction.close();
    }

    /**
     * 周期是否允许切换
     *
     * @param isStop
     */
    public void cycleChange(boolean isStop) {
        lastCycleNotChange.set(isStop);
    }

    /**
     * 往队列增加数据
     *
     * @param dataBean
     * @return 抛数据会返回1，不抛返回0
     */
    public int add(T dataBean) {
        try {
            long dataBean_seconds = dataBean.getFormatSecond_time();
            if (isFirst.getAndSet(false)) {
                firstTime = System.currentTimeMillis();
                iDataFilterAction.put(dataBean, dataBean_seconds);
                logger.debug("【过滤器】【数据添加】首次添加 {}，设置firstTime：{}", dataBean, firstTime);
            } else if (isFirstWaterLineSet.get()) {// 暂时无水位线
                iDataFilterAction.put(dataBean, dataBean_seconds);
                logger.debug("【过滤器】【数据添加】暂时无水位线，数据：{}", dataBean);
                // 已经间隔了timeOut，可以设置最小值为第一次水位线了
                if (System.currentTimeMillis() - firstTime > timeOut) {
                    isFirstWaterLineSet.set(false);
                    // 设置队列中最小的时间作为水位线
                    iDataFilterAction.setWaterLine(new AtomicLong(iDataFilterAction.queryMinWaterLine()));
                    logger.debug("【过滤器】【数据添加】数据：{}，满足条件System.currentTimeMillis() - firstTime > timeOut，设置水位线：{}",
                            dataBean, iDataFilterAction.getWaterLine());
                }
            } else {
                if (dataBean_seconds <= iDataFilterAction.getWaterLine()) {
                    logger.warn("【过滤器】【数据添加】抛弃：{} {}，低于水位线：{} {}！", dataBean, dataBean_seconds,
                            TimeUtil.formatTime(iDataFilterAction.getWaterLine()), iDataFilterAction.getWaterLine());
                    return 1;
                } else {
                    iDataFilterAction.put(dataBean, dataBean_seconds);
                    logger.debug("【过滤器】【数据添加】添加 {}", dataBean);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("【过滤器】【数据添加】添加数据异常！数据：%s", dataBean), e);
        }
        return 0;
    }

    public List<T> poll(long timeOut) {
        return iDataFilterAction.poll(timeOut);
    }

    private class DealDataQueueRunnable extends BaseRunable {

        @Override
        public void exec() throws Exception {
            if (!isFirstWaterLineSet.get() && iDataFilterAction.dataUpdateTimeIsNull()) {
                //判断第一个和最后一个，是否超过时限，如果没有超过，就判断第一个是否超过预设的时限
                long first = iDataFilterAction.getDataUpdateTimeFirstKey();
                long last = iDataFilterAction.getDataUpdateTimeLastKey();
                String firstValue = iDataFilterAction.getDataUpdateTimeFirstVale();
                String lastValue = iDataFilterAction.getDataUpdateTimeLastVale();
                logger.debug("【过滤器】【水位线判断】第一个元素: {}, 最后一个元素: {}", firstValue, lastValue);
                if (last - first > timeOut) {
                    logger.info("【过滤器】【水位线判断】(last - first) {} > {}", (last - first), timeOut);
                    iDataFilterAction.dealData(first, firstValue);
                } else {
                    if (lastCycleNotChange.get()) {
                        logger.warn("【过滤器】上游满了，最后个周期不允许切换，第一个元素: {}, 最后一个元素: {}", firstValue, lastValue);
                        // 有可能存在周期队列中的数据小于timeOut，但由于数据太大，上游堵塞的情况，这样就卡着不动了
                    } else {
                        long current = System.currentTimeMillis();
                        if (current - first > timeOut) {
                            logger.info("【过滤器】【水位线判断】(current - first) {} > {}", (current - first), timeOut);
                            iDataFilterAction.dealData(first, firstValue);
                        }
                    }
                }
            } else if (lastCycleNotChange.get() && isFirstWaterLineSet.get()) {
                // 如果限流，并且水位线未初始化过，立刻触发水位线初始化
                // 已经间隔了timeOut，可以设置最小值为第一次水位线了
                if (System.currentTimeMillis() - firstTime > timeOut) {
                    isFirstWaterLineSet.set(false);
                    // 设置队列中最小的时间作为水位线
                    iDataFilterAction.setWaterLine(new AtomicLong(iDataFilterAction.queryMinWaterLine()));
                    logger.info("【过滤器】【限流】设置水位线，满足条件System.currentTimeMillis() - firstTime > timeOut，设置水位线：{}",
                            iDataFilterAction.getWaterLine());
                }
            }
            SleepUtil.sleepMilliSecond(500);
        }

        @Override
        public void lastExec() throws Exception {
            logger.info("【过滤器】【停止】剩余 {} 个周期数据待处理。", iDataFilterAction.getDataUpdateTimeSize());
            //循环dataUpdateTime，有序的
//            for (Map.Entry<Long, String> entry : dataUpdateTime.entrySet()) {
//                List<T> dataBeans = dataMap.get(entry.getKey());
//                logger.info("【过滤器】【停止】处理 {}，大小：{}。", entry.getValue(), dataBeans.size());
//                //排序
//                Collections.sort(dataBeans);
//                //调用回调进行处理
//                iDataFilterCall.call(dataBeans);
//            }
            logger.info("【过滤器】停止过滤器完成。");
        }
    }
}
