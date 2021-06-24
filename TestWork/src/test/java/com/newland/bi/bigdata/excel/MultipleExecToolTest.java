package com.newland.bi.bigdata.excel;

import com.cqx.common.utils.file.FileUtil;
import com.newland.bi.bigdata.bean.ADBExcelBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultipleExecToolTest {
    private MultipleExecTool multipleExecTool;
    private ADBExcelBean adbExcelBean;
    private String path = "d:\\Work\\CVS\\BI\\系统文档\\EDC业务\\应用层\\"
            + "实时应用\\消息中心\\实时同步ADB\\实时同步ADB-详细设计说明书.xlsm";
    private String toolconfigPath = "d:\\Work\\实时\\ADB\\KafkaToAdb\\config\\20210609-config\\toolconfig\\";
    private String configPath = "d:\\Work\\实时\\ADB\\KafkaToAdb\\config\\20210609-config\\config\\";

    @Before
    public void setUp() throws Exception {
        multipleExecTool = new MultipleExecTool();
    }

    @After
    public void tearDown() throws Exception {
        if (adbExcelBean != null) {
            String table_name = adbExcelBean.getAdb_table_name().replace("rl_", "");
            multipleExecTool.saveToFile(toolconfigPath
                    , MultipleExecTool.MULTIPLE_EXEC_FILE_NAME
                    , multipleExecTool.getMultipleExec(adbExcelBean)
                    , table_name);
            multipleExecTool.saveToFile(configPath
                    , MultipleExecTool.KAFKA_SINGLE_PARTITION_SYNC_FILE_NAME
                    , multipleExecTool.getKafkaSinglePartitionSync(adbExcelBean)
                    , table_name);
//            multipleExecTool.printMultipleExec(adbExcelBean);
//            multipleExecTool.printKafkaToJdbcMixed(adbExcelBean);
//            multipleExecTool.printOggSchemaCheck(adbExcelBean);
//            multipleExecTool.printKafkaSinglePartitionSync(adbExcelBean);
        }
    }

    @Test
    public void all() throws Exception {
        FileUtil fileUtil = new FileUtil();
        String name = "d:\\Work\\实时\\ADB\\KafkaToAdb\\config\\20210609-config\\toolconfig\\check_ogg_schema_%s.yaml";
        for (ADBExcelBean adbExcelBean : multipleExecTool.run(path)) {
            try {
                String fileName = String.format(name, adbExcelBean.getAdb_table_name());
                System.out.println(fileName);
                fileUtil.createFile(fileName);
                fileUtil.write(multipleExecTool.getOggSchemaCheck(adbExcelBean));
            } finally {
                fileUtil.closeWrite();
            }
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

    @Test
    public void rl_res_piece_type_goods_consume() {
        adbExcelBean = multipleExecTool.run(path, "rl_res_piece_type_goods_consume");
    }

    @Test
    public void rl_res_chnl_storage_sales() {
        adbExcelBean = multipleExecTool.run(path, "rl_res_chnl_storage_sales");
    }
}