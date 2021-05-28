package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroRecord;
import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import org.apache.avro.Schema;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SchemaUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(SchemaUtilTest.class);
    private SchemaUtil schemaUtil;

    @Before
    public void setUp() throws Exception {
        schemaUtil = new SchemaUtil("http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t=");
    }

    @Test
    public void dealOggSchema() throws Exception {
        StringBuilder sb = new StringBuilder();
        Schema schema = null;// schemaUtil.getSchemaByTopic("USER_PRODUCT");

        // 读取文件
        FileCount fileCount;
        FileUtil fileUtil = new FileUtil();
        StringBuilder ogg = new StringBuilder();
        try {
            fileCount = new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    ogg.append(content);
                }
            };
            fileUtil.setReader("d:\\Work\\实时\\ADB\\KafkaToAdb\\avro\\FRTBASE.TB_SER_OGG_USER_ADDI_INFO.avsc");
            fileUtil.read(fileCount);
        } finally {
            fileUtil.closeRead();
        }

        schema = schemaUtil.getSchemaByString(ogg.toString());
        AvroRecord avroRecord = schemaUtil.dealSchema(schema, null);
        String schemaName = schema.getName().toLowerCase();
        String schemaNameSpace = schema.getNamespace().toLowerCase();
        String schemaType = schema.getType().getName();
        sb.append("{\"name\":\"").append(schemaName).append("\",\n" +
                "  \"namespace\":\"").append(schemaNameSpace).append("\",\n" +
                "  \"type\":\"").append(schemaType).append("\",\n" +
                "  \"fields\":[");
        if (avroRecord.hasChild()) {
            for (AvroRecord child : avroRecord.getChilds()) {
                String fatherName = child.getName();
                if (child.hasChild()) {
                    for (AvroRecord _child : child.getChilds()) {
                        sb.append("  {\"name\":\"")
                                .append(fatherName + "_" + _child.getName().toLowerCase())
                                .append("\",\"type\":[\"")
                                .append(_child.getType().getName().toLowerCase());
                        sb.append("\"]},\n");
                    }
                } else {
                    sb.append("  {\"name\":\"")
                            .append(fatherName)
                            .append("\",\"type\":[\"")
                            .append("string")
                            .append("\"]},\n");
                }
            }
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append("]}");
        System.out.println(sb.toString());
    }

    @Test
    public void dealFlatSchema() throws Exception{
        StringBuilder sb = new StringBuilder();
        Schema schema = null;// schemaUtil.getSchemaByTopic("USER_PRODUCT");

        // 读取文件
        FileCount fileCount;
        FileUtil fileUtil = new FileUtil();
        StringBuilder ogg = new StringBuilder();
        try {
            fileCount = new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    ogg.append(content);
                }
            };
            fileUtil.setReader("d:\\Work\\实时\\ADB\\KafkaToAdb\\avro\\FRTBASE.TB_SER_OGG_USER_ADDI_INFO_JAVA.avsc");
            fileUtil.read(fileCount);
        } finally {
            fileUtil.closeRead();
        }

        schema = schemaUtil.getSchemaByString(ogg.toString());
        AvroRecord avroRecord = schemaUtil.dealSchema(schema, null);
        System.out.println(schema);
    }
}