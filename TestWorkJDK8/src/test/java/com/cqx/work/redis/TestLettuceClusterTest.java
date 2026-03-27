package com.cqx.work.redis;

import io.lettuce.core.*;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.Executions;
import io.lettuce.core.cluster.api.sync.NodeSelection;
import io.lettuce.core.cluster.api.sync.NodeSelectionCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.async.NodeSelectionPubSubAsyncCommands;
import io.lettuce.core.cluster.pubsub.api.async.PubSubAsyncNodeSelection;
import io.lettuce.core.cluster.pubsub.api.reactive.RedisClusterPubSubReactiveCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestLettuceClusterTest {
    private static final Logger logger = LoggerFactory.getLogger(TestLettuceClusterTest.class);
    private TestLettuceCluster tlc;

    @Before
    public void setUp() throws Exception {
        tlc = new TestLettuceCluster();
        tlc.init();
    }

    @After
    public void tearDown() throws Exception {
        tlc.close();
    }

    @Test
    public void clusterCmdTest() {
        try (StatefulRedisClusterConnection<String, String> clusterConn = tlc.getClusterClient().connect()) {
            // 设置从哪些节点读取数据；
            clusterConn.setReadFrom(ReadFrom.ANY);
            // 创建一个Cmd
            RedisAdvancedClusterCommands<String, String> clusterCmd = clusterConn.sync();


            //=============================
            // 测试set + get
            //=============================
            clusterCmd.set("a", "A");
            clusterCmd.set("b", "B");
            clusterCmd.set("c", "C");
            clusterCmd.set("d", "D");
            logger.info("get a={}", clusterCmd.get("a"));
            logger.info("get b={}", clusterCmd.get("b"));
            logger.info("get c={}", clusterCmd.get("c"));
            logger.info("get d={}", clusterCmd.get("d"));


            //=============================
            // 测试mset + mget
            //=============================
            // 跨槽位命令
            Map<String, String> kvmap = new HashMap<>();
            kvmap.put("a", "AA");
            kvmap.put("b", "BB");
            kvmap.put("c", "CC");
            kvmap.put("d", "DD");
            // Lettuce做了优化，支持一些命令的跨槽位命令；
            clusterCmd.mset(kvmap);
            logger.info("Lettuce mget: {}", clusterCmd.mget("a", "b", "c", "d"));


            //=============================
            // 打印每个槽位的所有key
            //=============================
            // 选定部分节点操作
            NodeSelection<String, String> replicas = clusterCmd.replicas();
            NodeSelectionCommands<String, String> replicaseCmd = replicas.commands();
            Executions<KeyScanCursor<String>> executions = replicaseCmd.scan(ScanCursor.INITIAL);
            executions.forEach(s -> {
                logger.info("KeyScanCursor={}, size={}, keys={}", s.getCursor(), s.getKeys().size(), s.getKeys());
            });
        }
    }

    @Test
    public void pipeline1Test() {
        try (StatefulRedisClusterConnection<String, String> clusterConn = tlc.getClusterClient().connect()) {
            // 设置从哪些节点读取数据；
            clusterConn.setReadFrom(ReadFrom.ANY);
            // 创建一个异步客户端
            RedisAdvancedClusterAsyncCommands<String, String> asyncCommands = clusterConn.async();

            // 要获取的 hash 表列表
            List<String> hashKeys = Arrays.asList("H2_13075893065", "H2_17758979907");

            // 创建 Future 列表
            List<RedisFuture<Map<String, String>>> futures = hashKeys.stream()
                    .map(key -> asyncCommands.hgetall(key))
                    .collect(Collectors.toList());

            // 等待所有 Future 完成
            LettuceFutures.awaitAll(5, TimeUnit.SECONDS,
                    futures.toArray(new RedisFuture[0]));

            // 收集结果
            Map<String, Map<String, String>> allResults = new HashMap<>();
            for (int i = 0; i < hashKeys.size(); i++) {
                try {
                    allResults.put(hashKeys.get(i), futures.get(i).get());
                } catch (Exception e) {
                    allResults.put(hashKeys.get(i), Collections.emptyMap());
                }
            }
            logger.info("allResults={}", allResults);
        }
    }

    @Test
    public void pipeline2Test() {
        try (StatefulRedisClusterConnection<String, String> clusterConn = tlc.getClusterClient().connect()) {
            // 设置从哪些节点读取数据；
            clusterConn.setReadFrom(ReadFrom.ANY);
            // 创建一个async
            RedisAdvancedClusterAsyncCommands<String, String> async = clusterConn.async();
            async.setAutoFlushCommands(false);

            List<String> hashKeys = Arrays.asList("H2_13075893065", "H2_17758979907");

            // 批量提交 HGETALL 命令
            Map<String, RedisFuture<Map<String, String>>> futureMap = new HashMap<>();
            for (String hashKey : hashKeys) {
                futureMap.put(hashKey, async.hgetall(hashKey));
            }

            // 一次性发送所有命令
            async.flushCommands();

            // 获取结果
            Map<String, Map<String, String>> results = new HashMap<>();
            for (Map.Entry<String, RedisFuture<Map<String, String>>> entry : futureMap.entrySet()) {
                try {
                    results.put(entry.getKey(), entry.getValue().get(5, TimeUnit.SECONDS));
                } catch (Exception e) {
                    results.put(entry.getKey(), Collections.emptyMap());
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info("results={}", results);
        }
    }

    /**
     * 响应式订阅测试
     */
    @Test
    public void pubSubConnTest() {
        // 订阅发布消息
        try (StatefulRedisClusterConnection<String, String> clusterConn = tlc.getClusterClient().connect();
             StatefulRedisClusterPubSubConnection<String, String> pubSubConn = tlc.getClusterClient().connectPubSub()) {
            // 设置从哪些节点读取数据；
            clusterConn.setReadFrom(ReadFrom.ANY);
            // 创建一个Cmd
            RedisAdvancedClusterCommands<String, String> clusterCmd = clusterConn.sync();

            // 构造监听器
            pubSubConn.addListener(new RedisPubSubListener<String, String>() {
                @Override
                public void message(String channel, String message) {
                    System.out.println("[message]ch:" + channel + ",msg:" + message);
                }

                @Override
                public void message(String pattern, String channel, String message) {
                }

                @Override
                public void subscribed(String channel, long count) {
                    System.out.println("[subscribed]ch:" + channel);
                }

                @Override
                public void psubscribed(String pattern, long count) {
                }

                @Override
                public void unsubscribed(String channel, long count) {
                }

                @Override
                public void punsubscribed(String pattern, long count) {
                }
            });
            //（回调内部使用阻塞调用或者lettuce同步api调用，需使用异步订阅）
            pubSubConn.sync().subscribe("TEST_Ch");
            clusterCmd.publish("TEST_Ch", "MSGMSGMSG");
            // 响应式订阅，可以监听ChannelMessage和PatternMessage，使用链式过滤处理计算等操作
            RedisClusterPubSubReactiveCommands<String, String> pubsubReactive = pubSubConn.reactive();
            pubsubReactive.subscribe("TEST_Ch2").subscribe();
            pubsubReactive.observeChannels()
                    .filter(chmsg -> {
                        return chmsg.getMessage().contains("tom");
                    })
                    .doOnNext(chmsg -> {
                        System.out.println("<tom>" + chmsg.getChannel() + ">>" + chmsg.getMessage());
                    })
                    .subscribe();
            clusterCmd.publish("TEST_Ch2", "send to jerry");
            clusterCmd.publish("TEST_Ch", "tom MSG");
            clusterCmd.publish("TEST_Ch2", "this is tom");

            // 等待测试完成
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * keySpaceEvent事件测试
     */
    @Test
    public void keySpaceEventTest() {
        try (StatefulRedisClusterConnection<String, String> clusterConn = tlc.getClusterClient().connect();
             StatefulRedisClusterPubSubConnection<String, String> clusterPubSubConn = tlc.getClusterClient().connectPubSub()) {
            // 设置从哪些节点读取数据；
            clusterConn.setReadFrom(ReadFrom.ANY);
            // 创建一个Cmd
            RedisAdvancedClusterCommands<String, String> clusterCmd = clusterConn.sync();
            // 启用禁用节点消息传播到该listener，例如只能在本节点通知的键事件通知；
            clusterPubSubConn.setNodeMessagePropagation(true);

            // 构造监听器
            RedisPubSubListener<String, String> listener = new RedisPubSubListener<String, String>() {
                @Override
                public void unsubscribed(String channel, long count) {
                    System.out.println("unsubscribed_ch:" + channel);
                }

                @Override
                public void subscribed(String channel, long count) {
                    System.out.println("subscribed_ch:" + channel);
                }

                @Override
                public void punsubscribed(String pattern, long count) {
                    System.out.println("punsubscribed_pattern:" + pattern);
                }

                @Override
                public void psubscribed(String pattern, long count) {
                    System.out.println("psubscribed_pattern:" + pattern);
                }

                @Override
                public void message(String pattern, String channel, String message) {
                    System.out.println("message_pattern:" + pattern + " ch:" + channel + " msg:" + message);
                }

                @Override
                public void message(String channel, String message) {
                    System.out.println("message_ch:" + channel + " msg:" + message);
                }
            };
            clusterPubSubConn.addListener(listener);
            PubSubAsyncNodeSelection<String, String> allPubSubAsyncNodeSelection = clusterPubSubConn.async().all();
            NodeSelectionPubSubAsyncCommands<String, String> pubsubAsyncCmd = allPubSubAsyncNodeSelection.commands();
            clusterCmd.setex("a", 1, "A");
            pubsubAsyncCmd.psubscribe("__keyspace@0__:*");

            // 等待测试完成
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}