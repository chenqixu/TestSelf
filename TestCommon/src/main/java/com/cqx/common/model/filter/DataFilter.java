package com.cqx.common.model.filter;

import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
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
    private ConcurrentHashMap<Long, List<T>> dataMap = new ConcurrentHashMap<>();
    private ConcurrentSkipListMap<Long, String> dataUpdateTime = new ConcurrentSkipListMap<>(new MapKeyComparator());
    private AtomicBoolean isFirst = new AtomicBoolean(true);
    private AtomicLong waterLine;
    private DealDataQueueRunnable runnable;
    private Thread thread;
    private long timeOut;//默认30秒
    private IDataFilterCall<T> iDataFilterCall;

    public DataFilter(IDataFilterCall<T> iDataFilterCall) {
        this(iDataFilterCall, 30000);
    }

    public DataFilter(IDataFilterCall<T> iDataFilterCall, long timeOut) {
        this.iDataFilterCall = iDataFilterCall;
        this.timeOut = timeOut;
        runnable = new DealDataQueueRunnable();
        thread = new Thread(runnable);
        thread.start();
        logger.info("过滤器启动……");
    }

    public void close() {
        logger.info("准备停止过滤器……");
        if (thread != null && runnable != null) {
            runnable.stop();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void add(T dataBean) {
        try {
            long dataBean_seconds = dataBean.getFormatSecond_time();
            if (isFirst.getAndSet(false)) {
                waterLine = new AtomicLong(dataBean_seconds - 1000);
                put(dataBean, dataBean_seconds);
                logger.debug("【数据添加】首次添加 {}，设置水位线：{}", dataBean, waterLine.get());
            } else {
                if (dataBean_seconds <= waterLine.get()) {
                    logger.warn("【数据添加】抛弃：{} {}，低于水位线：{} {}！", dataBean, dataBean_seconds,
                            TimeUtil.formatTime(waterLine.get()), waterLine.get());
                } else {
                    put(dataBean, dataBean_seconds);
                    logger.debug("【数据添加】添加 {}", dataBean);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("【数据添加】添加数据异常！数据：%s", dataBean), e);
        }
    }

    private void put(T dataBean, long dataBean_seconds) throws ParseException {
        String key = dataBean.getFormatSecond();
        List<T> dataBeans = dataMap.get(dataBean_seconds);
        if (dataBeans == null) {
            dataBeans = new ArrayList<>();
            dataMap.put(dataBean_seconds, dataBeans);
        }
        dataBeans.add(dataBean);
        String dataUpdateTimeKey = dataUpdateTime.get(dataBean_seconds);
        if (dataUpdateTimeKey == null) dataUpdateTime.put(dataBean_seconds, key);
    }

    private void dealData(long first, String firstValue) {
        //更新水位线
        waterLine.set(first);
        List<T> dataBeans = dataMap.get(first);
        logger.info("【处理】更新水位线 [{}] {} ms，需要处理的列表大小：{}", firstValue, waterLine.get(), dataBeans.size());
        //需要处理
        dataUpdateTime.remove(first);
        dataMap.remove(first);
        //排序
        Collections.sort(dataBeans);
        //调用回调进行处理
        iDataFilterCall.call(dataBeans);
    }

    public interface IDataFilterCall<T extends IDataFilterBean> {

        void call(List<T> dataBeans);
    }

    static private class MapKeyComparator implements Comparator<Long> {

        @Override
        public int compare(Long l1, Long l2) {
            return l1.compareTo(l2);
        }
    }

    private class DealDataQueueRunnable extends BaseRunable {

        @Override
        public void exec() throws Exception {
            if (dataUpdateTime != null && dataUpdateTime.size() > 0) {
                //判断第一个和最后一个，是否超过时限，如果没有超过，就判断第一个是否超过预设的时限
                long first = dataUpdateTime.firstKey();
                long last = dataUpdateTime.lastKey();
                String firstValue = dataUpdateTime.firstEntry().getValue();
                String lastValue = dataUpdateTime.lastEntry().getValue();
                logger.debug("【水位线判断】第一个元素: {}, 最后一个元素: {}", firstValue, lastValue);
                if (last - first > timeOut) {
                    logger.info("【水位线判断】(last - first) {} > {}", (last - first), timeOut);
                    dealData(first, firstValue);
                } else {
                    long current = System.currentTimeMillis();
                    if (current - first > timeOut) {
                        logger.info("【水位线判断】(current - first) {} > {}", (current - first), timeOut);
                        dealData(first, firstValue);
                    }
                }
            }
            SleepUtil.sleepMilliSecond(500);
        }
    }
}
