package com.cqx.common.utils.kafka;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import java.util.List;

/**
 * Ogg业务处理类
 *
 * @author chenqixu
 */
public interface OggPollInf {

    /**
     * 业务数据处理
     *
     * @param genericRecords
     * @throws Exception
     */
    void dataDeal(List<GenericRecord> genericRecords) throws Exception;

    /**
     * 更新schema
     *
     * @param schema
     */
    void updateSchema(Schema schema);
}
