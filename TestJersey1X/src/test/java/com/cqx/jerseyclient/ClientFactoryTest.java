package com.cqx.jerseyclient;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.download.yaoqi.YaoqiParserTest;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ClientFactoryTest {
    private static final MyLogger logger = MyLoggerFactory.getLogger(YaoqiParserTest.class);

    @Test
    public void postFile() {
        boolean result = ClientFactory.getInstance()
                .buildFile()
                .addFile(new File("d:\\tmp\\1.txt"))
                .postFile("http://localhost:19192/server/call/upload", Boolean.class);
        logger.info("result：{}", result);
    }

    @Test
    public void postBatchFile() {
        boolean result = ClientFactory.getInstance()
                .buildFile()
                .addFile(new File("d:\\tmp\\1.txt"))
                .addFile(new File("d:\\tmp\\2.txt"))
                .postFile("http://localhost:19192/server/call/batch/upload", Boolean.class);
        logger.info("result：{}", result);
    }

    @Test
    public void callComponent() {
        ClientFactory clientFactory = ClientFactory.getInstance();
        clientFactory.postJSON("http://10.1.8.203:19192/server/call/callComponent",
                "{\"className\":\"com.newland.component.FujianBI.impl.ComponentNoWorkTest\"," +
                        "\"param\":\"<dog xmlns='uri:dog' id='node2880'>" +
                        "<desc><![CDATA[组件测试类]]></desc>" +
                        "<component name='component_no_work_test' version='1.0'>" +
                        "<param name='hdfs_name'><![CDATA[localfs]]></param>" +
                        "</component>" +
                        "</dog>\"," +
                        "\"task_id\":\"100000000001\"}");
        while (clientFactory.get("http://10.1.8.203:19192/server/call/get_task_status/100000000001", Integer.class) != 0) {
            List<String> logs = clientFactory.get("http://10.1.8.203:19192/server/call/get_task_log/100000000001", List.class);
            for (String log : logs) {
                logger.info("log：{}", log);
            }
            SleepUtil.sleepMilliSecond(500);
        }
        //日志有可能没消费完，再消费一次
        List<String> logs = clientFactory.get("http://10.1.8.203:19192/server/call/get_task_log/100000000001", List.class);
        for (String log : logs) {
            logger.info("log：{}", log);
        }
        //最后释放任务
        clientFactory.get("http://10.1.8.203:19192/server/call/release_task/100000000001");
    }
}