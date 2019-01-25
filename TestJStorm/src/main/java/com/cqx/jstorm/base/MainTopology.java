package com.cqx.jstorm.base;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;
import com.cqx.jstorm.bolt.CommonBolt;
import com.cqx.jstorm.spout.CommonSpout;
import com.cqx.jstorm.util.AppConst;
import com.cqx.jstorm.util.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * 启动入口类
 *
 * @author chenqixu
 */
public class MainTopology {
    public static void main(String[] args) throws Exception {
        Yaml yaml;
        InputStream is = null;
        Map<?, ?> map;
        AppConst appConst = new AppConst();
        try {
            // 加载yaml配置文件
            yaml = new Yaml();
            is = MainTopology.class.getClassLoader().getResourceAsStream("conf/config.yaml");
            map = yaml.loadAs(is, Map.class);
            is.close();

            // 解析
            appConst.parserParam(map);

            // 创建topology的生成器
            TopologyBuilder builder = new TopologyBuilder();
            // 创建Spout
            SpoutDeclarer spout = builder.setSpout(appConst.getSpoutBean().getName(),
                    new CommonSpout(appConst.getSpoutBean().getName()),
                    appConst.getSpoutBean().getParall());
            // 创建bolt
            BoltDeclarer totalBolt = builder.setBolt(appConst.getBoltBean().getName(),
                    new CommonBolt(appConst.getBoltBean().getName()),
                    appConst.getBoltBean().getParall())
                    .shuffleGrouping(appConst.getSpoutBean().getName());
            // 配置
            Config conf = new Config();
            // 允许debug
            conf.setDebug(true);
            // 表示整个topology将使用几个worker
            conf.setNumWorkers(appConst.getTopologyBean().getWorker_num());
            // 设置ack个数
            conf.setNumAckers(appConst.getTopologyBean().getAck_num());

            // 本地模式提交
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(appConst.getTopologyBean().getName(), conf, builder.createTopology());
            Utils.sleep(5000);
            cluster.shutdown();
        } finally {
            if (is != null)
                is.close();
        }
    }
}
