package com.cqx.common.utils.redis.client;

import com.cqx.common.utils.redis.bean.SlotNumAndHostAndPort;
import com.cqx.common.utils.system.SleepUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

import java.util.*;

/**
 * MyJedisCluster
 *
 * @author chenqixu
 */
public class MyJedisCluster extends JedisCluster {
    private static final Logger logger = LoggerFactory.getLogger(MyJedisCluster.class);
    private static final int MASTER_NODE_INDEX = 2;
    private final Map<String, Jedis> nodes = new HashMap<>();
    private final Map<Integer, String> slotsNode = new HashMap<>();

    public MyJedisCluster(Set<HostAndPort> nodes) {
        super(nodes);
        renewCache();
    }

    public MyJedisCluster(Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig) {
        super(nodes, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, poolConfig);
        renewCache();
    }

    public void renewCache() {
        //循环所有节点
        for (JedisPool jedisPool : getClusterNodes().values()) {
            Jedis jedis = jedisPool.getResource();
            try {
                if (renewCache(jedis)) return;
            } catch (Exception e) {
                //null
            }
        }
        throw new RuntimeException("没有可用的redis连接：" + nodes + "，请联系管理员！");
    }

    public boolean renewCache(Jedis jedis) {
        try {
            //发送ping命令，检查Jedis是否存活
            String ping = jedis.ping();
            //存活返回PONG
            if ("PONG".equalsIgnoreCase(ping)) {
                logger.debug("jedis：{}:{}，ping：{}", jedis.getClient().getHost(), jedis.getClient().getPort(), ping);
                //发现Cluster的Slots，并进行分配
                discoverClusterSlots(jedis);
                return true;
            }
        } finally {
            //释放Jedis
            releaseConnection(jedis);
        }
        throw new RuntimeException("没有可用的redis连接：" + jedis + "，请联系管理员！");
    }

    /**
     * 拼接成 host:port
     *
     * @param hnp
     * @return
     */
    private String getNodeKey(HostAndPort hnp) {
        return hnp.getHost() + ":" + hnp.getPort();
    }

    /**
     * 根据起始槽位和结束槽位生成所有槽位列表
     *
     * @param slotInfo
     * @return
     */
    private List<Integer> getAssignedSlotArray(List<Object> slotInfo) {
        List<Integer> slotNums = new ArrayList<>();
        for (int slot = ((Long) slotInfo.get(0)).intValue(); slot <= ((Long) slotInfo.get(1)).intValue(); slot++) {
            slotNums.add(slot);
        }
        return slotNums;
    }

    /**
     * 获取主节点的ip和端口
     *
     * @param hostInfos
     * @return
     */
    private HostAndPort generateHostAndPort(List<Object> hostInfos) {
        return new HostAndPort(SafeEncoder.encode((byte[]) hostInfos.get(0)),
                ((Long) hostInfos.get(1)).intValue());
    }

    /**
     * 填充nodes
     *
     * @param node
     * @return
     */
    private String setupNodeIfNotExist(HostAndPort node) {
        //host:port
        String nodeKey = getNodeKey(node);
        Jedis existingJedis = nodes.get(nodeKey);
        if (existingJedis != null) return nodeKey;

        Jedis nodePool = new Jedis(node.getHost(), node.getPort());
        nodes.put(nodeKey, nodePool);
        return nodeKey;
    }

    /**
     * 发现Cluster的Slots，并进行分配
     *
     * @param jedis
     */
    private void discoverClusterSlots(Jedis jedis) {
        //待分配列表
        List<SlotNumAndHostAndPort> waitDistributionList = getWaitDistributionList(jedis);
        //从待分配列表进行分配操作
        for (SlotNumAndHostAndPort slotNumAndHostAndPort : waitDistributionList) {
            //分配槽位到主节点
            assignSlotsToNode(slotNumAndHostAndPort.getSlotNums(),
                    slotNumAndHostAndPort.getHostAndPort());
        }
    }

