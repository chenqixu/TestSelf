package com.cqx.common.model.filter.action;

import com.cqx.common.bean.model.DataBean;
import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.model.filter.IDataFilterCall;
import com.cqx.common.utils.file.RAFFileMangerCenter;
import com.cqx.common.utils.file.RAFFileMerge;
import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.KryoSerializationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileAction
 *
 * @author chenqixu
 */
public class FileAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(FileAction.class);
    private ConcurrentHashMap<Long, RAFFileMangerCenter<IDataFilterBean>> dataMap = new ConcurrentHashMap<>();
    private IDataFilterCall iDataFilterCall;
    private String filePath;
    private RAFFileMerge<DataBean> rafFileMerge;
    private ISerialization<DataBean> iSerialization = new KryoSerializationImpl();

    @Override
    public void init(Map param, IDataFilterCall iDataFilterCall) {
        this.iDataFilterCall = iDataFilterCall;
        this.filePath = (String) param.get("filePath");
        String fileName = (String) param.get("fileName");
        long singleFileMaxLength = (Long) param.get("singleFileMaxLength");
        try {
            this.rafFileMerge = new RAFFileMerge<>(iSerialization, filePath, fileName, singleFileMaxLength);
        } catch (IOException e) {
            throw new RuntimeException("文件创建失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void put(IDataFilterBean dataBean, long dataBean_seconds) throws Exception {
        // 取出key
        String key = dataBean.getFormatSecond();
        // 判断是否有文件
        RAFFileMangerCenter<IDataFilterBean> file = dataMap.get(dataBean_seconds);
        // 如果对应的数据文件为空则需要创建并加入数据Map
        if (file == null) {
            file = new RAFFileMangerCenter(iSerialization, filePath + dataBean_seconds);
            dataMap.put(dataBean_seconds, file);
        }
        // 往数据文件添加数据
        file.write(dataBean);
        // 更新数据时间排序器
        String dataUpdateTimeKey = dataUpdateTime.get(dataBean_seconds);
        if (dataUpdateTimeKey == null) dataUpdateTime.put(dataBean_seconds, key);
    }

    @Override
    public void dealData(long first, String firstValue) {
        // 更新水位线
        waterLine.set(first);
        // 获取本次处理对象
        RAFFileMangerCenter file = dataMap.get(first);
        logger.info("【过滤器】【处理】更新水位线 [{}] {} ms，待处理水位线大小：{}",
                firstValue, waterLine.get(), dataUpdateTime.size() - 1);
        // 从数据时间排序器移除本次处理对象
        dataUpdateTime.remove(first);
        // 从数据Map移除本次处理对象
        dataMap.remove(first);
        // 合并数据到大文件，并删除本次处理的文件
        try {
            rafFileMerge.merge(file, true);
        } catch (IOException e) {
            throw new RuntimeException("文件创建失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List poll(long timeOut) {
        List<DataBean> tmps = rafFileMerge != null ? rafFileMerge.read(timeOut) : null;
        if (tmps != null && tmps.size() > 0) {
            return tmps;
        }
        return null;
    }

    @Override
    public void close() {
        // 清理剩余小文件
        for (RAFFileMangerCenter raf : dataMap.values()) {
            try {
                raf.close();
            } catch (IOException e) {
                //
            }
            logger.info("清理小文件：{}，清理结果：{}", raf.getFile_name(), raf.del());
        }
        // 大文件释放和清理
        if (rafFileMerge != null) rafFileMerge.close();
    }
}
