package com.bussiness.bi.bigdata.realtimemonitor.monitor;

import com.bussiness.bi.bigdata.realtimemonitor.rule.RuleUtil;
import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;

import java.io.IOException;

/**
 * MonitorBolt
 *
 * @author chenqixu
 */
public class MonitorBolt {

    public void monitor(String fileName) throws Exception {
        final RuleUtil ruleUtil = new RuleUtil();
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setReader(fileName);
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    try {
                        ruleUtil.check(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } finally {
            fileUtil.closeRead();
        }
    }
}
