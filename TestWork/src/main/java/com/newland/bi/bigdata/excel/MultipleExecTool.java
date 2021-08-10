package com.newland.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.file.FileUtil;
import com.newland.bi.bigdata.bean.ADBExcelBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * ADB配置文件的生成工具
 *
 * @author chenqixu
 */
public class MultipleExecTool {
    /**
     * 全量修复文件名，带rl_，全名
     */
    public static final String MULTIPLE_EXEC_FILE_NAME = "multiple_exec_%s.yaml";
    /**
     * 单线程文件名
     */
    public static final String KAFKA_SINGLE_PARTITION_SYNC_FILE_NAME = "kafka_single_partition_sync_%s.config.yaml";
    /**
     * 检查ogg和数据库中的schema是否一致的文件名
     */
    public static final String CHECK_OGG_SCHEMA_FILE_NAME = "check_ogg_schema_%s.yaml";
    /**
     * 更新ogg和flat的schema的文件名
     */
    public static final String UPDATE_OGG_FLAT_SCHEMA_FILE_NAME = "update_ogg_flat_schema_%s.yaml ";
    /**
     * 增量修复文件名，带rl_，全名
     */
    public static final String MULTIPLE_EXEC_REPAIR_FILE_NAME = "multiple_exec_repair_%s.yaml";

    private static final Logger logger = LoggerFactory.getLogger(MultipleExecTool.class);
    /**
     * 全量修复
     */
    private StringBuilder multiple_exec = new StringBuilder();
    /**
     * 多线程模式
     */
    private StringBuilder kafka_to_jdbc_mixed = new StringBuilder();
    /**
     * 单线程模式
     */
    private StringBuilder kafka_single_partition_sync = new StringBuilder();
    /**
     * 检查ogg和数据库中的schema是否一致
     */
    private StringBuilder ogg_schema_check = new StringBuilder();
    /**
     * 更新ogg和flat的schema
     */
    private StringBuilder update_ogg_flat_schema = new StringBuilder();
    /**
     * 增量修复
     */
    private StringBuilder multiple_exec_repair = new StringBuilder();

