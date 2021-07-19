package com.cqx.common.utils.file;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileConsumerTest {
    private static final Logger logger = LoggerFactory.getLogger(FileConsumerTest.class);

    @Test
    public void poll() throws IOException {
        String fileName = "d:\\tmp\\data\\dpi\\dpi_s1mme\\streaminput\\LTE_S1MME_026679650002_20190529171000.txt";
        Map params = new HashMap();
        params.put(FileConsumer.MAX_POLL_SIZE, 400);
        try (FileConsumer fileConsumer = new FileConsumer(params)) {
            fileConsumer.init(fileName);
            for (int i = 0; i < 10; i++) {
                List<FileRecord> contents = fileConsumer.poll(100L);
                logger.info("read.size：{}", contents.size());
                // 业务处理
                if (contents.size() > 0) {
                    FileRecord lastRecord = contents.get(contents.size() - 1);
                    logger.info("lastRecord：{}", lastRecord);
                    // 提交
                    fileConsumer.commit(lastRecord.getOffset());
                }
            }
        }
    }

    @Test
    public void access() throws IOException {
        String fileName = "d:\\tmp\\data\\dpi\\dpi_s1mme\\streaminput\\LTE_S1MME_TEST.txt";
        try (FileConsumer fileConsumer = new FileConsumer()) {
            fileConsumer.init(fileName);
            for (int i = 0; i < 3; i++) {
                List<FileRecord> contents = fileConsumer.poll(2L);
                logger.info("read.size：{}", contents.size());
                // 业务处理
                if (contents.size() > 0) {
                    FileRecord lastRecord = contents.get(contents.size() - 1);
                    logger.info("lastRecord：{}", lastRecord);
                    // 提交
                    fileConsumer.commit(lastRecord.getOffset());
                }
            }
        }
    }
}