package com.bussiness.bi.bigdata.txt;

import com.cqx.common.utils.file.FileResult;
import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 高铁数据分析
 *
 * @author chenqixu
 */
public class HtrainCsvParse {
    private static final Logger logger = LoggerFactory.getLogger(HtrainCsvParse.class);

    public static void main(String[] args) throws Exception {
        new HtrainCsvParse().run();
    }

    public void run() throws Exception {
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setReader("d:\\Work\\实时\\高铁乘客识别实时数据对接\\data\\USER_RESIDENT_20230803.csv");
            fileUtil.readByLimit(new FileResult() {
                @Override
                public void run(String content) throws IOException {
                    logger.info("{}", content);
                }
            }, 5);
        } finally {
            fileUtil.closeRead();
        }
    }
}
