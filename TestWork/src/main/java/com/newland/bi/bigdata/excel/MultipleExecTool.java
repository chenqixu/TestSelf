package com.newland.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.newland.bi.bigdata.bean.ADBExcelBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * MultipleExecTool
 *
 * @author chenqixu
 */
public class MultipleExecTool {
    private static final Logger logger = LoggerFactory.getLogger(MultipleExecTool.class);
    private StringBuilder multiple_exec = new StringBuilder();
    private StringBuilder kafka_to_jdbc_mixed = new StringBuilder();

    public MultipleExecTool() {
        multiple_exec.append("\nmultiple_exec:\n" +
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

        kafka_to_jdbc_mixed.append("\njstorm:\n" +
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
    }

    public void printMultipleExec(ADBExcelBean adbExcelBean) {
        // adb_table
        // adb_table
        // fields，逗号分隔
        // 源表，带数据库链
        // adb_table
        // adb_table
        // adb_table
        // adb_table
        logger.info("{}", String.format(multiple_exec.toString()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getFieldsWithSeparator()
                , adbExcelBean.getSource_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getAdb_table_name()
        ));
    }

    public void printKafkaToJdbcMixed(ADBExcelBean adbExcelBean) {
        // adb表名，去掉rl_
        // adb表名
        // ogg pks
        // 下发分组，默认是ogg pks
        // 所有字段
        // 所有字段类型
        // 扁平化话题
        logger.info("{}", String.format(kafka_to_jdbc_mixed.toString()
                , adbExcelBean.getAdb_table_name().replace("rl_", "")
                , adbExcelBean.getAdb_table_name()
                , adbExcelBean.getOgg_pks()
                , adbExcelBean.getOgg_pks()
                , adbExcelBean.getFieldsWithSeparator()
                , adbExcelBean.getFieldsTypeWithSeparator()
                , adbExcelBean.getFlat_topic()
        ));
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
                            if (excelSheetListSheet.get(0) != null) {
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
                                    case "字段":
                                        isFieldStart = true;
                                        break;
                                    default:
                                        break;
                                }
                            } else if (excelSheetListSheet.get(0) == null) {
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
}
