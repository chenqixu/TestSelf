package com.cqx.common.model.filter.action;

import com.cqx.common.bean.model.IDataFilterBean;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * BaseAction
 *
 * @author chenqixu
 */
public abstract class BaseAction<T extends IDataFilterBean> implements IDataFilterAction<T> {
    ConcurrentSkipListMap<Long, String> dataUpdateTime = new ConcurrentSkipListMap<>(new MapKeyComparator());
    AtomicLong waterLine;

    @Override
    public long getWaterLine() {
        return waterLine.get();
    }

    @Override
    public void setWaterLine(AtomicLong waterLine) {
        this.waterLine = waterLine;
    }

    @Override
    public long queryMinWaterLine() {
        return dataUpdateTime.firstKey();
    }

    @Override
    public boolean dataUpdateTimeIsNull() {
        return dataUpdateTime != null && dataUpdateTime.size() > 0;
    }

    @Override
    public long getDataUpdateTimeFirstKey() {
        return dataUpdateTime.firstKey();
    }

    @Override
    public long getDataUpdateTimeLastKey() {
        return dataUpdateTime.lastKey();
    }

    @Override
    public String getDataUpdateTimeFirstVale() {
        return dataUpdateTime.firstEntry().getValue();
    }

    @Override
    public String getDataUpdateTimeLastVale() {
        return dataUpdateTime.lastEntry().getValue();
    }

    @Override
    public int getDataUpdateTimeSize() {
        return dataUpdateTime.size();
    }

    @Override
    public List<T> poll(long timeOut) {
        return null;
    }
}
