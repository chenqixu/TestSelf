package com.cqx.common.model.filter.action;

import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.model.filter.IDataFilterCall;
import com.cqx.common.utils.file.RAFBean;
import com.cqx.common.utils.file.RAFFileMangerCenter;
import com.cqx.common.utils.file.RAFFileMerge;
import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.ProtoStuffSerializationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileAction
 *
 * @author chenqixu
 */
public class FileAction<T extends IDataFilterBean> extends BaseAction<T> {
    private static final Logger logger = LoggerFactory.getLogger(FileAction.class);
    private ConcurrentHashMap<Long, RAFFileMangerCenter<T>> dataMap = new ConcurrentHashMap<>();
    private IDataFilterCall<T> iDataFilterCall;
    private String filePath;
    private String taskId;
    private RAFFileMerge<T> rafFileMerge;
    private ISerialization<T> iSerialization =
//            new KryoSerializationImpl();
            new ProtoStuffSerializationImpl<>();
    private boolean isMerge = true;

    @Override
    public void init(Map<String, ?> param, IDataFilterCall<T> iDataFilterCall, Class<T> tClass) {
        this.iSerialization.setTClass(tClass);
        this.iDataFilterCall = iDataFilterCall;
        this.filePath = (String) param.get("filePath");
        this.taskId = (String) param.get("taskId");
        String fileName = (String) param.get("fileName");
        long singleFileMaxLength = (Long) param.get("singleFileMaxLength");
        int data_filter_file_MaxNum = (Integer) param.get("data_filter_file_MaxNum");
        try {
            if (isMerge) {
                this.rafFileMerge = new RAFFileMerge<>(iSerialization, filePath, fileName, singleFileMaxLength, data_filter_file_MaxNum);
            }
        } catch (Exception e) {
            throw new RuntimeException("文件创建失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void put(T dataBean, long dataBean_seconds) throws Exception {
        // 取出key
        String key = dataBean.getFormatSecond();
        // 判断是否有文件
        RAFFileMangerCenter<T> file = dataMap.get(dataBean_seconds);
        // 如果对应的数据文件为空则需要创建并加入数据Map
        if (file == null) {
            file = new RAFFileMangerCenter<>(iSerialization, filePath + taskId + dataBean_seconds);
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
        RAFFileMangerCenter<T> file = dataMap.get(first);
        logger.info("【过滤器】【处理】更新水位线 [{}] {} ms，待处理水位线大小：{}",
                firstValue, waterLine.get(), dataUpdateTime.size() - 1);
        // 从数据时间排序器移除本次处理对象
        dataUpdateTime.remove(first);
        // 从数据Map移除本次处理对象
        dataMap.remove(first);
        // 写个文件结束符
        file.writeEndTag();
        if (isMerge) {// 走大文件
            try {
                // 合并数据到大文件，并删除本次处理的文件
                rafFileMerge.merge(file, true);
            } catch (Exception e) {
                throw new RuntimeException("文件合并数据失败：" + e.getMessage(), e);
            }
        } else {// 走小文件，已经不走这条支线了
            // 从头读取
            file.seekToBegin();
            List<T> tList = new ArrayList<>();
            RAFBean<T> rafBean;
            // 读到内存
            while (true) {
                if ((rafBean = file.readDeserialize()) != null) {
                    if (rafBean.isEnd()) {
                        break;
                    } else {
                        tList.add(rafBean.getT());
                    }
                }
            }
            // 文件关闭&删除
            try {
                file.close();
            } catch (IOException e) {
                //
            } finally {
                file.del();
            }
            // 对本次处理对象进行排序
            logger.info("本次读取{}数据记录{}条", file.getFile_name(), tList.size());
            Collections.sort(tList);
            // 调用回调处理本次处理对象
            iDataFilterCall.call(tList);
        }
    }

    @Override
    public List<T> poll(long timeOut) {
        List<T> tmps = rafFileMerge != null ? rafFileMerge.read(timeOut) : null;
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
