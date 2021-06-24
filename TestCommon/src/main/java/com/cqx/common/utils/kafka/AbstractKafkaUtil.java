package com.cqx.common.utils.kafka;

import com.cqx.common.utils.list.IKVList;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * IKafkaUtil
 *
 * @author chenqixu
 */
public abstract class AbstractKafkaUtil<K, V> {

    public boolean callBack(ConsumerRecord<K, V> records) {
        return false;
    }

    public boolean callBack(List<IKVList.Entry<K, V>> records) {
        return false;
    }
}
