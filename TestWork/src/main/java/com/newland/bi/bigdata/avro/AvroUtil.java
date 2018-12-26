package com.newland.bi.bigdata.avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * avro工具类
 *
 * @author chenqixu
 */
public class AvroUtil {

    private static final Logger logger = LoggerFactory.getLogger(AvroUtil.class);

    public byte[] serializeAvroToByte(SpecificRecordBase specificRecordBase) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DatumWriter<SpecificRecordBase> datumWriter = new SpecificDatumWriter<>();
        DataFileWriter<SpecificRecordBase> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(specificRecordBase.getSchema(), baos);
        dataFileWriter.append(specificRecordBase);
        dataFileWriter.close();
        return baos.toByteArray();
    }

    public SpecificRecordBase deserialzeAvroFromByte(byte[] avrobyte) throws IOException {
        SeekableByteArrayInput seekableByteArrayInput = new SeekableByteArrayInput(avrobyte);
        DatumReader<SpecificRecordBase> datumReader = new SpecificDatumReader<>();
        DataFileReader<SpecificRecordBase> dataFileReader = new DataFileReader<>(seekableByteArrayInput, datumReader);
        SpecificRecordBase specificRecordBase = null;
        // 只取一条记录
        while (dataFileReader.hasNext()) {
            specificRecordBase = dataFileReader.next(specificRecordBase);
            logger.debug("specificRecordBase：{}", specificRecordBase);
            break;
        }
        return specificRecordBase;
    }

    public void serializeAvroToFile(SpecificRecordBase specificRecordBase, String fileName) throws IOException {
        DatumWriter<SpecificRecordBase> datumWriter = new SpecificDatumWriter<>();
        DataFileWriter<SpecificRecordBase> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(specificRecordBase.getSchema(), new File(fileName));
        dataFileWriter.append(specificRecordBase);
        dataFileWriter.close();
    }

    public void deserializeAvroFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        DatumReader<SpecificRecordBase> datumReader = new SpecificDatumReader<>();
        DataFileReader<SpecificRecordBase> dataFileReader = new DataFileReader<>(file, datumReader);

    }
}
