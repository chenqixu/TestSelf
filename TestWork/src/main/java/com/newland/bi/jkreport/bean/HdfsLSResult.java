package com.newland.bi.jkreport.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * HdfsLSResult
 *
 * @author chenqixu
 */
public class HdfsLSResult {
    private static final Logger logger = LoggerFactory.getLogger(HdfsLSResult.class);
    private List<HdfsLSBean> sourceList;
    private Map<String, List<HdfsLSBean>> typeMap;
    private Map<String, Map<String, List<HdfsLSBean>>> typeDateMap;
    private Map<String, Map<String, HdfsLSCheck>> typeDateCheckMap;
    private String exclusionKey;
    private String filterKey;

    public HdfsLSResult() {
        clean();
    }

    /**
     * 设置单个文件处理完成标志
     *
     * @param exclusionKey
     */
    public void setExclusionKey(String exclusionKey) {
        this.exclusionKey = exclusionKey;
    }

    /**
     * 设置周期处理完成标志
     *
     * @param filterKey
     */
    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    /**
     * 增加要处理的数据源
     *
     * @param hdfsLSBean
     */
    public void addSource(HdfsLSBean hdfsLSBean) {
        sourceList.add(hdfsLSBean);
    }

    /**
     * 按类型分组
     */
    public void sourceToType() {
        for (HdfsLSBean hdfsLSBean : sourceList) {
            List<HdfsLSBean> typeList = typeMap.get(hdfsLSBean.getType());
            if (typeList == null) {
                typeList = new ArrayList<>();
                typeMap.put(hdfsLSBean.getType(), typeList);
            }
            typeList.add(hdfsLSBean);
        }
        for (Map.Entry<String, List<HdfsLSBean>> entry : typeMap.entrySet()) {
            logger.info("key：{}，value.size：{}", entry.getKey(), entry.getValue().size());
        }
    }

    /**
     * 按时间分组
     */
    public void typeToDate() {
        //按类型循环
        for (Map.Entry<String, List<HdfsLSBean>> entry : typeMap.entrySet()) {
            String key = entry.getKey();
            List<HdfsLSBean> value = entry.getValue();
            Map<String, List<HdfsLSBean>> dateMap = typeDateMap.get(key);
            if (dateMap == null) {
                dateMap = new HashMap<>();
                typeDateMap.put(key, dateMap);
            }
            //同类型下进行时间分组
            for (HdfsLSBean hdfsLSBean : value) {
                List<HdfsLSBean> dateList = dateMap.get(hdfsLSBean.getDate());
                if (dateList == null) {
                    dateList = new ArrayList<>();
                    dateMap.put(hdfsLSBean.getDate(), dateList);
                }
                dateList.add(hdfsLSBean);
            }
        }
    }

    /**
     * 排除不能处理的文件
     */
    public void exclusion() {
        for (Map<String, List<HdfsLSBean>> entry : typeDateMap.values()) {
            //筛选出exclusionKey和filterKey
            exclusion(entry.entrySet().iterator());
        }
    }

    /**
     * 排除不能处理的文件
     *
     * @param entryIterator
     */
    private void exclusion(Iterator<Map.Entry<String, List<HdfsLSBean>>> entryIterator) {
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<HdfsLSBean>> entryDate = entryIterator.next();
            String key = entryDate.getKey();
            logger.info("key：{}，value：{}", key, entryDate.getValue());
            boolean isExclusion = false;
            boolean isFilter = false;
            Iterator<HdfsLSBean> hdfsLSBeanIterator = entryDate.getValue().iterator();
            while (hdfsLSBeanIterator.hasNext()) {
                HdfsLSBean hdfsLSBean = hdfsLSBeanIterator.next();
                if (hdfsLSBean.getContent().contains(exclusionKey)) isExclusion = true;
                if (hdfsLSBean.getContent().contains(filterKey)) {
                    isFilter = true;
                    hdfsLSBeanIterator.remove();
                }
            }
            if (isExclusion) {
                entryIterator.remove();
                continue;
            }
            if (!isFilter) entryIterator.remove();
        }
    }

    /**
     * 给剩余要处理的文件增加检查项
     */
    public void addCheck() {
        //按类型循环
        for (Map.Entry<String, Map<String, List<HdfsLSBean>>> type_entry : getTypeDateMap().entrySet()) {
            String type_key = type_entry.getKey();
            Map<String, List<HdfsLSBean>> type_value = type_entry.getValue();
            //取出某个类型的时间关系
            Map<String, HdfsLSCheck> dateCheck = typeDateCheckMap.get(type_key);
            if (dateCheck == null) {
                dateCheck = new HashMap<>();
                typeDateCheckMap.put(type_key, dateCheck);
            }
            //按时间循环
            for (Map.Entry<String, List<HdfsLSBean>> cycle_entry : type_value.entrySet()) {
                String cycle_key = cycle_entry.getKey();
                List<HdfsLSBean> cycle_value = cycle_entry.getValue();
                //取出某个类型某个时间的关系
                HdfsLSCheck hdfsLSCheck = dateCheck.get(cycle_key);
                if (hdfsLSCheck == null) {
                    hdfsLSCheck = new HdfsLSCheck(type_key, cycle_key);
                    dateCheck.put(cycle_key, hdfsLSCheck);
                }
                //循环时间下的所有对象
                for (HdfsLSBean hdfsLSBean : cycle_value) {
                    //计数
                    hdfsLSCheck.increment();
                    //设置触发对象
                    hdfsLSBean.setHdfsLSCheck(hdfsLSCheck);
                }
            }
        }
    }

    public Map<String, Map<String, List<HdfsLSBean>>> getTypeDateMap() {
        return typeDateMap;
    }

    public Map<String, List<HdfsLSBean>> mergeMap() {
        Map<String, List<HdfsLSBean>> resultMap = new HashMap<>();
        for (Map<String, List<HdfsLSBean>> entry : getTypeDateMap().values()) {
            resultMap.putAll(entry);
        }
        return resultMap;
    }

    public int addQueue(BlockingQueue<FastFailureTask> scanQueue, String dataForamt) {
        int cnt = 0;
        //按时间排序
        for (HdfsLSTimeSortBean hdfsLSTimeSortBean : HdfsLSTimeSortBean.mapToListAndSort(mergeMap(), dataForamt)) {
            //加入扫描队列
            scanQueue.addAll(hdfsLSTimeSortBean.getHdfsLSBeanList());
            cnt = cnt + hdfsLSTimeSortBean.getHdfsLSBeanList().size();
        }
        return cnt;
    }

    public void clean() {
        sourceList = new ArrayList<>();
        typeMap = new HashMap<>();
        typeDateMap = new HashMap<>();
        typeDateCheckMap = new HashMap<>();
    }

    public interface FastFailureTask {
        String getTaskName();
    }
}
