package com.newland.bi.bigdata.memory;

import com.newland.bi.bigdata.redis.RedisClient;
import com.newland.bi.bigdata.redis.RedisFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

public class ShareMemoryCacheTest {

    public static final String newLine = System.getProperty("line.separator");
    private ShareMemoryCache shareMemoryCache;
    private String fileName;
    private RedisClient rc;

    @Before
    public void setUp() throws Exception {
        shareMemoryCache = new ShareMemoryCache();
        fileName = "d:\\test\\yyzs\\20190110141813\\epg";
    }

    @Test
    public void read() throws Exception {
        shareMemoryCache.createRandomAccessFile(fileName, ShareMemoryCache.MemoryCacheMode.READ_ONLY);
        shareMemoryCache.read();
        shareMemoryCache.close();
    }

    @Test
    public void write() throws Exception {
        rc = RedisFactory.builder()
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                // 开发
                .setIp_ports("10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385")
                .build();
        fileName = "d:\\test\\yyzs\\20190110141813\\xml";
        shareMemoryCache.createRandomAccessFile(fileName, ShareMemoryCache.MemoryCacheMode.READ_WRITE);
        Map<String, String> resultMap = rc.hgetAll("06006008");
        Iterator<Map.Entry<String, String>> iterator = resultMap.entrySet().iterator();
        while (iterator.hasNext()) {
            shareMemoryCache.write(iterator.next().getValue());
            if (iterator.hasNext())
                shareMemoryCache.write(newLine);
        }
        shareMemoryCache.close();
        rc.close();
    }

    @Test
    public void lock() throws Exception {
        new ShareMemoryCache().createRandomAccessFile(fileName, ShareMemoryCache.MemoryCacheMode.READ_WRITE);
        new ShareMemoryCache().createRandomAccessFile(fileName, ShareMemoryCache.MemoryCacheMode.READ_WRITE);
    }
}