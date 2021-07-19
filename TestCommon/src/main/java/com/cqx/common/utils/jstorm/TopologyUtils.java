package com.cqx.common.utils.jstorm;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.utils.NimbusClient;
import backtype.storm.utils.Utils;
import org.apache.thrift.TException;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TopologyUtils
 *
 * @author chenqixu
 */
public class TopologyUtils implements Closeable {
    public static final String STORM_CONF_FILE = "storm.conf.file";
    private NimbusClient client;

    /**
     * 构造，传入storm.yaml
     *
     * @param confFile
     */
    public TopologyUtils(String confFile) {
        System.setProperty(STORM_CONF_FILE, confFile);
        Map conf = Utils.readStormConfig();
        client = NimbusClient.getConfiguredClient(conf);
    }

    /**
     * 通过topologyName获取TopologyInfo
     *
     * @param topologyName
     * @return
     * @throws TException
     */
    public TopologyInfo getTopologyInfoByName(String topologyName) throws TException {
        return client.getClient().getTopologyInfoByName(topologyName);
    }

    /**
     * 通过topologyName获取TopologySummary
     *
     * @param topologyName
     * @return
     * @throws TException
     */
    public TopologySummary getTopologySummaryByName(String topologyName) throws TException {
        return client.getClient().getTopologyInfoByName(topologyName).get_topology();
    }

    /**
     * 获取所有的TopologySummary
     *
     * @return
     * @throws TException
     */
    public List<TopologySummary> getTopologies() throws TException {
        ClusterSummary clusterSummary = client.getClient().getClusterInfo();
        return clusterSummary.get_topologies();
    }

    /**
     * 获取所有的TopologySummary，返回一个Map，通过topologyName进行映射
     *
     * @return
     * @throws TException
     */
    public Map<String, TopologySummary> getTopologiesMap() throws TException {
        Map<String, TopologySummary> map = new HashMap<>();
        ClusterSummary clusterSummary = client.getClient().getClusterInfo();
        for (TopologySummary topologySummary : clusterSummary.get_topologies()) {
            map.put(topologySummary.get_name(), topologySummary);
        }
        return map;
    }

    @Override
    public void close() {
        if (client != null) client.close();
    }
}