    /**
     * 获取待分配列表
     *
     * @param jedis
     * @return
     */
    private List<SlotNumAndHostAndPort> getWaitDistributionList(Jedis jedis) {
        //发送cluster slots命令，获取槽位和ip关系
        //1：起始槽位
        //2：结束槽位
        //3：主节点
        //4：从节点
        List<Object> slots = jedis.clusterSlots();
        //重置
        reset();
        //待分配列表
        List<SlotNumAndHostAndPort> waitDistributionList = new ArrayList<>();
        //循环
        for (Object slotInfoObj : slots) {
            List<Object> slotInfo = (List<Object>) slotInfoObj;
            logger.debug("slotInfo.size()：{}，slotInfo：{}", slotInfo.size(), getSlotInfo(slotInfo));
            //没有从节点，进入下一个循环
            if (slotInfo.size() <= MASTER_NODE_INDEX) {
                continue;
            }
            //根据起始槽位和结束槽位生成所有槽位列表
            List<Integer> slotNums = getAssignedSlotArray(slotInfo);
            //获取主节点的ip端口等信息
            List<Object> hostInfos = (List<Object>) slotInfo.get(MASTER_NODE_INDEX);
            //没有获取到主节点的ip端口信息，进入下一个循环
            if (hostInfos.isEmpty()) {
                continue;
            }
            //此时，我们只需使用主节点，丢弃从节点信息
            /**
             * at this time, we just use master, discard slave information
             * @see redis.clients.jedis.JedisClusterInfoCache#discoverClusterSlots(Jedis jedis)
             */
            //获取主节点的ip和端口
            HostAndPort targetNode = generateHostAndPort(hostInfos);

            //判断主机是否可用
            Jedis tmp = new Jedis(targetNode.getHost(), targetNode.getPort());
            String ping = tmp.ping();
            if (!"PONG".equalsIgnoreCase(ping)) {
                //主机不可用，需要重新发送cluster slots命令，获取槽位和ip关系
                logger.warn("主机不可用，需要重新发送cluster slots命令，获取槽位和ip关系，sleep 5……");
                SleepUtil.sleepSecond(5);
                return getWaitDistributionList(jedis);
            }

            //添加到待分配列表
            waitDistributionList.add(new SlotNumAndHostAndPort(slotNums, targetNode));
        }
        return waitDistributionList;
    }

    /**
     * 分配槽位到主节点
     *
     * @param targetSlots
     * @param targetNode
     */
    private void assignSlotsToNode(List<Integer> targetSlots, HostAndPort targetNode) {
        String nodeKey = setupNodeIfNotExist(targetNode);
        for (Integer slot : targetSlots) {
            slotsNode.put(slot, nodeKey);
        }
    }

    /**
     * 解析cluster slots命令的结果
     *
     * @param slotInfo
     * @return
     */
    private String getSlotInfo(List<Object> slotInfo) {
        StringBuilder sb = new StringBuilder("\r\n");
        int tag = 1;
        for (int j = 0; j < slotInfo.size(); j++) {
            StringBuilder sub_sb = null;
            Object object = slotInfo.get(j);
            if (object.getClass().equals(ArrayList.class)) {
                sub_sb = new StringBuilder();
                List<Object> sub_object = (List<Object>) object;
                int sub_tag = 1;
                //ip端口等信息
                for (int i = 0; i < sub_object.size(); i++) {
                    Object _obj = sub_object.get(i);
                    if (_obj.getClass().isArray() && _obj.getClass().getComponentType().equals(Byte.TYPE)) {
                        byte[] bytes = (byte[]) _obj;
                        _obj = new String(bytes);
                    }
                    sub_sb.append(sub_tag == 1 ? "" : "    ").append(sub_tag++).append(") ").append(_obj)
                            .append((i + 1) == sub_object.size() ? "" : "\r\n");
                }
            }
            sb.append(tag++).append(") ").append(sub_sb != null ? sub_sb.toString() : object)
                    .append((j + 1) == slotInfo.size() ? "" : "\r\n");
        }
        return sb.toString();
    }

    /**
     * 通过key获得对应的槽位
     *
     * @param key
     * @return
     */
    public int getSlot(String key) {
        return JedisClusterCRC16.getSlot(key);
    }

    /**
     * 通过槽位获得对应的Jedis
     *
     * @param slot
     * @return
     */
    public Jedis getConnectionFromSlot(int slot) {
        return nodes.get(getNodeBySlot(slot));
    }

    /**
     * 通过槽位获得对应的Jedis信息
     *
     * @param slot
     * @return
     */
    public String getNodeBySlot(int slot) {
        return slotsNode.get(slot);
    }

    /**
     * 释放Jedis
     *
     * @param connection
     */
    private void releaseConnection(Jedis connection) {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * 重置nodes<br>
     * 重置slotsNode<br>
     * 释放所有Jedis
     */
    public void reset() {
        for (Map.Entry<String, Jedis> entry : nodes.entrySet()) {
            releaseConnection(entry.getValue());
        }
        nodes.clear();
        slotsNode.clear();
    }
}
