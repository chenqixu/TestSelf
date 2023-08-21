package com.cqx.work.redis;

import io.lettuce.core.*;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.Executions;
import io.lettuce.core.cluster.api.sync.NodeSelection;
import io.lettuce.core.cluster.api.sync.NodeSelectionCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.cluster.pubsub.api.async.NodeSelectionPubSubAsyncCommands;
import io.lettuce.core.cluster.pubsub.api.async.PubSubAsyncNodeSelection;
import io.lettuce.core.cluster.pubsub.api.reactive.RedisClusterPubSubReactiveCommands;
import io.lettuce.core.protocol.DecodeBufferPolicies;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * lettuce
 *
 * @author chenqixu
 */
public class TestLettuceCluster {

    public static void main(String[] args) {
        List<RedisURI> nodeList = new ArrayList<>();
        nodeList.add(RedisURI.builder().withHost("10.1.8.200").withPort(10010).withAuthentication("default", "by7JqR_k").build());
        nodeList.add(RedisURI.builder().withHost("10.1.8.201").withPort(10010).withAuthentication("default", "by7JqR_k").build());
        nodeList.add(RedisURI.builder().withHost("10.1.8.202").withPort(10010).withAuthentication("default", "by7JqR_k").build());
        RedisClusterClient clusterClient = RedisClusterClient.create(nodeList);

        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(5L))//设置自适应拓扑刷新超时，每次超时刷新一次，默认30s；
                .closeStaleConnections(false)//刷新拓扑时是否关闭失效连接，默认true，isPeriodicRefreshEnabled()为true时生效；
                .dynamicRefreshSources(true)//从拓扑中发现新节点，并将新节点也作为拓扑的源节点，动态刷新可以发现全部节点并计算每个客户端的数量，设置false则只有初始节点为源和计算客户端数量；
                .enableAllAdaptiveRefreshTriggers()//启用全部触发器自适应刷新拓扑，默认关闭；
                .enablePeriodicRefresh(Duration.ofSeconds(5L))//开启定时拓扑刷新并设置周期；
                .refreshTriggersReconnectAttempts(3)//长连接重新连接尝试n次才拓扑刷新
                .build();
        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .autoReconnect(true)//在连接丢失时开启或关闭自动重连，默认true；
                .cancelCommandsOnReconnectFailure(true)//允许在重连失败取消排队命令，默认false；
                .decodeBufferPolicy(DecodeBufferPolicies.always())//设置丢弃解码缓冲区的策略，以回收内存；always：解码后丢弃，最大内存效率；alwaysSome：解码后丢弃一部分；ratio(n)基于比率丢弃，n/(1+n),通常用1-10对应50%-90%；
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)//设置连接断开时命令的调用行为，默认启用重连；DEFAULT：启用时重连中接收命令，禁用时重连中拒绝命令；ACCEPT_COMMANDS：重连中接收命令；REJECT_COMMANDS：重连中拒绝命令；
//				.maxRedirects(5)//当键从一个节点迁移到另一个节点，集群重定向次数，默认5；
//				.nodeFilter(nodeFilter)//设置节点过滤器
//				.pingBeforeActivateConnection(true)//激活连接前设置PING，默认true；
//				.protocolVersion(ProtocolVersion.RESP3)//设置协议版本，默认RESP3；
//				.publishOnScheduler(false)//使用专用的调度器发出响应信号，默认false，启用时数据信号将使用服务的多线程发出；
//				.requestQueueSize(requestQueueSize)//设置每个连接请求队列大小；
//				.scriptCharset(scriptCharset)//设置Lua脚本编码为byte[]的字符集，默认StandardCharsets.UTF_8；
//				.socketOptions(SocketOptions.builder().connectTimeout(Duration.ofSeconds(10)).keepAlive(true).tcpNoDelay(true).build())//设置低级套接字的属性
//				.sslOptions(SslOptions.builder().build())//设置ssl属性
//				.suspendReconnectOnProtocolFailure(false)//当重新连接遇到协议失败时暂停重新连接(SSL验证，连接失败前PING)，默认值为false；
//				.timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(10)))//设置超时来取消和终止命令；
                .topologyRefreshOptions(clusterTopologyRefreshOptions)//设置拓扑更新设置
                .validateClusterNodeMembership(true)//在允许连接到集群节点之前，验证集群节点成员关系，默认值为true；
                .build();

        clusterClient.setDefaultTimeout(Duration.ofSeconds(5L));
        clusterClient.setOptions(clusterClientOptions);

        StatefulRedisClusterConnection<String, String> clusterConn = clusterClient.connect();
        clusterConn.setReadFrom(ReadFrom.ANY);//设置从哪些节点读取数据；
        RedisAdvancedClusterCommands<String, String> clusterCmd = clusterConn.sync();
        clusterCmd.set("a", "A");
        clusterCmd.set("b", "B");
        clusterCmd.set("c", "C");
        clusterCmd.set("d", "D");
        System.out.println("get a=" + clusterCmd.get("a"));
        System.out.println("get b=" + clusterCmd.get("b"));
        System.out.println("get c=" + clusterCmd.get("c"));
        System.out.println("get d=" + clusterCmd.get("d"));
        //跨槽位命令
        Map<String, String> kvmap = new HashMap<>();
        kvmap.put("a", "AA");
        kvmap.put("b", "BB");
        kvmap.put("c", "CC");
        kvmap.put("d", "DD");
        clusterCmd.mset(kvmap);//Lettuce做了优化，支持一些命令的跨槽位命令；
        System.out.println("Lettuce mget:" + clusterCmd.mget("a", "b", "c", "d"));
        //选定部分节点操作
        NodeSelection<String, String> replicas = clusterCmd.replicas();
        NodeSelectionCommands<String, String> replicaseCmd = replicas.commands();
        Executions<KeyScanCursor<String>> executions = replicaseCmd.scan(ScanCursor.INITIAL);
        executions.forEach(s -> {
            System.out.println(s.getKeys());
        });

        //订阅发布消息
        StatefulRedisClusterPubSubConnection<String, String> pubSubConn = clusterClient.connectPubSub();
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
        pubSubConn.sync().subscribe("TEST_Ch");//（回调内部使用阻塞调用或者lettuce同步api调用，需使用异步订阅）
        clusterCmd.publish("TEST_Ch", "MSGMSGMSG");
        //响应式订阅，可以监听ChannelMessage和PatternMessage，使用链式过滤处理计算等操作
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

        //keySpaceEvent事件
        StatefulRedisClusterPubSubConnection<String, String> clusterPubSubConn = clusterClient.connectPubSub();
        clusterPubSubConn.setNodeMessagePropagation(true);//启用禁用节点消息传播到该listener，例如只能在本节点通知的键事件通知；
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

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }
}