    public MultipleExecTool() {
        // 全量修复
        multiple_exec.append("multiple_exec:\n" +
                "  - name: truncate_table\n" +
                "    type: jdbc\n" +
                "  - name: clean_file\n" +
                "    type: exec_shell\n" +
                "  - name: oracle_to_local\n" +
                "    type: oracle_to_file\n" +
                "  - name: copy\n" +
                "    type: postgresql_copy\n" +
                "  - name: analyze\n" +
                "    type: jdbc\n" +
                "\n" +
                "truncate_table:\n" +
                "  sql:\n" +
                "    - \"truncate table %s\"\n" +// adb_table
                "  auto_commit: true\n" +
                "  dbbeans:\n" +
                "    - name: srcBean\n" +
                "      user_name: \"label_core\"\n" +
                "      pass_word: \"admin\"\n" +
                "      tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "      dbType: \"POSTGRESQL\"\n" +
                "clean_file:\n" +
                "  cmd:\n" +
                "    - \"sh /home/jstorm/jstormtasks/tool/clean_file.sh %s\"\n" +// adb_table
                "oracle_to_local:\n" +
                "  tab_fields: \"%s\"\n" +// fields，逗号分隔
                "  tab_name: \"%s\"\n" +// 源表，带数据库链
                "  src_where: \n" +
                "  file_name: \"/home/jstorm/jstormtasks/realtime-jstorm/data/%s/data\"\n" +// adb_table
                "  thread_num: 9\n" +
                "  dbbeans:\n" +
                "    - name: srcBean\n" +
                "      user_name: \"expbi\"\n" +
                "      pass_word: \"e218cekltynb4\"\n" +
                "      tns: \"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.46.216.32)(PORT = 1521)))(CONNECT_DATA =(SERVICE_NAME = jfhd)))\"\n" +
                "      dbType: \"ORACLE\"\n" +
                "copy:\n" +
                "  copy_file_path: \"/home/jstorm/jstormtasks/realtime-jstorm/data/%s/\"\n" +// adb_table
                "  copy_file_key: \"data\"\n" +
                "  copy_file_endwith: \"txt\"\n" +
                "  table_name: \"%s\"\n" +// adb_table
                "  dbbeans:\n" +
                "    - name: srcBean\n" +
                "      user_name: \"label_core\"\n" +
                "      pass_word: \"admin\"\n" +
                "      tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "      dbType: \"POSTGRESQL\"\n" +
                "analyze:\n" +
                "  sql:\n" +
                "    - \"vacuum analyze %s\"\n" +// adb_table
                "  auto_commit: true\n" +
                "  dbbeans:\n" +
                "    - name: srcBean\n" +
                "      user_name: \"label_core\"\n" +
                "      pass_word: \"admin\"\n" +
                "      tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "      dbType: \"POSTGRESQL\"\n");

        kafka_to_jdbc_mixed.append("jstorm:\n" +
                "  nimbus_host: 10.45.179.119\n" +
                "  nimbus_thrift_port: 17627\n" +
                "  storm_zookeeper_servers:\n" +
                "    - \"10.48.134.152\"\n" +
                "    - \"10.48.134.153\"\n" +
                "    - \"10.48.134.154\"\n" +
                "  storm_zookeeper_port: 2184\n" +
                "  storm_zookeeper_root: /streampaas-6-jstorm\n" +
                "\n" +
                "topology:\n" +
                "  worker_num: 3\n" +
                "  ack_num: 0\n" +
                "  worker_memory: 4294967296\n" +
                "  cpu_slotNum: 100\n" +
                "  jvm_options:\n" +
                "  name: kafka_to_jdbc_mixed_%s\n" +// adb表名，去掉rl_
                "  ip: 10.45.179.119\n" +
                "\n" +
                "spout:\n" +
                "  - name: KafkaMixedAllConsumerSpout\n" +
                "    packagename: com.nl.realtime.kafkatojdbc.spout\n" +
                "    parall: 6\n" +
                "  - name: JDBCAnalyzeSpout\n" +
                "    packagename: com.nl.realtime.kafkatojdbc.spout\n" +
                "    parall: 1\n" +
                "\n" +
                "bolt:\n" +
                "  - name: JDBCMixedFilterOperationBolt\n" +
                "    packagename: com.nl.realtime.kafkatojdbc.bolt\n" +
                "    parall: 6\n" +
                "    groupingcode: FIELDSGROUPING\n" +
                "    componentId: KafkaMixedAllConsumerSpout\n" +
                "    streamId: \n" +
                "    fields:\n" +
                "      - pks\n" +
                "\n" +
                "param:\n" +
                "  table_name: \"%s\"\n" +// adb表名
                "  primary_keys: \"%s\"\n" +// ogg pks
                "  send_pks: \"%s\"\n" +// 下发分组，默认是ogg pks
                "  send_fields: \"%s\"\n" +// 所有字段
                "  send_fields_type: \"%s\"\n" +// 所有字段类型
                "  submit_timeout: 30000\n" +
                "  submit_cnt: 4000\n" +
                "  queue_limit: 50000\n" +
                "  data_filter_limit: 120000\n" +
                "  #redis_conf: \"10.48.134.140:6380,10.48.134.141:6380,10.48.134.142:6380,10.48.134.143:6380,10.48.134.144:6380,10.48.134.145:6380,10.48.134.146:6380,10.48.134.147:6380,10.48.134.148:6380,10.48.134.149:6380\"\n" +
                "  analyze_hour: 15\n" +
                "  is_merge_info: true\n" +
                "  KafkaMixedAllConsumerSpout:\n" +
                "    kafkaconf.bootstrap.servers: \"10.48.137.217:9092,10.48.137.241:9092,10.48.137.242:9092,10.48.137.243:9092,10.48.137.244:9092\"\n" +
                "    kafkaconf.key.deserializer: \"org.apache.kafka.common.serialization.StringDeserializer\"\n" +
                "    kafkaconf.value.deserializer: \"org.apache.kafka.common.serialization.ByteArrayDeserializer\"\n" +
                "    kafkaconf.security.protocol: \"SASL_PLAINTEXT\"\n" +
                "    kafkaconf.sasl.mechanism: \"PLAIN\"\n" +
                "    kafkaconf.group.id: \"ADB_TEST_T1\"\n" +
                "    kafkaconf.enable.auto.commit: \"true\"\n" +
                "    kafkaconf.fetch.min.bytes: \"52428800\"\n" +
                "    kafkaconf.max.poll.records: \"12000\"\n" +
                "    kafkaconf.newland.kafka_username: admin\n" +
                "    kafkaconf.newland.kafka_password: admin-secret\n" +
                "#    kafkaconf.newland.consumer.mode: fromEnd\n" +
                "    schema_url: \"http://10.48.137.217:8080/SchemaService/getSchema?t=\"\n" +
                "    topic: %s\n" +// 扁平化话题
                "  dbbeans:\n" +
                "    - name: adbBean\n" +
                "      user_name: \"label_core\"\n" +
                "      pass_word: \"admin\"\n" +
                "      tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "      dbType: \"POSTGRESQL\"\n");

        // 检查ogg和数据库中的schema是否一致
        ogg_schema_check.append("ogg_topic_name: \"%s\"\n" +
                "file_path: \"/ogg/dirdef/\"\n" +
                "file_name: \"%s\"\n" +
                "dbbeans:\n" +
                "  - name: srcBean\n" +
                "    user_name: \"edc_cfg\"\n" +
                "    pass_word: \"#cF2-0wFgl\"\n" +
                "    tns: \"jdbc:oracle:thin:@10.48.236.215:1521/edc_cfg_pri\"\n" +
                "    dbType: \"ORACLE\"\n" +
                "ftpbeans:\n" +
                "  - name: oggSftp\n" +
                "    type: SFTP\n" +
                "    user_name: \"gg\"\n" +
                "    pass_word: \"z+xESe0P\"\n" +
                "    host: \"10.48.137.234\"\n" +
                "    port: 22");

        kafka_single_partition_sync.append("jstorm:\n" +
                "  nimbus_host: 10.45.179.119\n" +
                "  nimbus_thrift_port: 17627\n" +
                "  storm_zookeeper_servers:\n" +
                "    - \"10.48.134.152\"\n" +
                "    - \"10.48.134.153\"\n" +
                "    - \"10.48.134.154\"\n" +
                "  storm_zookeeper_port: 2184\n" +
                "  storm_zookeeper_root: /streampaas-6-jstorm\n" +
                "\n" +
                "topology:\n" +
                "  worker_num: 1\n" +
                "  ack_num: 0\n" +
                "  worker_memory: 4294967296\n" +
                "  cpu_slotNum: 100\n" +
                "  jvm_options:\n" +
                "  name: kafka_single_partition_sync_%s\n" +// adb表名，去掉rl_
                "  ip: 10.45.179.119\n" +
                "\n" +
                "spout:\n" +
                "  - name: KafkaSinglePartitionSyncADB\n" +
                "    packagename: com.nl.realtime.kafkatojdbc.spout\n" +
                "    parall: 1\n" +
                "  - name: JDBCAnalyzeSpout\n" +
                "    packagename: com.nl.realtime.kafkatojdbc.spout\n" +
                "    parall: 1\n" +
                "\n" +
                "param:\n" +
                "  table_name: \"%s\"\n" +// adb表名
                "  primary_keys: \"%s\"\n" +// pks
                "  send_pks: \"%s\"\n" +// send_pks
//                "  send_fields: \"%s\"\n" +// send_fields
//                "  send_fields_type: \"%s\"\n" +// send_fields_type
                "  analyze_hour: 3\n" +
//                "  is_throw_err_data: false\n" +
                "  redis_conf: \"10.48.134.140:6380,10.48.134.141:6380,10.48.134.142:6380,10.48.134.143:6380,10.48.134.144:6380,10.48.134.145:6380,10.48.134.146:6380,10.48.134.147:6380,10.48.134.148:6380,10.48.134.149:6380\"\n" +
                "  KafkaSinglePartitionSyncADB:\n" +
//                "    kafkaconf.bootstrap.servers: \"10.48.137.217:9092,10.48.137.241:9092,10.48.137.242:9092,10.48.137.243:9092,10.48.137.244:9092\"\n" +
                "    kafkaconf.bootstrap.servers: \"10.45.177.116:9092,10.45.177.117:9092,10.45.177.118:9092,10.45.177.119:9092,10.45.177.120:9092,10.45.177.121:9092,10.45.177.122:9092,10.45.177.123:9092,10.45.177.124:9092,10.45.177.125:9092\"\n" +
                "    kafkaconf.key.deserializer: \"org.apache.kafka.common.serialization.StringDeserializer\"\n" +
                "    kafkaconf.value.deserializer: \"org.apache.kafka.common.serialization.ByteArrayDeserializer\"\n" +
                "    kafkaconf.security.protocol: \"SASL_PLAINTEXT\"\n" +
                "    kafkaconf.sasl.mechanism: \"PLAIN\"\n" +
                "    kafkaconf.group.id: \"adb_single\"\n" +
                "    kafkaconf.enable.auto.commit: \"false\"\n" +
                "    kafkaconf.fetch.min.bytes: \"52428800\"\n" +
                "    kafkaconf.max.poll.records: \"4000\"\n" +
                "    kafkaconf.heartbeat.interval.ms: \"20000\"\n" +
                "    kafkaconf.session.timeout.ms: \"60000\"\n" +
//                "    kafkaconf.newland.kafka_username: admin\n" +
//                "    kafkaconf.newland.kafka_password: admin-secret\n" +
                "    kafkaconf.newland.kafka_username: \"newland\"\n" +
                "    kafkaconf.newland.kafka_password: \"Bi-Newland\"\n" +
                "    kafkaconf.newland.consumer.mode: fromTime\n" +
                "    kafkaconf.newland.consumer.fromTime: \"2021-07-30 15:30:00\"\n" +
                "    schema_url: \"http://10.48.137.217:8080/SchemaService/getSchema?t=\"\n" +
                "    ogg.topic: \"%s\"\n" +// ogg_topic
                "    flat.topic: \"%s\"\n" +// flat_topic
                "  dbbeans:\n" +
                "    - name: adbBean\n" +
                "      user_name: \"label_core\"\n" +
                "      pass_word: \"admin\"\n" +
                "      tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "      dbType: \"POSTGRESQL\"\n" +
                "    - name: srcBean\n" +
                "      user_name: \"edc_cfg\"\n" +
                "      pass_word: \"#cF2-0wFgl\"\n" +
                "      tns: \"jdbc:oracle:thin:@10.48.236.215:1521/edc_cfg_pri\"\n" +
                "      dbType: \"ORACLE\"");

        update_ogg_flat_schema.append("file_path: \"/ogg/dirdef/\"\n" +
                "file_name: \"%s\"\n" + // avsc定义名称
                "ogg_topic_name: \"%s\"\n" + // ogg话题
                "flat_topic_name: \"%s\"\n" + // flat话题
                "avsc_type: SFTP\n" +
                "dbbeans: \n" +
                "  - name: srcBean\n" +
                "    user_name: \"edc_cfg\"\n" +
                "    pass_word: \"#cF2-0wFgl\"\n" +
                "    tns: \"jdbc:oracle:thin:@10.48.236.215:1521/edc_cfg_pri\"\n" +
                "    dbType: \"ORACLE\"\n" +
                "ftpbeans:\n" +
                "  - name: oggSftp\n" +
                "    type: SFTP\n" +
                "    user_name: \"gg\"\n" +
                "    pass_word: \"z+xESe0P\"\n" +
                "    host: \"10.48.137.234\"\n" +
                "    port: 22");

        // 增量修复
        multiple_exec_repair.append("multiple_exec:\n" +
                "  - name: data_sync\n" +
                "    type: data_sync\n" +
                "  - name: analyze\n" +
                "    type: jdbc\n" +
                "\n" +
                "data_sync:\n" +
                "     tab_fields: \"%s\"\n" +// 需要同步的表字段
                "     src_tab_name: \"%s\"\n" +// 源表(经分互动)
                "     dst_tab_name: \"%s\"\n" +// 目标表(adb)
                "     src_where: \"create_time between to_timestamp('20210724122000','yyyymmddhh24miss') and to_timestamp('20210724130059','yyyymmddhh24miss')\"\n" +
                "     dst_submit: 2000\n" +
                "     dst_thread_num: 1\n" +
                "     sync_type: \"MERGE_INTO_UPDATE\"\n" +
                "     pks: \"%s\"\n" +// 主键
                "     dbbeans:\n" +
                "       - name: srcBean\n" +
                "         user_name: \"expbi\"\n" +
                "         pass_word: \"e218cekltynb4\"\n" +
                "         tns: \"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.46.216.32)(PORT = 1521)))(CONNECT_DATA =(SERVICE_NAME = jfhd)))\"\n" +
                "         dbType: \"ORACLE\"\n" +
                "       - name: dstBean\n" +
                "         user_name: \"label_core\"\n" +
                "         pass_word: \"admin\"\n" +
                "         tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "         dbType: \"POSTGRESQL\"\n" +
                "analyze:\n" +
                "  sql:\n" +
                "    - \"VACUUM analyze %s\"\n" +// 目标表(adb)
                "  auto_commit: true\n" +
                "  dbbeans:\n" +
                "    - name: srcBean\n" +
                "      user_name: \"label_core\"\n" +
                "      pass_word: \"admin\"\n" +
                "      tns: \"jdbc:postgresql://10.45.189.15:3432/label_core\"\n" +
                "      dbType: \"POSTGRESQL\"");
    }

