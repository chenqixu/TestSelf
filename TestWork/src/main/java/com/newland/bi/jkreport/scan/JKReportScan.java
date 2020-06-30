package com.newland.bi.jkreport.scan;

import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.newland.bi.jkreport.bean.HdfsLSResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JKReportScan
 *
 * @author chenqixu
 */
public class JKReportScan {

    private static final Logger logger = LoggerFactory.getLogger(JKReportScan.class);
    private static final String conf = "d:\\tmp\\etc\\hadoop\\conf75\\";
    private HdfsTool hdfsTool;
    private HdfsLSResult hdfsLSResult;
    private ScanUtil scanUtil;

    public void init() throws IOException {
        HdfsTool.setHadoopUser("edc_base");
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(conf, hdfsBean);
        hdfsLSResult = new HdfsLSResult();
        hdfsLSResult.setExclusionKey(".complete");
        hdfsLSResult.setFilterKey(".ok");
    }

    public void scan(String scan_path) throws IOException {
        scanUtil = new ScanUtil(scan_path);
        hdfsLSResult.clean();
        scanUtil.scan(hdfsTool, hdfsLSResult);
        hdfsLSResult.sourceToType();
        hdfsLSResult.typeToDate();
//        hdfsLSResult.sourceToDate();
        hdfsLSResult.exclusion();
//        logger.info("TypeDateMap：{}", hdfsLSResult.getTypeDateMap());
        logger.info("DateMap：{}", hdfsLSResult.getDateMap());
        logger.info("mergeMap：{}", hdfsLSResult.mergeMap());
    }

    public void close() throws IOException {
        hdfsTool.closeFileSystem();
    }
}
