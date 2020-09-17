package com.newland.bi.jkreport.scan;

import com.cqx.common.utils.hdfs.HdfsTool;
import com.newland.bi.jkreport.bean.HdfsLSBean;
import com.newland.bi.jkreport.bean.HdfsLSResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ScanUtil
 *
 * @author chenqixu
 */
public class ScanUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScanUtil.class);
    private String scanPath;
    private List<Integer> paramIndexList;
    private List<String> paramList;
    private String dateFormat;

    public ScanUtil(String scanPath) {
        this.scanPath = scanPath;
        init();
    }

    /**
     * 扫描路径处理
     */
    private void init() {
        paramIndexList = new ArrayList<>();
        paramList = new ArrayList<>();
        //按[切割，找到]，把内容截取出来
        String[] arr = scanPath.split("/", -1);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith("[")) {
                int index = arr[i].indexOf("]");
                if (index > 0) {
                    String param = arr[i].substring(1, index);
                    if (param.startsWith("date:")) {
                        dateFormat = param.replace("date:", "");
                        logger.info("dateFormat：{}", dateFormat);
                    }
                    paramList.add(param);
                    paramIndexList.add(i);
                    logger.info("i：{}，param：{}", i, param);
                    scanPath = scanPath.replace("[" + param + "]", "*");
                }
            }
        }
        logger.info("scanPath：{}", scanPath);
    }

    /**
     * 扫描
     *
     * @param hdfsTool
     * @param hdfsLSResult
     * @throws IOException
     */
    public void scan(HdfsTool hdfsTool, HdfsLSResult hdfsLSResult) throws IOException {
        //扫描
        for (String path : hdfsTool.lsPath(scanPath)) {
            //对扫描出来的路径进行切割
            String[] ls_arr = path.split("/", -1);
            HdfsLSBean hdfsLSBean = new HdfsLSBean();
            for (int index = 0; index < paramIndexList.size(); index++) {
                String value = ls_arr[paramIndexList.get(index)];
                String param = paramList.get(index);
                if (param.startsWith("date")) {
                    hdfsLSBean.setDate(value);
                    hdfsLSBean.setDateFormat(dateFormat);
                } else if (param.startsWith("type")) {
                    hdfsLSBean.setType(value);
                } else if (param.startsWith("content")) {
                    hdfsLSBean.setContent(path);
                }
            }
            logger.info("path：{}，{}", path, hdfsLSBean.toString());
            hdfsLSResult.addSource(hdfsLSBean);
        }
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