    public void printMultipleExec(ADBExcelBean adbExcelBean) {
        logger.info("\n{}", getMultipleExec(adbExcelBean));
    }

    public void printKafkaToJdbcMixed(ADBExcelBean adbExcelBean) {
        logger.info("\n{}", getKafkaToJdbcMixed(adbExcelBean));
    }

    public void printOggSchemaCheck(ADBExcelBean adbExcelBean) {
        logger.info("\n{}", getOggSchemaCheck(adbExcelBean));
    }

    public void printKafkaSinglePartitionSync(ADBExcelBean adbExcelBean) {
        logger.info("\n{}", getKafkaSinglePartitionSync(adbExcelBean));
    }

    public void printUpdateOggFlatSchema(ADBExcelBean adbExcelBean) {
        logger.info("\n{}", getUpdateOggFlatSchema(adbExcelBean));
    }

    public void printMultipleExecRepair(ADBExcelBean adbExcelBean) {
        logger.info("\n{}", getMultipleExecRepair(adbExcelBean));
    }

    public String getKafkaToJdbcMixed(ADBExcelBean adbExcelBean) {
        // adb表名，去掉rl_
        // adb表名
        // ogg pks
        // 下发分组，默认是ogg pks
        // 所有字段
        // 所有字段类型
        // 扁平化话题
        return String.format(kafka_to_jdbc_mixed.toString()
                , adbExcelBean.getAdb_table_name().replace("rl_", "")
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getOgg_pks()
                , adbExcelBean.getOgg_pks()
                , adbExcelBean.getFieldsWithSeparator()
                , adbExcelBean.getFieldsTypeWithSeparator()
                , adbExcelBean.getFlat_topic()
        );
    }

