package com.cqx.common.model.filter.action;

import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.model.filter.IDataFilterCall;

import java.io.Closeable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * IDataFilterAction
 *
 * @author chenqixu
 */
public interface IDataFilterAction<T extends IDataFilterBean> extends Closeable {

    void init(Map<String, ?> param, IDataFilterCall<T> iDataFilterCall, Class<T> tClass);

    void put(T dataBean, long dataBean_seconds) throws Exception;

    void dealData(long first, String firstValue);

    long getWaterLine();

    void setWaterLine(AtomicLong waterLine);

    long queryMinWaterLine();

    boolean dataUpdateTimeIsNull();

    long getDataUpdateTimeFirstKey();

    long getDataUpdateTimeLastKey();

    String getDataUpdateTimeFirstVale();

    String getDataUpdateTimeLastVale();

    int getDataUpdateTimeSize();

    List<T> poll(long timeOut);

    @Override
    void close();

    class MapKeyComparator implements Comparator<Long> {

        @Override
        public int compare(Long l1, Long l2) {
            return l1.compareTo(l2);
        }
    }
}
