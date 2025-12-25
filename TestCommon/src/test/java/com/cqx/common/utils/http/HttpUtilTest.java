package com.cqx.common.utils.http;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseCallableV1;
import com.cqx.common.utils.thread.ExecutorFactoryV1;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class HttpUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtilTest.class);

    @Test
    public void doGet() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.doGet("http://127.0.0.1:19090/nl-edc-cct-sys-ms-dev/v1/session/code?data=你好");
    }

    @Test
    public void doPost() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.doPost("http://127.0.0.1:19090/nl-edc-cct-sys-ms-dev/v1/session/code?data=你好", new HashMap<>(), "GBK");
    }

    @Test
    public void doPut() throws ExecutionException, InterruptedException {
        ExecutorFactoryV1 ef = ExecutorFactoryV1.newInstance(50);
        List<HttpTest> htlist = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            HttpTest ht1 = new HttpTest();
            htlist.add(ht1);
        }

        TimeCostUtil tc = new TimeCostUtil();
        for (int j = 0; j < 10; j++) {
            tc.start();
            for (int i = 0; i < htlist.size(); i++) {
                ef.submit(htlist.get(i));
            }
            ef.joinAndClean();
            long cost = tc.stopAndGet();
            logger.info("all_cost={}", cost);

            for (int i = 0; i < htlist.size(); i++) {
                htlist.get(i).restart();
            }
            if (cost < 1000L) {
                SleepUtil.sleepMilliSecond(1000L - cost);
            } else {
                logger.info("本次耗时太长");
            }
        }
        ef.stop();
    }

    /**
     * 从Multipart中打印表单内容
     *
     * @throws Exception
     */
    @Test
    public void MultipartTest() throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("title", "SDP.txt");
        paramMap.put("fileType", "txt");
        InputStream inputStream = new FileInputStream("d:\\tmp\\SDP.txt");

        //解决中文乱码问题
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file"
                , inputStream
                , ContentType.DEFAULT_BINARY.withCharset("UTF-8")
                , String.valueOf(paramMap.get("title")));

        //(这个是要调的接口的参数，根据实际情况组装)解决文件名中文乱码（？？？）
        builder.addTextBody("title"
                , String.valueOf(paramMap.get("title"))
                , ContentType.DEFAULT_TEXT.withCharset("UTF-8"));
        builder.addTextBody("fileType"
                , String.valueOf(paramMap.get("fileType")));

        HttpEntity entity = builder.build();
        logger.info("entity={}", entity);
        HttpUtil.printlnHttpEntity(entity);
    }

    public class HttpTest extends BaseCallableV1 {
        HttpUtil httpUtil = new HttpUtil();
        HashMap<String, String> header = new HashMap<>();
        TimeCostUtil tc = new TimeCostUtil();

        public HttpTest() {
            httpUtil.setKeepAlive();
            header.put("Content-type", "application/json");
            header.put("charset", "UTF-8");
            header.put("Connection", "keep-alive");
            header.put("reqChannelId", "C000201");
            header.put("SecretAuthorization", " 50766a5c-844c-42ef-9f4f-2a5f0d6fe7d6");
        }

        @Override
        public void exec() throws Exception {
            tc.start();
            String uuid = UUID.randomUUID().toString();
            String data = "{\"portraitId\":\"P00000010030\",\"msisdn\": \"13509323824\",\"queryType\": 2,\"tags\": [\"" + uuid + "\"]}";
            String res = httpUtil.doPost("http://10.1.8.203:21801/edc-label-query-service/qryPortrait"
                    , header, data);
            logger.info("cost={}ms, res={}", tc.stopAndGet(), res);
            stop();
        }
    }
}