    public String getMultipleExec(ADBExcelBean adbExcelBean) {
        // adb_table
        // adb_table
        // fields，逗号分隔
        // 源表，带数据库链
        // adb_table
        // adb_table
        // adb_table
        // adb_table
        return String.format(multiple_exec.toString()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getFieldsWithSeparator()
                , adbExcelBean.getSource_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
        );
    }

    public String getKafkaSinglePartitionSync(ADBExcelBean adbExcelBean) {
        // adb表名，去掉rl_
        // adb表名
        // pks
        // send_pks
        // send_fields，新版不需要了
        // send_fields_type，新版不需要了
        // ogg_topic
        // flat_topic
        return String.format(kafka_single_partition_sync.toString()
                , adbExcelBean.getAdb_table_name().replace("rl_", "")
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getOgg_pks()
                , adbExcelBean.getOgg_pks()
//                , adbExcelBean.getFieldsWithSeparator()
//                , adbExcelBean.getFieldsTypeWithSeparator()
                , adbExcelBean.getOgg_topic()
                , adbExcelBean.getFlat_topic()
        );
    }

    public String getOggSchemaCheck(ADBExcelBean adbExcelBean) {
        // ogg话题
        // ogg schema文件名称
        return String.format(ogg_schema_check.toString()
                , adbExcelBean.getOgg_topic()
                , adbExcelBean.getOgg_asvc_name()
        );
    }

