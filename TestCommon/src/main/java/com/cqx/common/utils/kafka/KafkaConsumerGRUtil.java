package com.cqx.common.utils.kafka;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.list.KVList;
import com.cqx.common.utils.param.ParamUtil;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * KafkaConsumerGRUtil
 * <pre>
 *     开发参数参考：
 *       kafkaconf.bootstrap.servers: "10.1.8.200:9092,10.1.8.201:9092,10.1.8.202:9092"
 *       kafkaconf.key.deserializer: "org.apache.kafka.common.serialization.StringDeserializer"
 *       kafkaconf.value.deserializer: "org.apache.kafka.common.serialization.ByteArrayDeserializer"
 *       kafkaconf.security.protocol: "SASL_PLAINTEXT"
 *       kafkaconf.sasl.mechanism: "PLAIN"
 *       kafkaconf.group.id: "throughput_jstorm"
 *       kafkaconf.enable.auto.commit: "true"
 *       kafkaconf.fetch.min.bytes: "52428800"
 *       kafkaconf.max.poll.records: "12000"
 *       kafkaconf.newland.kafka_username: admin
 *       kafkaconf.newland.kafka_password: admin
 *       schema_url: "http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t="
 * </pre>
 *
 * @author chenqixu
 */
public class KafkaConsumerGRUtil extends KafkaConsumerUtil<String, byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGRUtil.class);
    private final String schemaMode_URL = "URL";
    private final String schemaMode_FILE = "FILE";
    private final String schemaMode_NOAVRO = "NOAVRO";
    private SchemaUtil schemaUtil;
    private RecordConvertor recordConvertor = null;
    private Schema schema;
    private String mode;
    private String groupId;
    private String fromTime;
    private String fromOffset;
    private String schemaMode;
    private String avscStr;
    private Map<TopicPartition, Long> topicPartitionOffsetMap;

    public KafkaConsumerGRUtil(Map stormConf) throws IOException {
        super(stormConf);
        initGR(stormConf);
    }

    public KafkaConsumerGRUtil(Map stormConf, boolean isTransaction) throws IOException {
        super(stormConf, isTransaction);
        initGR(stormConf);
    }

    /**
     * 初始化
     *
     * @param stormConf
     * @throws IOException
     */
    private void initGR(Map stormConf) throws IOException {
        // 新增schema读取模式：[URL|FILE]，默认是URL
        try {
            schemaMode = ParamUtil.setValDefault(stormConf, SchemaUtil.SCHEMA_MODE, schemaMode_URL);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            // 默认是URL
            schemaMode = schemaMode_URL;
        }
        // schema模式匹配
        switch (schemaMode) {
            // 非avro
            case schemaMode_NOAVRO:
                break;
            // 本地文件模式
            case schemaMode_FILE:
                // 读取avsc文件
                String avsc_file = (String) stormConf.get(SchemaUtil.SCHEAM_FILE);
                if (avsc_file == null || avsc_file.trim().length() == 0 || !FileUtil.isExists(avsc_file)) {
                    throw new RuntimeException(
                            String.format("初始化失败，读取avsc文件异常！%s：%s", SchemaUtil.SCHEAM_FILE, avsc_file)
                    );
                }
                avscStr = avscFromFile(avsc_file);
                logger.info("从 {} 文件读取avsc文件内容：{}", avsc_file, avscStr);
                schemaUtil = new SchemaUtil(null);
                break;
            // 远程服务、默认模式
            case schemaMode_URL:
            default:
                //schema工具类
                schemaUtil = new SchemaUtil(null, stormConf);
                break;
        }
        // 模式
        mode = (String) stormConf.get("kafkaconf.newland.consumer.mode");
        // 消费时间
        fromTime = (String) stormConf.get("kafkaconf.newland.consumer.fromTime");
        // 消费组id
        groupId = (String) stormConf.get("kafkaconf.group.id");
        // 消费位置
        fromOffset = (String) stormConf.get("kafkaconf.newland.consumer.fromOffset");
    }

    /**
     * 订阅话题
     *
     * @param topic
     */
    @Override
    public void subscribe(String topic) {
        super.subscribe(topic);
        // schema模式匹配
        switch (schemaMode) {
            case schemaMode_NOAVRO:
                break;
            case schemaMode_FILE:
                // schema从本地文件获取
                schema = schemaUtil.getSchemaByString(avscStr);
                // 记录转换工具类
                recordConvertor = new RecordConvertor(schema);
                break;
            case schemaMode_URL:
            default:
                // schema从远程服务器获取
                schema = schemaUtil.getSchemaByTopic(topic);
                // 记录转换工具类
                recordConvertor = new RecordConvertor(schema);
                break;
        }
        try {
            //模式匹配
            if ("fromBeginning".equals(mode)) {
                fromBeginning();//从头开始消费
                logger.info("消费模式：{}，从头开始消费", mode);
            } else if ("fromEnd".equals(mode)) {
                fromEnd();//从最新位置消费
                logger.info("消费模式：{}，从最新位置消费", mode);
            } else if ("fromTime".equals(mode) && fromTime != null) {
                fromTime(fromTime);
                logger.info("消费模式：{}，从{}开始消费", mode, fromTime);
            } else if ("fromOffset".equals(mode)) {
                if (fromOffset != null && fromOffset.trim().length() > 0) {
                    topicPartitionOffsetMap = new HashMap<>();
                    String[] offsetArray = fromOffset.split(";", -1);
                    for (String topicPartition : offsetArray) {
                        String[] topicPartitionArray = topicPartition.split(",", -1);
                        if (topicPartitionArray.length == 2) {
                            int partition = Integer.valueOf(topicPartitionArray[0]);
                            long offset = Long.valueOf(topicPartitionArray[1]);
                            topicPartitionOffsetMap.put(new TopicPartition(topic, partition), offset);
                        }
                    }
                }
                if (topicPartitionOffsetMap.size() > 0) {
                    fromOffset(topicPartitionOffsetMap);
                    for (Map.Entry<TopicPartition, Long> entry : topicPartitionOffsetMap.entrySet()) {
                        logger.info("消费模式：{}，分区{}从{}开始消费", mode, entry.getKey(), entry.getValue());
                    }
                } else {
                    throw new NullPointerException("fromOffset配置不正确！格式请参考：分区,偏移量，多个分区之间用分号分割");
                }
            } else {
                //从group id的上个位置消费
                if (groupId == null) throw new NullPointerException("group id未配置！请配置参数：kafkaconf.group.id");
                logger.info("从group id：{}，的上个位置消费", groupId);
            }
        } catch (Exception e) {
            throw new RuntimeException("订阅话题异常", e);
        }
    }

    /**
     * 通过schema构造记录转换工具类
     *
     * @param schema
     */
    public void buildRecordConvertor(Schema schema) {
        // 记录转换工具类
        recordConvertor = new RecordConvertor(schema);
    }

    /**
     * 消费，只有Value，获得List&lt;GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public List<GenericRecord> polls(long timeout) {
        List<byte[]> values = poll(timeout);
        List<GenericRecord> records = new ArrayList<>();
        for (byte[] bytes : values) {
            records.add(getGenericRecord(bytes));
        }
        return records;
    }

    /**
     * 消费，带Key和Value，获得IKVList&lt;String, GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<String, GenericRecord> pollsHasKey(long timeout) {
        IKVList<String, byte[]> recordshaskey = pollHasKey(timeout);
        IKVList<String, GenericRecord> records = new KVList<>();
        for (IKVList.Entry<String, byte[]> entry : recordshaskey.entrySet()) {
            records.put(entry.getKey(), getGenericRecord(entry.getValue()));
        }
        return records;
    }

    /**
     * 消费，带offset，获得IKVList&lt;Long, GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<Long, GenericRecord> pollsHasOffset(long timeout) {
        IKVList<Long, GenericRecord> records = new KVList<>();
        for (IKVList.Entry<ConsumerRecord<String, byte[]>, GenericRecord> entry : pollsHasConsumerRecord(timeout).entrySet()) {
            records.put(entry.getKey().offset(), entry.getValue());
        }
        return records;
    }

    /**
     * 消费，返回IKVList&lt;ConsumerRecord, GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<ConsumerRecord<String, byte[]>, GenericRecord> pollsHasConsumerRecord(long timeout) {
        List<ConsumerRecord<String, byte[]>> recordshasConsumerRecord = pollHasConsumerRecord(timeout);
        IKVList<ConsumerRecord<String, byte[]>, GenericRecord> records = new KVList<>();
        for (ConsumerRecord<String, byte[]> entry : recordshasConsumerRecord) {
            records.put(entry, getGenericRecord(entry.value()));
        }
        return records;
    }

    /**
     * 消费，处理成功则提交，异常则回滚，回滚异常则抛出运行时异常
     *
     * @param timeout
     * @param abstractKafkaUtil
     */
    public void poll(long timeout, AbstractKafkaUtil<ConsumerRecord<String, byte[]>, GenericRecord> abstractKafkaUtil) {
        long firstOffset = -1L;
        TopicPartition topicPartition = null;
        try {
            // 先消费，再转换成GenericRecord
            List<ConsumerRecord<String, byte[]>> records = pollHasConsumerRecord(timeout);
            if (records.size() > 0) {
                // 记录第一个offset
                firstOffset = records.get(0).offset();
                // 记录话题分区
                topicPartition = new TopicPartition(records.get(0).topic(), records.get(0).partition());
                // 转成GenericRecord
                IKVList<ConsumerRecord<String, byte[]>, GenericRecord> genericRecordIKVList = new KVList<>();
                for (ConsumerRecord<String, byte[]> entry : records) {
                    genericRecordIKVList.put(entry, getGenericRecord(entry.value()));
                }
                // 成功处理
                if (abstractKafkaUtil.callBack(genericRecordIKVList.entrySet())) {
                    // 提交
                    commitSync();
                }
            }
        } catch (Exception e) {
            // 异常，进行回滚
            if (firstOffset > -1 && topicPartition != null) {
                // 拼接回滚的分区和偏移量
                Map<TopicPartition, Long> topicPartitionOffsetMap = new HashMap<>();
                topicPartitionOffsetMap.put(topicPartition, firstOffset);
                try {
                    // 回滚
                    fromOffset(topicPartitionOffsetMap);
                    // 提交
                    commitSync();
                    logger.warn("异常，进行回滚，topicPartition：{}，firstOffset：{}，具体异常信息：", topicPartition, firstOffset, e);
                } catch (Exception rollBackException) {
                    // 回滚异常，抛出运行时异常
                    throw new RuntimeException(String.format("回滚异常，topicPartition：%s，firstOffset：%s，异常信息：%s"
                            , topicPartition, firstOffset, rollBackException.getMessage()), rollBackException);
                }
            }
        }
    }

    /**
     * 消费Ogg数据，并处理<br>
     * <pre>
     *     遇到schema，就先处理正常数据，再处理schema
     *     如果处理过程遇到异常，就抛出
     *     支持以下几种情况：
     *     1、data
     *     2、data+schema
     *     3、data+schema+data
     *     4、schema
     *     5、schema+data
     *     6、异常data
     *     7、data+异常data
     *     8、schema+异常data
     *     9、data，处理流程异常
     *     10、schema，更新schema异常
     * </pre>
     *
     * @param timeout    单次数据拉取时间
     * @param oggPollInf 业务处理类
     * @throws Exception
     */
    public void oggPoll(long timeout, OggPollInf oggPollInf) throws Exception {
        // 从kafka消费数据
        List<ConsumerRecord<String, byte[]>> records = pollHasConsumerRecord(timeout);
        // 进行业务处理
        oggDataDeal(oggPollInf, records);
    }

    /**
     * 处理Ogg数据，非测试模式
     *
     * @param oggPollInf 业务处理类
     * @param records    从kafka消费到的数据
     * @throws Exception
     */
    public void oggDataDeal(OggPollInf oggPollInf, List<ConsumerRecord<String, byte[]>> records) throws Exception {
        oggDataDeal(oggPollInf, records, false);
    }

    /**
     * 处理Ogg数据，具体实现
     *
     * @param oggPollInf 业务处理类
     * @param records    从kafka消费到的数据
     * @param isTest     是否是测试模式，是：不进行偏移量提交
     * @throws Exception
     */
    public void oggDataDeal(OggPollInf oggPollInf, List<ConsumerRecord<String, byte[]>> records, boolean isTest) throws Exception {
        long firstOffset = -1L;
        long lastDealOffset = -1L;
        int partition = -1;
        LinkedBlockingDeque<ConsumerRecord<String, byte[]>> deque = new LinkedBlockingDeque<>();
        if (records.size() > 0) {
            // 循环写到双端队列
            for (ConsumerRecord<String, byte[]> consumerRecord : records) {
                deque.offer(consumerRecord);
            }
            // 数据缓存List
            List<GenericRecord> genericRecords = new ArrayList<>();
            // 从双端队列获取数据
            ConsumerRecord<String, byte[]> consumerRecord;
            while ((consumerRecord = deque.poll()) != null) {
                // 尝试解析Data
                try {
                    // 写入数据缓存List
                    genericRecords.add(getGenericRecord(consumerRecord.value()));
                    // 更新最后位置
                    lastDealOffset = consumerRecord.offset();
                    // 更新首条记录偏移量
                    if (firstOffset == -1L) {
                        firstOffset = lastDealOffset;
                        partition = consumerRecord.partition();
                    }
                } catch (Exception toGenericRecordException) {
                    logger.warn("解析Data异常，进入异常处理流程，尝试转换schema，当前异常数据偏移量: {}", consumerRecord.offset());
                    //================
                    // 异常处理流程
                    //================
                    // 如果有数据未处理，则处理业务数据，并把解析不了的数据丢回栈顶
                    if (genericRecords.size() > 0) {
                        // 把解析不了的数据丢回栈顶
                        deque.offerFirst(consumerRecord);
                        logger.info("解析不了的数据丢回栈顶: {}", consumerRecord);
                        try {
                            logger.info("处理业务数据, 首条记录偏移量: {}, 最后位置偏移量: {}", firstOffset, lastDealOffset);
                            // 处理业务数据
                            oggPollInf.dataDeal(genericRecords);
                            // 处理完成，清空缓存
                            genericRecords.clear();
                            // 处理完成，提交偏移量
                            if (!isTest) {
                                commitSync(consumerRecord.partition(), lastDealOffset);
                            }
                            // 更新首条记录偏移量
                            firstOffset = -1L;
                            logger.info("处理完成，针对分区: {}, 提交偏移量: {}", consumerRecord.partition(), lastDealOffset);
                        } catch (Exception dataE) {
                            // 处理业务数据异常，向上抛出
                            logger.warn("处理业务数据异常！", dataE);
                            dataE.addSuppressed(toGenericRecordException);
                            throw dataE;
                        }
                    } else {// 没有数据待处理，尝试转换schema
                        String value = new String(consumerRecord.value());
                        try {
                            // 尝试能否转换成schema
                            Schema schema = updateSchema(value);
                            //================
                            // 转换成功
                            //================
                            // 更新数据库
                            oggPollInf.updateSchema(schema);
                            // 获取最后位置
                            lastDealOffset = consumerRecord.offset();
                            // 提交偏移量
                            if (!isTest) {
                                commitSync(consumerRecord.partition(), lastDealOffset);
                            }
                            logger.info("转换schema完成，针对分区: {}, 提交偏移量: {}", consumerRecord.partition(), lastDealOffset);
                        } catch (Exception updateSchemaException) {
                            // 把解析不了的数据丢回栈顶
                            deque.offerFirst(consumerRecord);
                            // 抛出异常
                            logger.warn(String.format("%s 更新schema异常！", value), updateSchemaException);
                            updateSchemaException.addSuppressed(toGenericRecordException);
                            throw updateSchemaException;
                        }
                    }
                }
            }
            //================
            // 正常处理流程
            //================
            if (genericRecords.size() > 0) {
                try {
                    logger.info("处理业务数据, 首条记录偏移量: {}, 最后位置偏移量: {}", firstOffset, lastDealOffset);
                    // 处理业务数据
                    oggPollInf.dataDeal(genericRecords);
                    // 处理完成，提交偏移量
                    if (!isTest) {
                        commitSync(partition, lastDealOffset);
                    }
                    logger.info("处理完成，针对分区: {}, 提交偏移量: {}", partition, lastDealOffset);
                } catch (Exception dataE) {
                    // 处理业务数据异常，向上抛出
                    logger.warn("处理业务数据异常！", dataE);
                    throw dataE;
                }
            }
        }
    }

    /**
     * 字节数组转换成GenericRecord
     *
     * @param bytes
     * @return
     */
    public GenericRecord getGenericRecord(byte[] bytes) {
        GenericRecord record;
        try {
            record = recordConvertor.binaryToRecord(bytes);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("GenericRecord转换异常，%s", new String(bytes)), e);
        }
        return record;
    }

    /**
     * 如果目前的avro无法成功转换，尝试更新schema
     *
     * @param bytes
     * @return
     */
    public OggRecord getValueTryToChangeSchema(byte[] bytes) {
        OggRecord oggRecord = new OggRecord();
        try {
            // 尝试能否转换成avro
            oggRecord.setGenericRecord(getGenericRecord(bytes));
        } catch (Exception toGenericRecordException) {
            logger.warn("{} 无法转换成avro，尝试能否更新schema." + new String(bytes));
            // 不能转换成avro
            try {
                // 就尝试能否更新schema
                oggRecord.updateSchema(updateSchema(new String(bytes)));
            } catch (Exception updateSchemaException) {
                logger.warn(String.format("%s 更新schema异常！", new String(bytes)), updateSchemaException);
                // 加入转换异常，否则只能看到更新异常
                updateSchemaException.addSuppressed(toGenericRecordException);
                throw updateSchemaException;
            }
        }
        return oggRecord;
    }

    /**
     * 更新schema
     *
     * @param str
     * @return
     */
    private Schema updateSchema(String str) {
        //更新schema
        schema = schemaUtil.getSchemaByString(str);
        //重构记录转换工具类
        recordConvertor = new RecordConvertor(schema);
        logger.info("转换Schema成功：{}", str);
        return schema;
    }

    public Schema getSchema() {
        return schema;
    }

    /**
     * 从文件读取avsc
     *
     * @param file_name
     * @return
     * @throws IOException
     */
    public String avscFromFile(String file_name) throws IOException {
        // 读取文件
        FileCount fileCount;
        FileUtil fileUtils = new FileUtil();
        final StringBuilder avsc = new StringBuilder();
        try {
            fileCount = new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    avsc.append(content);
                }
            };
            fileUtils.setReader(file_name);
            fileUtils.read(fileCount);
        } finally {
            fileUtils.closeRead();
        }
        return avsc.toString();
    }
}
