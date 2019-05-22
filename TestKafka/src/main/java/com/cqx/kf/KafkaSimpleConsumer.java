package com.cqx.kf;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.FetchResponse;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.consumer.SimpleConsumer;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.message.ByteBufferMessageSet;
import scala.collection.Seq;

import java.util.*;

public class KafkaSimpleConsumer {
    private List<String> m_replicaBrokers = new ArrayList<String>();

    public KafkaSimpleConsumer() {
        m_replicaBrokers = new ArrayList<String>();
    }

    public static void main(String args[]) {
        KafkaSimpleConsumer example = new KafkaSimpleConsumer();
        // 最大读取消息数量
        long maxReads = Long.parseLong("3");
        // 要订阅的topic
        String topic = "mytopic";
        // 要查找的分区
        int partition = Integer.parseInt("0");
        // broker节点的ip
        List<String> seeds = new ArrayList<String>();
        seeds.add("192.168.4.30");
        seeds.add("192.168.4.31");
        seeds.add("192.168.4.32");
        // 端口
        int port = Integer.parseInt("9092");
        try {
            example.run(maxReads, topic, partition, seeds, port);
        } catch (Exception e) {
            System.out.println("Oops:" + e);
            e.printStackTrace();
        }
    }

    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition,
                                     long whichTime, String clientName) {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo =
                new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
//		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo,
//				kafka.api.OffsetRequest.CurrentVersion(), clientName);
//        kafka.api.OffsetResponse response = consumer.getOffsetsBefore(request);
//        if (response.hasError()) {
//			System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
//            return 0;
//        }
//        long[] offsets = response.offsets(topic, partition);
//        return offsets[0];
        return 0l;
    }

    public void run(long a_maxReads, String a_topic, int a_partition,
                    List<String> a_seedBrokers, int a_port) throws Exception {
        // 获取指定Topic partition的元数据
        PartitionMetadata metadata = findLeader(a_seedBrokers, a_port, a_topic, a_partition);
        if (metadata == null) {
            System.out.println("Can't find metadata for Topic and Partition. Exiting");
            return;
        }
        if (metadata.leader() == null) {
            System.out.println("Can't find Leader for Topic and Partition. Exiting");
            return;
        }
        // 找到leader broker
        String leadBroker = metadata.leader().host();
        String clientName = "Client_" + a_topic + "_" + a_partition;
        // 链接leader broker
        SimpleConsumer consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
        // 获取topic的最新偏移量
        long readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.EarliestTime(), clientName);
        int numErrors = 0;
        while (a_maxReads > 0) {
            if (consumer == null) {
                consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
            }
            // 本质上就是发送FetchRequest请求
            FetchRequest req = new FetchRequestBuilder().clientId(clientName).addFetch(a_topic,
                    a_partition, readOffset, 100000).build();
            FetchResponse fetchResponse = consumer.fetch(req);
            if (fetchResponse.hasError()) {
                numErrors++;
                // Something went wrong!
                short code = 0;//fetchResponse.errorCode(a_topic, a_partition);
                System.out.println("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
                if (numErrors > 5)
                    break;
                if (code == ErrorMapping.OffsetOutOfRangeCode()) {
                    // We asked for an invalid offset. For simple case ask for
                    // the last element to reset
                    readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.LatestTime(), clientName);
                    continue;
                }
                consumer.close();
                consumer = null;
                // 处理topic的partition的leader发生变更的情况
                leadBroker = findNewLeader(leadBroker, a_topic, a_partition, a_port);
                continue;
            }
            numErrors = 0;
            long numRead = 0;
            ByteBufferMessageSet byteBufferMessageSet = fetchResponse.messageSet(a_topic, a_partition);

//			for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(a_topic, a_partition)) {
//				long currentOffset = messageAndOffset.offset();
//				if (currentOffset < readOffset) {// 过滤旧的数据
//					System.out.println("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
//					continue;
//				}
//				readOffset = messageAndOffset.nextOffset();
//				ByteBuffer payload = messageAndOffset.message().payload();
//				byte[] bytes = new byte[payload.limit()];
//				payload.get(bytes);
//				// 打印消息
//				System.out.println(String.valueOf(messageAndOffset.offset()) + ": " + new String(bytes, "UTF-8"));
//				numRead++;
//				a_maxReads--;
//			}
            if (numRead == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        if (consumer != null)
            consumer.close();
    }

    /**
     * @param a_oldLeader
     * @param a_topic
     * @param a_partition
     * @param a_port
     * @return String
     * @throws Exception 找一个leader broker，其实就是发送TopicMetadataRequest请求
     */
    private String findNewLeader(String a_oldLeader, String a_topic,
                                 int a_partition, int a_port) throws Exception {
        for (int i = 0; i < 3; i++) {
            boolean goToSleep = false;
            PartitionMetadata metadata = findLeader(m_replicaBrokers, a_port, a_topic, a_partition);
            if (metadata == null) {
                goToSleep = true;
            } else if (metadata.leader() == null) {
                goToSleep = true;
            } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                // first time through if the leader hasn't changed give
                // ZooKeeper a second to recover
                // second time, assume the broker did recover before failover,
                // or it was a non-Broker issue
                //
                goToSleep = true;
            } else {
                return metadata.leader().host();
            }
            if (goToSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        System.out.println("Unable to find new leader after Broker failure. Exiting");
        throw new Exception("Unable to find new leader after Broker failure. Exiting");
    }

    private PartitionMetadata findLeader(List<String> a_seedBrokers, int a_port, String a_topic, int a_partition) {
        PartitionMetadata returnMetaData = null;
        loop:
        for (String seed : a_seedBrokers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(seed, a_port, 100000, 64 * 1024, "leaderLookup");
                List<String> topics = Collections.singletonList(a_topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
//                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
//                List<TopicMetadata> metaData = resp.topicsMetadata();
//                for (TopicMetadata item : metaData) {
//                    for (PartitionMetadata part : item.partitionsMetadata()) {
//                        if (part.partitionId() == a_partition) {
//                            returnMetaData = part;
//                            break loop;
//                        }
//                    }
//                }
            } catch (Exception e) {
                System.out.println("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic + ", " + a_partition + "] Reason: " + e);
            } finally {
                if (consumer != null)
                    consumer.close();
            }
        }
        if (returnMetaData != null) {
            m_replicaBrokers.clear();
//            for (kafka.cluster.Broker replica : returnMetaData.replicas()) {
//                m_replicaBrokers.add(replica.host());
//            }
        }
        return returnMetaData;
    }
}