    public String getUpdateOggFlatSchema(ADBExcelBean adbExcelBean) {
        // avsc定义名称
        // ogg话题
        // flat话题
        return String.format(update_ogg_flat_schema.toString()
                , adbExcelBean.getOgg_asvc_name()
                , adbExcelBean.getOgg_topic()
                , adbExcelBean.getFlat_topic()
        );
    }

    public String getMultipleExecRepair(ADBExcelBean adbExcelBean) {
        // 需要同步的表字段
        // 源表(经分互动)
        // 目标表(adb)
        // 主键
        // 目标表(adb)
        return String.format(multiple_exec_repair.toString()
                , adbExcelBean.getFieldsWithSeparator()
                , adbExcelBean.getSource_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getOgg_pks()
                , adbExcelBean.getAdb_table_name()
        );
    }

    public ADBExcelBean run(String path, String sheetName) {
        ADBExcelBean adbExcelBean = new ADBExcelBean();
        List<ExcelSheetList> list;
        ExcelUtils eu = new ExcelUtils();
        try {
            list = eu.readExcel(path);
            if (list != null) {
                // 循环sheet
                for (ExcelSheetList excelSheetList : list) {
                    if (sheetName.equals(excelSheetList.getSheetName())) {
                        boolean isFieldStart = false;
                        boolean isFieldEnd = false;
                        List<List<String>> excelSheetListSheetList = excelSheetList.getSheetList();
                        for (List<String> excelSheetListSheet : excelSheetListSheetList) {
                            logger.debug("{}", excelSheetListSheet);
                            if (excelSheetListSheet != null && excelSheetListSheet.size() > 0 && excelSheetListSheet.get(0) != null) {
                                switch (excelSheetListSheet.get(0)) {
                                    case "源表名称":
                                        logger.debug("源表名称：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setSource_table_name(excelSheetListSheet.get(1));
                                        break;
                                    case "原始清单话题":
                                        logger.debug("原始清单话题：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setOgg_topic(excelSheetListSheet.get(1));
                                        break;
                                    case "扁平化话题":
                                        logger.debug("扁平化话题：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setFlat_topic(excelSheetListSheet.get(1));
                                        break;
                                    case "adb表名":
                                        logger.debug("adb表名：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setAdb_table_name(excelSheetListSheet.get(1));
                                        break;
                                    case "OGG中的pks":
                                        logger.debug("OGG中的pks：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setOgg_pks(excelSheetListSheet.get(1));
                                        break;
                                    case "OGG的AVSC":
                                        logger.debug("OGG的AVSC：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setOgg_asvc_name(excelSheetListSheet.get(1));
                                        break;
                                    case "字段":
                                        isFieldStart = true;
                                        break;
                                    default:
                                        break;
                                }
                            } else if (excelSheetListSheet == null || (excelSheetListSheet.size() > 0 && excelSheetListSheet.get(0) == null)) {
                                if (isFieldStart) isFieldEnd = true;
                            }
                            if (isFieldStart && !isFieldEnd && !"字段".equals(excelSheetListSheet.get(0))) {
                                String type = excelSheetListSheet.get(3);
                                String length = excelSheetListSheet.get(4);
                                String fieldType = null;
                                switch (type) {
                                    case "d":
                                        if (Integer.valueOf(length) > 6) fieldType = "long";
                                        else fieldType = "int";
                                        break;
                                    case "v":
                                        fieldType = "java.lang.String";
                                        break;
                                    case "t":
                                        fieldType = "java.sql.Timestamp";
                                        break;
                                    default:
                                        break;
                                }
                                logger.debug("field：{}，field_type：{}", excelSheetListSheet.get(0), fieldType);
                                adbExcelBean.addField(excelSheetListSheet.get(0));
                                adbExcelBean.addFieldType(fieldType);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return adbExcelBean;
    }

    public List<ADBExcelBean> run(String path) {
        List<ADBExcelBean> adbExcelBeans = new ArrayList<>();
        List<ExcelSheetList> list;
        ExcelUtils eu = new ExcelUtils();
        try {
            list = eu.readExcel(path);
            if (list != null) {
                // 循环sheet
                for (ExcelSheetList excelSheetList : list) {
                    if (excelSheetList.getSheetName().startsWith("rl_")) {
                        ADBExcelBean adbExcelBean = new ADBExcelBean();
                        boolean isFieldStart = false;
                        boolean isFieldEnd = false;
                        List<List<String>> excelSheetListSheetList = excelSheetList.getSheetList();
                        for (List<String> excelSheetListSheet : excelSheetListSheetList) {
                            logger.debug("{}", excelSheetListSheet);
                            if (excelSheetListSheet.size() > 0 && excelSheetListSheet.get(0) != null) {
                                switch (excelSheetListSheet.get(0)) {
                                    case "源表名称":
                                        logger.debug("源表名称：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setSource_table_name(excelSheetListSheet.get(1));
                                        break;
                                    case "原始清单话题":
                                        logger.debug("原始清单话题：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setOgg_topic(excelSheetListSheet.get(1));
                                        break;
                                    case "扁平化话题":
                                        logger.debug("扁平化话题：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setFlat_topic(excelSheetListSheet.get(1));
                                        break;
                                    case "adb表名":
                                        logger.debug("adb表名：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setAdb_table_name(excelSheetListSheet.get(1));
                                        break;
                                    case "OGG中的pks":
                                        logger.debug("OGG中的pks：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setOgg_pks(excelSheetListSheet.get(1));
                                        break;
                                    case "OGG的AVSC":
                                        logger.debug("OGG的AVSC：{}", excelSheetListSheet.get(1));
                                        adbExcelBean.setOgg_asvc_name(excelSheetListSheet.get(1));
                                        break;
                                    case "字段":
                                        isFieldStart = true;
                                        break;
                                    default:
                                        break;
                                }
                            } else if (excelSheetListSheet.size() > 0 && excelSheetListSheet.get(0) == null) {
                                if (isFieldStart) isFieldEnd = true;
                            }
                            if (isFieldStart && !isFieldEnd && excelSheetListSheet.size() > 0 && !"字段".equals(excelSheetListSheet.get(0))) {
                                String type = excelSheetListSheet.get(3);
                                String length = excelSheetListSheet.get(4);
                                String fieldType = null;
                                switch (type) {
                                    case "d":
                                        if (Integer.valueOf(length) > 6) fieldType = "long";
                                        else fieldType = "int";
                                        break;
                                    case "v":
                                        fieldType = "java.lang.String";
                                        break;
                                    case "t":
                                        fieldType = "java.sql.Timestamp";
                                        break;
                                    default:
                                        break;
                                }
                                logger.debug("field：{}，field_type：{}", excelSheetListSheet.get(0), fieldType);
                                adbExcelBean.addField(excelSheetListSheet.get(0));
                                adbExcelBean.addFieldType(fieldType);
                            }
                        }
                        adbExcelBeans.add(adbExcelBean);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return adbExcelBeans;
    }

    /**
     * 保存到文件
     *
     * @param path
     * @param fileName
     * @param value
     * @param adbExcelBean
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void saveToFile(String path, String fileName, String value, String table_name) throws FileNotFoundException, UnsupportedEncodingException {
        FileUtil fileUtil = new FileUtil();
        try {
            String _fileName = FileUtil.endWith(path) + String.format(fileName, table_name);
            logger.info("文件名：{}", _fileName);
            fileUtil.createFile(_fileName);
            fileUtil.write(value);
        } finally {
            fileUtil.closeWrite();
        }
    }
}
