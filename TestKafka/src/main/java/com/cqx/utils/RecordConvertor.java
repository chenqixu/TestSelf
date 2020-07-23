package com.cqx.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * RecordConvertor
 *
 * @author chenqixu
 */
public class RecordConvertor {

    private final DatumReader<GenericRecord> reader;
    private final DatumWriter<GenericRecord> writer;

    public RecordConvertor(Schema schema) {
        reader = new SpecificDatumReader<>(schema);
        writer = new SpecificDatumWriter<>(schema);
    }

    public RecordConvertor(SchemaUtil schemaUtil, String topic) {
        Schema schema;
        if (schemaUtil.isLocal()) {
            schema = schemaUtil.getSchemaByTopic(topic);
        } else {
            schema = schemaUtil.getSchemaByUrlTopic(topic);
        }
        reader = new SpecificDatumReader<>(schema);
        writer = new SpecificDatumWriter<>(schema);
    }

    public RecordConvertor(String topic) {
        this("", topic);
    }

    public RecordConvertor(String schemaUrl, String topic) {
        SchemaUtil schemaUtil;
        if (schemaUrl != null && schemaUrl.length() > 0) {
            schemaUtil = new SchemaUtil(schemaUrl);
        } else {
            schemaUtil = new SchemaUtil();
        }
        Schema schema;
        if (schemaUtil.isLocal()) {
            schema = schemaUtil.getSchemaByTopic(topic);
        } else {
            schema = schemaUtil.getSchemaByUrlTopic(topic);
        }
        reader = new SpecificDatumReader<>(schema);
        writer = new SpecificDatumWriter<>(schema);
    }

    public GenericRecord binaryToRecord(byte[] msgByte) {
        Decoder decoder = DecoderFactory.get().binaryDecoder(msgByte, null);
        try {
            return reader.read(null, decoder);
        } catch (IOException e) {
            //log.error("binary message convert to record fail !"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public byte[] recordToBinary(GenericRecord record) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        try {
            writer.write(record, encoder);
            encoder.flush();
            out.close();
        } catch (IOException e) {
            //log.error("record to binary message convert fail !"+e.getMessage());
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
