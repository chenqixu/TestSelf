package com.bussiness.bi.bigdata.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.util.Bytes;

@SuppressWarnings({ "deprecation", "unused" })
public class HbaseConnection {
	public static final int POOL_SIZE = 15;	
	public static final String ZQ_MASTER75 = "node78,node81,master75";
	public static final String ZQ_EDC01 = "edc01,edc02,edc03";
	
	public void conn(){
		long starttime = new Date().getTime();
		Configuration conf = HBaseConfiguration.create();
	    // 以下4个配置设置仅供开发测试
		conf.set("hbase.zookeeper.quorum", ZQ_MASTER75); //设置zookeeper地址
		conf.set("hbase.zookeeper.property.clientPort", "2181"); //设置zookeeper端口
		conf.set("hbase.regionserver.lease.period", "1000");
		conf.set("hbase.client.scanner.timeout.period", "1000");
	    //创建Hbase的连接池
		HTablePool tablePool = new HTablePool(conf, POOL_SIZE);
		String tablename = "analytics_demo";
		tablePool.getTable(Bytes.toBytes(tablename));
	}
	
	public void getConn(){
		
	}
	
	public List<String> getHBaseTableList() {
		long starttime = new Date().getTime();
        List<String> tableList = new ArrayList<String>();
        try {
            Configuration config=HBaseConfiguration.create();
            config.set("hbase.zookeeper.quorum", ZQ_MASTER75); //设置zookeeper地址
            config.set("hbase.zookeeper.property.clientPort", "2181"); //设置zookeeper端口
            config.set("hbase.regionserver.lease.period", "1000");
            config.set("hbase.client.scanner.timeout.period", "1000");
            Connection connection = ConnectionFactory.createConnection(config);
            System.out.println("[createConnection]"+(new Date().getTime()-starttime));
            HBaseAdmin admin = (HBaseAdmin) connection.getAdmin();
            System.out.println("[getAdmin]"+(new Date().getTime()-starttime));
            HTableDescriptor[] tableDescriptors = admin.listTables();
            for (HTableDescriptor tableDescriptor:tableDescriptors) {
                tableList.add(tableDescriptor.getNameAsString());
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableList;
    }
	
	public static void main(String[] args) throws Exception {
		long starttime = new Date().getTime();
		System.out.println("[start]"+new Date());
//		new HbaseConnection().conn();
		new HbaseConnection().getHBaseTableList();
		System.out.println("[end]"+(new Date().getTime()-starttime));		
	}
}
