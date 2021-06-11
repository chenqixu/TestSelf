package com.newland.bi.bigdata.excel;

import com.newland.bi.bigdata.bean.ADBExcelBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultipleExecToolTest {
    private MultipleExecTool multipleExecTool;
    private ADBExcelBean adbExcelBean;
    private String path = "d:\\Work\\CVS\\BI\\系统文档\\EDC业务\\应用层\\"
            + "实时应用\\消息中心\\实时同步ADB\\实时同步ADB-详细设计说明书.xlsm";

    @Before
    public void setUp() throws Exception {
        multipleExecTool = new MultipleExecTool();
    }

    @After
    public void tearDown() throws Exception {
        if (adbExcelBean != null) {
            multipleExecTool.printMultipleExec(adbExcelBean);
            multipleExecTool.printKafkaToJdbcMixed(adbExcelBean);
        }
    }

    @Test
    public void rl_un_common_flow() {
        adbExcelBean = multipleExecTool.run(path, "rl_un_common_flow");
    }

    @Test
    public void rl_cus_name_record_info() {
        adbExcelBean = multipleExecTool.run(path, "rl_cus_name_record_info");
    }

    @Test
    public void rl_broadband_mop_manager() {
        adbExcelBean = multipleExecTool.run(path, "rl_broadband_mop_manager");
    }
}