package com.bussiness.bi.bigdata.excel;

import com.bussiness.bi.bigdata.bean.ADBExcelBean;
import com.bussiness.bi.bigdata.excel.MultipleExecTool;
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
            // 全量修复
//            multipleExecTool.saveToFile(toolconfigPath
//                    , MultipleExecTool.MULTIPLE_EXEC_FILE_NAME
//                    , multipleExecTool.getMultipleExec(adbExcelBean)
//                    , table_name);

            // 单线程模式
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

    /**
     * 校验ogg的schema
     *
     * @throws Exception
     */
    @Test
    public void all_CHECK_OGG_SCHEMA_FILE_NAME() throws Exception {
        for (ADBExcelBean adbExcelBean : multipleExecTool.run(path)) {
            String table_name = adbExcelBean.getAdb_table_name().replace("rl_", "");
            multipleExecTool.saveToFile(toolconfigPath
                    , MultipleExecTool.CHECK_OGG_SCHEMA_FILE_NAME
                    , multipleExecTool.getOggSchemaCheck(adbExcelBean)
                    , table_name);
        }
    }

    /**
     * 更新ogg的schema
     *
     * @throws Exception
     */
    @Test
    public void all_UPDATE_OGG_FLAT_SCHEMA_FILE_NAME() throws Exception {
        for (ADBExcelBean adbExcelBean : multipleExecTool.run(path)) {
            String table_name = adbExcelBean.getAdb_table_name().replace("rl_", "");
            multipleExecTool.saveToFile(toolconfigPath
                    , MultipleExecTool.UPDATE_OGG_FLAT_SCHEMA_FILE_NAME
                    , multipleExecTool.getUpdateOggFlatSchema(adbExcelBean)
                    , table_name);
        }
    }

    /**
     * 增量修复
     *
     * @throws Exception
     */
    @Test
    public void all_MULTIPLE_EXEC_REPAIR() throws Exception {
        for (ADBExcelBean adbExcelBean : multipleExecTool.run(path)) {
            multipleExecTool.saveToFile(toolconfigPath
                    , MultipleExecTool.MULTIPLE_EXEC_REPAIR_FILE_NAME
                    , multipleExecTool.getMultipleExecRepair(adbExcelBean)
                    , adbExcelBean.getAdb_table_name());
        }
    }

    /**
     * 全量修复
     *
     * @throws Exception
     */
    @Test
    public void all_MULTIPLE_EXEC() throws Exception {
        for (ADBExcelBean adbExcelBean : multipleExecTool.run(path)) {
            multipleExecTool.saveToFile(toolconfigPath
                    , MultipleExecTool.MULTIPLE_EXEC_FILE_NAME
                    , multipleExecTool.getMultipleExec(adbExcelBean)
                    , adbExcelBean.getAdb_table_name());
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

    @Test
    public void rl_user_additional_info() {
        adbExcelBean = multipleExecTool.run(path, "rl_user_additional_info");
    }

    @Test
    public void rl_user_odfw_export_info() {
        adbExcelBean = multipleExecTool.run(path, "rl_user_odfw_export_info");
    }

    @Test
    public void rl_broadband_users() {
        adbExcelBean = multipleExecTool.run(path, "rl_broadband_users");
    }

    @Test
    public void rl_itv_users() {
        adbExcelBean = multipleExecTool.run(path, "rl_itv_users");
    }

    @Test
    public void rl_users() {
        adbExcelBean = multipleExecTool.run(path, "rl_users");
    }

    @Test
    public void rl_user_product() {
        adbExcelBean = multipleExecTool.run(path, "rl_user_product");
    }
}