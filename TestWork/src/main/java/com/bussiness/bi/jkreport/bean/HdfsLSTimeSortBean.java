package com.bussiness.bi.jkreport.bean;

import com.bussiness.bi.bigdata.time.TimeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HdfsLSTimeSortBean
 *
 * @author chenqixu
 */
public class HdfsLSTimeSortBean implements Comparable<HdfsLSTimeSortBean> {
    private String date;
    private List<HdfsLSBean> hdfsLSBeanList;
    private String dateFormat;

    public HdfsLSTimeSortBean(String date, List<HdfsLSBean> hdfsLSBeanList, String dateFormat) {
        this.date = date;
        this.hdfsLSBeanList = hdfsLSBeanList;
        this.dateFormat = dateFormat;
    }

    public static List<HdfsLSTimeSortBean> mapToListAndSort(Map<String, List<HdfsLSBean>> listMap, String dateFormat) {
        List<HdfsLSTimeSortBean> hdfsLSTimeSortBeanList = new ArrayList<>();
        for (Map.Entry<String, List<HdfsLSBean>> entry : listMap.entrySet()) {
            hdfsLSTimeSortBeanList.add(new HdfsLSTimeSortBean(entry.getKey(), entry.getValue(), dateFormat));
        }
        Collections.sort(hdfsLSTimeSortBeanList);//排序
        return hdfsLSTimeSortBeanList;
    }

    @Override
    public int compareTo(HdfsLSTimeSortBean o) {
        //比较规则
        return TimeHelper.timeComparison(this.date, o.date, this.dateFormat);
    }

    public List<HdfsLSBean> getHdfsLSBeanList() {
        return hdfsLSBeanList;
    }
}
