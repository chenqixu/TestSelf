package com.newland.bi.jkreport.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * HdfsLSResult
 *
 * @author chenqixu
 */
public class HdfsLSResult {
    private static final Logger logger = LoggerFactory.getLogger(HdfsLSResult.class);
    private List<HdfsLSBean> sourceList;
    private Map<String, List<HdfsLSBean>> dateMap;
    private Map<String, List<HdfsLSBean>> typeMap;
    private Map<String, Map<String, List<HdfsLSBean>>> typeDateMap;
    private String exclusionKey;
    private String filterKey;

    public HdfsLSResult() {
        clean();
    }

    public void setExclusionKey(String exclusionKey) {
        this.exclusionKey = exclusionKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public void addSource(HdfsLSBean hdfsLSBean) {
        sourceList.add(hdfsLSBean);
    }

    public void sourceToDate() {
        for (HdfsLSBean hdfsLSBean : sourceList) {
            List<HdfsLSBean> typeList = dateMap.get(hdfsLSBean.getDate());
            if (typeList == null) {
                typeList = new ArrayList<>();
                dateMap.put(hdfsLSBean.getDate(), typeList);
            }
            typeList.add(hdfsLSBean);
        }
    }

    public void sourceToType() {
        for (HdfsLSBean hdfsLSBean : sourceList) {
            List<HdfsLSBean> typeList = typeMap.get(hdfsLSBean.getType());
            if (typeList == null) {
                typeList = new ArrayList<>();
                typeMap.put(hdfsLSBean.getType(), typeList);
            }
            typeList.add(hdfsLSBean);
        }
    }

    public void typeToDate() {
        for (Map.Entry<String, List<HdfsLSBean>> entry : typeMap.entrySet()) {
            String key = entry.getKey();
            List<HdfsLSBean> value = entry.getValue();
            Map<String, List<HdfsLSBean>> dateMap = typeDateMap.get(key);
            if (dateMap == null) {
                dateMap = new HashMap<>();
                typeDateMap.put(key, dateMap);
            }
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

    public void exclusion() {
        if (dateMap.size() > 0) {//按时间分
            //筛选出exclusionKey和filterKey
            //这里有个缺陷，不同类型可能有的有.ok，有的没有，这里会被算在一起
            exclusion(dateMap.entrySet().iterator());
        } else if (typeDateMap.size() > 0) {//按类型时间分
            for (Map<String, List<HdfsLSBean>> entry : typeDateMap.values()) {
                //筛选出exclusionKey和filterKey
                exclusion(entry.entrySet().iterator());
            }
        }
    }

    private void exclusion(Iterator<Map.Entry<String, List<HdfsLSBean>>> entryIterator) {
        while (entryIterator.hasNext()) {
            Map.Entry<String, List<HdfsLSBean>> entryDate = entryIterator.next();
            String key = entryDate.getKey();
            logger.info("key：{}", key);
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

    public Map<String, Map<String, List<HdfsLSBean>>> getTypeDateMap() {
        return typeDateMap;
    }

    public Map<String, List<HdfsLSBean>> getDateMap() {
        return dateMap;
    }

    public Map<String, List<HdfsLSBean>> mergeMap() {
        Map<String, List<HdfsLSBean>> resultMap = new HashMap<>();
        for (Map<String, List<HdfsLSBean>> entry : getTypeDateMap().values()) {
            resultMap.putAll(entry);
        }
        return resultMap;
    }

    public void clean() {
        sourceList = new ArrayList<>();
        dateMap = new HashMap<>();
        typeMap = new HashMap<>();
        typeDateMap = new HashMap<>();
    }
}
