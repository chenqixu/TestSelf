package com.newland.bi.bigdata.thread;

import com.alibaba.fastjson.JSON;
import com.newland.bi.bigdata.changecode.FileUtil;
import com.newland.storm.component.etl.hdfs.common.AbstractHDFSWriter;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriterBeanTest {
    @Test
    public void test() {
        FileUtil fileUtil = new FileUtil();
        List<String> list = fileUtil.read("D:\\tmp\\logs\\getData.log", "GBK");
        String jsonstr = list.get(0);
        List<WriterBean> resultlist = JSON.parseArray(jsonstr, WriterBean.class);
        Map<String, WriterBean> map = new HashMap<>();
        for (WriterBean writerBean : resultlist) {
            map.put(writerBean.toJson(), writerBean);
//            System.out.println(writerBean.getStartTime()+" "+writerBean.toJson());
            System.out.println(writerBean.getPathAndFileName());
        }
        System.out.println(map.size());
//        System.out.println(JSON.toJSONString(map.values()));
    }

    @Test
    public void testPath() {
//        Path path = new Path("hdfs://edc01:8020/usr/test/data/rc_hw/x2");
        Path path = new Path("/usr/test/data/rc_hw/x2");
        System.out.println(path.toUri().toString());
        System.out.println(path.toUri().getPath());
        System.out.println(path.toUri().getScheme());
        System.out.println(path.toUri().getAuthority());
//        System.out.println(path.toUri().getFragment());
//        System.out.println(path.toUri().getHost());
//        System.out.println(path.toUri().getPort());
//        System.out.println(path.toUri().getQuery());
//        System.out.println(path.toUri().getRawAuthority());
//        System.out.println(path.toUri().getRawFragment());
//        System.out.println(path.toUri().getRawPath());
//        System.out.println(path.toUri().getRawQuery());
//        System.out.println(path.toUri().getRawSchemeSpecificPart());
//        System.out.println(path.toUri().getRawUserInfo());
    }
}