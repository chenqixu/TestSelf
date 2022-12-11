package com.cqx.common.utils.hdfs;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * hive 工具类
 *
 * @author chenqixu
 */
public class HiveTool {
    private static final Logger logger = LoggerFactory.getLogger(HiveTool.class);

    public void metaStoreDemo() throws TException {
        System.setProperty("java.security.krb5.conf", "d:\\tmp\\etc\\hadoop\\confhw\\krb5.conf");
//        System.setProperty("sun.security.krb5.debug", "true");
//        System.setProperty("HADOOP_JAAS_DEBUG", "true");

        HiveConf hiveConf = new HiveConf();
        hiveConf.addResource(new Path("d:\\tmp\\etc\\hadoop\\confhw\\hdfs-site.xml"));
        hiveConf.addResource(new Path("d:\\tmp\\etc\\hadoop\\confhw\\core-site.xml"));
        hiveConf.addResource(new Path("d:\\tmp\\etc\\hadoop\\confhw\\hivemetastore-site.xml"));
        hiveConf.addResource(new Path("d:\\tmp\\etc\\hadoop\\confhw\\hive-site.xml"));
        hiveConf.set("hadoop.security.authentication", "kerberos");
        hiveConf.set("hive.metastore.execute.setugi", "true");
        hiveConf.set("hive.security.authorization.enabled", "false");
        hiveConf.set("hive.metastore.sasl.enabled", "true");
        hiveConf.set("hive.metastore.kerberos.principal", "hive/hadoop.hadoop.com@HADOOP.COM");
        hiveConf.set("hive.server2.authentication.kerberos.principal", "hive/hadoop.hadoop.com@HADOOP.COM");
        UserGroupInformation.setConfiguration(hiveConf);
        try {
            UserGroupInformation.loginUserFromKeytab("yz_newland@HADOOP.COM"
                    , "d:\\tmp\\etc\\keytab\\yz_newland.keytab");
        } catch (IOException e1) {
            logger.error(e1.getMessage(), e1);
        }

        try (HiveMetaStoreClient client = new HiveMetaStoreClient(hiveConf)) {
            // 获取所有数据库信息
            for (String _dbName : client.getAllDatabases()) {
                logger.info("[dbName] {}", _dbName);
                // 获取数据库所有表信息
                if (_dbName.equals("default")) {
                    printAllTableInfo(client, _dbName);
                }
            }
        }
    }

    private void printAllTableInfo(HiveMetaStoreClient client, String _dbName) throws TException {
        List<String> tablesList = client.getAllTables(_dbName);
        logger.info("[dbName] {} 所有的表:  ", _dbName);
        for (String tableName : tablesList) {
            // 获取表信息
            logger.info("{}.{} 表信息: ", _dbName, tableName);
            Table table = client.getTable(_dbName, tableName);
            List<FieldSchema> fieldSchemaList = table.getSd().getCols();
            for (FieldSchema schema : fieldSchemaList) {
                logger.info("字段: {}, 类型: {}", schema.getName(), schema.getType());
            }
        }
    }
}
