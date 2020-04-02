package com.newland.bi.bigdata.memory;

import com.newland.bi.bigdata.metric.MetricsUtil;
import com.newland.bi.bigdata.redis.RedisClient;
import com.newland.bi.bigdata.redis.RedisFactory;
import com.newland.bi.bigdata.utils.string.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShareMemoryCacheTest {

    public static final String newLine = System.getProperty("line.separator");
    private ShareMemoryCache shareMemoryCache;
    private String fileName;
    private RedisClient rc;
    private String datafilename = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\data\\yyzs\\memoryfile.txt";
    private String idfilename = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\data\\yyzs\\FA4403000047351000000495735CE5D0.txt";
    private String datapath = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\data\\yyzs";
    private List<String> idfiles;
    private MemoryTest memoryTest;

    @Before
    public void setUp() throws Exception {
        shareMemoryCache = ShareMemoryCache.newbuilder();
        fileName = "d:\\test\\yyzs\\20190110141813\\epg";
        memoryTest = MemoryTest.builder();
        idfiles = memoryTest.getIdfiles();
    }

    @Test
    public void read() throws Exception {
//        shareMemoryCache.createRandomAccessFile(fileName, ShareMemoryCache.MemoryCacheMode.READ_ONLY);
        shareMemoryCache.createRandomAccessFile(fileName, MemoryCacheMode.READ_WRITE);
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
        shareMemoryCache.createRandomAccessFile(fileName, MemoryCacheMode.READ_WRITE);
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
        ShareMemoryCache.newbuilder().createRandomAccessFile(fileName, MemoryCacheMode.READ_WRITE);
        ShareMemoryCache.newbuilder().createRandomAccessFile(fileName, MemoryCacheMode.READ_WRITE);
    }

    @Test
    public void hash() throws Exception {
        System.out.println("567".hashCode() % 10);
        System.out.println("1234".hashCode() % 10);
//        for (String str : idfiles) {
//            System.out.println(str + " " + Math.abs(str.hashCode()) % 3000000);
//        }
    }

    @Test
    public void getData() throws Exception {
        MetricsUtil ms = MetricsUtil.builder();
        ms.addTimeTag();
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder()
                .setDatafilename(datafilename)
                .setIdfilename(idfilename);
        shareMemoryCache.init();
        System.out.println(shareMemoryCache.getData() + "，spend：" + ms.getTimeOut());
        shareMemoryCache.closeAll();
    }

    @Test
    public void getDataFromFile() throws Exception {
        MetricsUtil ms = MetricsUtil.builder();
        ms.addTimeTag();
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder()
                .setDatafilename(datafilename);
        shareMemoryCache.init();
//        List<String> idfiles = new ArrayList<>();
//        idfiles.add(StringUtils.getEndsWithPath(datapath) + "123.txt");
//        idfiles.add(StringUtils.getEndsWithPath(datapath) + "456.txt");
        for (String filename : idfiles) {
            shareMemoryCache.getDataFromFile(StringUtils.addPathAndSuffix(filename, datapath, ".txt"));
        }
        System.out.println("spend：" + ms.getTimeOut());
        shareMemoryCache.closeAll();
    }

    @Test
    public void getDataFromFiles() throws Exception {
        MetricsUtil ms = MetricsUtil.builder();
        ms.addTimeTag();
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder()
                .setDatafilename(datafilename);
        shareMemoryCache.init();
//        shareMemoryCache.getDataFromFile(datapath, "1234", 10);
//        shareMemoryCache.getDataFromFile(datapath, "567", 10);
        for (String _tmp : idfiles) {
            shareMemoryCache.getDataFromFile(datapath, _tmp, 10);
        }
        System.out.println("spend：" + ms.getTimeOut());
        shareMemoryCache.closeAll();
    }

    @Test
    public void writeData() throws Exception {
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder()
                .setDatafilename(datafilename);
        shareMemoryCache.init();
//        List<String> idfiles = new ArrayList<>();
//        idfiles.add(StringUtils.getEndsWithPath(datapath) + "123.txt");
//        idfiles.add(StringUtils.getEndsWithPath(datapath) + "456.txt");
//        shareMemoryCache.writeData(idfiles, 3);
        for (String _tmp : idfiles) {
            shareMemoryCache.writeData(datapath, _tmp, 3, 10);
        }
//        shareMemoryCache.writeData(datapath, "567", 3, 10);
//        shareMemoryCache.writeData(datapath, "1234", 3, 10);
        shareMemoryCache.closeAll();
    }

    @Test
    public void writeDatas() throws Exception {
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder()
                .setDatafilename(datafilename);
        shareMemoryCache.init();
        List<String> newlist = StringUtils.addPathAndSuffix(idfiles, datapath, ".txt");
        shareMemoryCache.writeData(newlist, 3);
        shareMemoryCache.closeAll();
    }

    @Test
    public void createRandomFileName() {
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder();
        StringUtils.printList(shareMemoryCache.createRandomFileName(datapath, 1000));
    }

    @Test
    public void cleanfile() {
        ShareMemoryCache shareMemoryCache = ShareMemoryCache
                .newbuilder();
        shareMemoryCache.cleanfile(datapath);
    }
}