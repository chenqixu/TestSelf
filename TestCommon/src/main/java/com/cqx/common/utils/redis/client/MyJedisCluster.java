package com.cqx.common.utils.redis.client;

import com.cqx.common.utils.list.TwoWayHashMap;
import com.cqx.common.utils.redis.bean.SlotNumAndHostAndPort;
import com.cqx.common.utils.system.SleepUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.JedisClusterCRC16;
import redis.clients.util.SafeEncoder;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MyJedisCluster
 *
 * @author chenqixu
 */
public class MyJedisCluster extends JedisCluster {
    private static final Logger logger = LoggerFactory.getLogger(MyJedisCluster.class);
    private static final int MASTER_NODE_INDEX = 2;
    private final Map<String, Jedis> nodes = new HashMap<>();
    private final TwoWayHashMap<Integer, String> slotsNode = new TwoWayHashMap<>();
    private AtomicBoolean first = new AtomicBoolean(true);
    private Lock w = new ReentrantLock();
    private String password;
    private Set<HostAndPort> _hostAndPortSet;

    public MyJedisCluster(Set<HostAndPort> nodes) {
        this(nodes, DEFAULT_TIMEOUT);
    }

    public MyJedisCluster(Set<HostAndPort> nodes, int timeout) {
        super(nodes, timeout);
        this._hostAndPortSet = nodes;
        renewCache();
    }

    public MyJedisCluster(Set<HostAndPort> nodes, final GenericObjectPoolConfig poolConfig) {
        super(nodes, DEFAULT_TIMEOUT, DEFAULT_MAX_REDIRECTIONS, poolConfig);
        this._hostAndPortSet = nodes;
        renewCache();
    }

    public MyJedisCluster(Set<HostAndPort> nodes, String password) {
        this(nodes, password, DEFAULT_TIMEOUT);
    }

    public MyJedisCluster(Set<HostAndPort> nodes, String password, int timeout) {
        super(nodes, timeout, timeout, DEFAULT_MAX_REDIRECTIONS
                , password, new GenericObjectPoolConfig());
        this._hostAndPortSet = nodes;
        this.password = password;
        renewCache();
    }

    /**
     * 回调，由于有进行自动重分配，所以要加锁
     *
     * @param myJedisClusterCallBack
     * @param <T>
     * @return
     */
    public <T> T callBack(MyJedisClusterCallBack<T> myJedisClusterCallBack) {
        T t = null;
        if (w.tryLock()) {
            try {
                t = myJedisClusterCallBack.call();
            } finally {
                w.unlock();
            }
        } else {
            logger.warn("callBack没有获取到锁");
        }
        return t;
    }

