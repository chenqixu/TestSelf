package com.cqx.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.CompatibleDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * RecordConvertorIgnore
 * <pre>
 *     忽略不一致字段的版本
 * </pre>
 *
 * @author chenqixu
 */
public class RecordConvertorIgnore {

    private final DatumReader<GenericRecord> reader;
    private final DatumWriter<GenericRecord> writer;

    /**
     * @param schema
     */
    public RecordConvertorIgnore(Schema schema) {
        reader = new CompatibleDatumReader<>(schema);
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
