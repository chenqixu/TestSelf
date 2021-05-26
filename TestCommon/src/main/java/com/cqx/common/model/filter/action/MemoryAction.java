package com.cqx.common.model.filter.action;

import com.cqx.common.bean.model.DataBean;
import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.model.filter.IDataFilterCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MapAction
 *
 * @author chenqixu
 */
public class MemoryAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(MemoryAction.class);
    private ConcurrentHashMap<Long, List<IDataFilterBean>> dataMap = new ConcurrentHashMap<>();
    private IDataFilterCall<IDataFilterBean> iDataFilterCall;

    @Override
    public void init(Map param, IDataFilterCall iDataFilterCall) {
        this.iDataFilterCall = iDataFilterCall;
    }

    @Override
    public void put(IDataFilterBean dataBean, long dataBean_seconds) throws Exception {
        // 取出key
        String key = dataBean.getFormatSecond();
        // 从数据Map根据key取出对应数据List
        List<IDataFilterBean> dataBeans = dataMap.get(dataBean_seconds);
        // 如果对应的数据List为空则需要创建并加入数据Map
        if (dataBeans == null) {
            dataBeans = new ArrayList<>();
            dataMap.put(dataBean_seconds, dataBeans);
        }
        // 往数据List添加数据
        dataBeans.add(dataBean);
        // 更新数据时间排序器
        String dataUpdateTimeKey = dataUpdateTime.get(dataBean_seconds);
        if (dataUpdateTimeKey == null) dataUpdateTime.put(dataBean_seconds, key);
    }

    @Override
    public void dealData(long first, String firstValue) {
        // 更新水位线
        waterLine.set(first);
        // 获取本次处理对象
        List<IDataFilterBean> dataBeans = dataMap.get(first);
        logger.info("【过滤器】【处理】更新水位线 [{}] {} ms，需要处理的列表大小：{}，待处理水位线大小：{}",
                firstValue, getWaterLine(), dataBeans.size(), dataUpdateTime.size() - 1);
        // 从数据时间排序器移除本次处理对象
        dataUpdateTime.remove(first);
        // 从数据Map移除本次处理对象
        dataMap.remove(first);
        // 对本次处理对象进行排序
        Collections.sort(dataBeans);
        // 调用回调处理本次处理对象
        iDataFilterCall.call(dataBeans);
    }

    @Override
    public void close() {
    }
}