    /**
     * 认证检查，只有在有密码的集群才需要进行<br>
     * 如果第一个节点就异常，MyJedisCluster根本无法构造成功，自然不会执行到这里
     */
    private void checkAuth() {
        if (this.password != null && this.password.length() > 0) {
            Iterator<HostAndPort> it = _hostAndPortSet.iterator();
            if (it.hasNext()) {
                HostAndPort _hostAndPort = it.next();
                try (Jedis jedis = new Jedis(_hostAndPort.getHost(), _hostAndPort.getPort())) {
                    String clusterNodes = jedis.clusterNodes();
                    List<String> clusterNodeList = parserClusterNodes(clusterNodes);
                    checkClusterNodes(clusterNodeList);
                } catch (JedisDataException e) {
                    if (e.getMessage().contains("NOAUTH")) {
                        try (Jedis jedis = new Jedis(_hostAndPort.getHost(), _hostAndPort.getPort())) {
                            jedis.auth(this.password);
                            String clusterNodes = jedis.clusterNodes();
                            List<String> clusterNodeList = parserClusterNodes(clusterNodes);
                            checkClusterNodes(clusterNodeList);
                        }
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * 解析cluster nodes结果
     *
     * @param clusterNodes
     * @return
     */
    private List<String> parserClusterNodes(String clusterNodes) {
        List<String> hostAndPortList = new ArrayList<>();
        String[] line = clusterNodes.split("\n", -1);
        for (String info : line) {
            if (info.trim().length() > 0) {
                String[] infos = info.split(" ", -1);
                if (infos.length > 1) {
                    String[] hostAndPort = infos[1].split("@", -1);
                    hostAndPortList.add(hostAndPort[0]);
                }
            }
        }
        return hostAndPortList;
    }

    /**
     * 针对cluster nodes进行认证校验，输出校验结果
     *
     * @param clusterNodeList
     */
    private void checkClusterNodes(List<String> clusterNodeList) {
        List<String> checkFailNodeList = new ArrayList<>();
        int checkCnt = 0;
        int failCnt = 0;
        for (String node : clusterNodeList) {
            String[] ipAndPortArr = node.split(":", -1);
            if (ipAndPortArr.length == 2) {
                checkCnt++;
                try (Jedis nodeJedis = new Jedis(ipAndPortArr[0], Integer.valueOf(ipAndPortArr[1]))) {
                    nodeJedis.connect();
                    nodeJedis.auth(this.password);
                } catch (Exception e) {
                    failCnt++;
                    checkFailNodeList.add(node + ", Error: " + e.getMessage());
                }
            }
        }
        logger.info("clusterNodes: {}, checkCnt: {}, failCnt: {}, checkFailNodeList:  {}"
                , clusterNodeList, checkCnt, failCnt, checkFailNodeList);
    }

    /**
     * 分配slot到cluster
     */
    public void renewCache() {
        if (w.tryLock()) {
            try {
                // 认证检查
                checkAuth();
                //第一次，从getClusterNodes获取
                if (first.getAndSet(false)) {
                    for (JedisPool jedisPool : getClusterNodes().values()) {
                        //注意，这里是取一个，用一个，释放一个，如果都取出来，就会有连接无法释放
                        Jedis jedis = jedisPool.getResource();
                        try {
                            if (renewCache(jedis)) return;
                        } catch (Exception e) {
                            logger.error("renewCache异常：" + e.getMessage(), e);
                        }
                    }
                } else {//后面都从缓存nodes获取，所以要维护好nodes
                    for (Jedis jedis : nodes.values()) {
                        try {
                            //先重置一下，再renew
                            //renewCache异常：Cannot use Jedis when in Pipeline. Please use Pipeline or reset jedis state
                            jedis.resetState();
                            if (renewCache(jedis)) return;
                        } catch (Exception e) {
                            logger.error("renewCache异常：" + e.getMessage(), e);
                        }
                    }
                }
            } finally {
                w.unlock();
            }
        } else {
            logger.warn("renewCache没有获取到锁");
        }
        throw new RuntimeException("没有可用的redis连接：" + nodes + "，请联系管理员！");
    }

    /**
     * 分配slot到cluster
     *
     * @param jedis
     * @return
     */
    public boolean renewCache(Jedis jedis) {
        try {
            //发送ping命令，检查Jedis是否存活
            String ping = jedis.ping();
            //存活返回PONG
            if ("PONG".equalsIgnoreCase(ping)) {
                logger.info("jedis：{}:{}，ping：{}，尝试分配", jedis.getClient().getHost(), jedis.getClient().getPort(), ping);
                //发现Cluster的Slots，并进行分配
                discoverClusterSlots(jedis);
                logger.info("jedis：{}:{}，分配成功", jedis.getClient().getHost(), jedis.getClient().getPort());
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
     * 拼接成 host:port
     *
     * @param jedis
     * @return
     */
    private String getNodeKey(Jedis jedis) {
        return jedis.getClient().getHost() + ":" + jedis.getClient().getPort();
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
        if (password != null && password.length() > 0) {
            nodePool.auth(password);
        }
        nodes.put(nodeKey, nodePool);
        return nodeKey;
    }

    /**
     * 发现Cluster的Slots，并进行分配
     *
     * @param jedis
     */
    private void discoverClusterSlots(Jedis jedis) {
        boolean cachePing = true;
        if (nodes.size() > 0) {
            //轮询缓存，ping不通过的移除，移除nodes和slotsNode
            cachePing = testPing(nodes.values(), true);
        }
        //(nodes.size() > 0 && !cachePing)
        //Cluster的Slots已经分配过了，但是可能有一台或几台服务端异常，需要重新拉取
        //nodes.size() == 0
        //Cluster的Slots未分配
        if ((nodes.size() > 0 && !cachePing) || nodes.size() == 0) {
            //获取集群
            SlotNumAndHostAndPortPool slotNumAndHostAndPortPool = getClusterSlots(jedis);
            List<Jedis> jedisList = slotNumAndHostAndPortPool.getJedisList();
            //测试集群是否全部可用
            while (!testPing(jedisList, false)) {
                //重新获取集群
                slotNumAndHostAndPortPool = getClusterSlots(jedis);
                jedisList = slotNumAndHostAndPortPool.getJedisList();
                SleepUtil.sleepMilliSecond(500);
            }

            //从待分配列表进行分配操作
            for (SlotNumAndHostAndPort waitSlot : slotNumAndHostAndPortPool.getWaitDistributionList()) {
                //分配槽位到主节点
                assignSlotsToNode(waitSlot.getSlotNums(), waitSlot.getHostAndPort());
            }
        }
    }

    /**
     * 获取最新集群信息
     *
     * @param jedis
     * @return
     */
    private SlotNumAndHostAndPortPool getClusterSlots(Jedis jedis) {
        //待分配列表
        List<SlotNumAndHostAndPort> waitDistributionList = new ArrayList<>();
        //发送cluster slots命令，获取槽位和ip关系
        //1：起始槽位
        //2：结束槽位
        //3：主节点
        //4：从节点
        List<Object> slots = jedis.clusterSlots();
        //循环
        for (Object slotInfoObj : slots) {
            List<Object> slotInfo = (List<Object>) slotInfoObj;
            logger.debug("slotInfo.size()：{}，slotInfo：{}", slotInfo.size(), getSlotInfo(slotInfo));
            //没有主节点，进入下一个循环
            if (slotInfo.size() <= MASTER_NODE_INDEX) {
                continue;
            }
            //获取主节点的ip端口等信息
            List<Object> hostInfos = (List<Object>) slotInfo.get(MASTER_NODE_INDEX);
            //没有获取到主节点的ip端口信息，进入下一个循环
            if (hostInfos.isEmpty()) {
                continue;
            }
            //根据起始槽位和结束槽位生成所有槽位列表
            List<Integer> slotNums = getAssignedSlotArray(slotInfo);
            //此时，我们只需使用主节点，丢弃从节点信息
            /**
             * at this time, we just use master, discard slave information
             * @see redis.clients.jedis.JedisClusterInfoCache#discoverClusterSlots(Jedis jedis)
             */
            //获取主节点的ip和端口
            HostAndPort targetNode = generateHostAndPort(hostInfos);
            //添加到待分配列表
            waitDistributionList.add(new SlotNumAndHostAndPort(slotNums, targetNode));
        }
        return new SlotNumAndHostAndPortPool(waitDistributionList);
    }

    /**
     * 测试集群所有Master节点是否都ok
     *
     * @param src
     * @param isRemove
     * @return
     */
    private boolean testPing(Collection<Jedis> src, boolean isRemove) {
        for (Iterator<Jedis> it = src.iterator(); it.hasNext(); ) {
            Jedis old = it.next();
            try {
                old.resetState();
                if (!old.ping().equals("PONG")) {
                    if (isRemove) {
                        logger.warn("{}：remove：{}", uuid("retry-testPing"), old);
                        //从slotsNode移除
                        slotsNode.keysRemove(getNodeKey(old));
                        //从nodes移除
                        it.remove();
                    }
                    logger.warn("{}：false，size：{}", uuid("retry-testPing"), src.size());
                    return false;
                }
            } catch (JedisConnectionException connectionException) {
                if (isRemove) {
                    logger.warn("{}：Exception.remove：{}", uuid("retry-testPing"), old);
                    //从slotsNode移除
                    slotsNode.keysRemove(getNodeKey(old));
                    //从nodes移除
                    it.remove();
                }
                logger.warn("{}：false，size：{}", uuid("retry-testPing"), src.size());
                return false;
            } finally {
                releaseConnection(old);
            }
        }
        logger.info("{}：true，size：{}", uuid("retry-testPing"), src.size());
        return true;
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
        for (Jedis jedis : nodes.values()) releaseConnection(jedis);
        nodes.clear();
        slotsNode.clear();
    }

    private String uuid(String tag) {
        return "【" + tag + "-" + UUID.randomUUID().toString() + "】";
    }

    interface MyJedisClusterCallBack<T> {
        T call();
    }

    class SlotNumAndHostAndPortPool {
        private List<SlotNumAndHostAndPort> slotNumAndHostAndPorts;
        private List<Jedis> jedisList = new ArrayList<>();

        SlotNumAndHostAndPortPool(List<SlotNumAndHostAndPort> slotNumAndHostAndPorts) {
            this.slotNumAndHostAndPorts = slotNumAndHostAndPorts;
            for (SlotNumAndHostAndPort slotNumAndHostAndPort : slotNumAndHostAndPorts) {
                Jedis _jedis = new Jedis(slotNumAndHostAndPort.getHostAndPort().getHost(),
                        slotNumAndHostAndPort.getHostAndPort().getPort());
                if (password != null && password.length() > 0) {
                    _jedis.auth(password);
                }
                this.jedisList.add(_jedis);
            }
        }

        List<Jedis> getJedisList() {
            return jedisList;
        }

        List<SlotNumAndHostAndPort> getWaitDistributionList() {
            return slotNumAndHostAndPorts;
        }
    }
}
