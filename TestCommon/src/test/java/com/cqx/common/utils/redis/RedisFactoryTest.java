package com.cqx.common.utils.redis;

import com.cqx.common.bean.javabean.ErrorBean;
import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.client.RedisPipeline;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.system.TimeUtil;
import com.cqx.common.utils.thread.CallableTool;
import com.cqx.common.utils.thread.ICallableTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class RedisFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisFactoryTest.class);
    private RedisClient redisClient;
    private int num = 1000;
    private int add = 1;
    private boolean isShowResult = true;
    private Cluster cluster;

    @Before
    public void setUp() throws Exception {
        redisClient = RedisFactory.builder()
//                .setIp("10.1.8.200")
//                .setPort(10009)
//                .setMode(RedisFactory.SINGLE_MODE_TYPE)
                .setIp_ports("10.1.8.200:10000,10.1.8.201:10000,10.1.8.202:10000")
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .setPipeline(true)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        if (redisClient != null) redisClient.close();
    }

    @Test
    public void ping() {
        try (Jedis jedis = new Jedis("10.1.8.200", 10009)) {
            TimeCostUtil tc = new TimeCostUtil();
            tc.start();
            for (int i = 0; i < 1; i++)
                jedis.ping();
            logger.info("cost：{}", tc.stopAndGet());
        }
        String _ci = "";
        Long ci = ((_ci == null || _ci.length() == 0) ? null : Long.valueOf(_ci));
        logger.info("ci：{}", ci);
    }

    @Test
    public void linkedTest() {
        //测试
        LinkedBlockingQueue<Integer> getCache = new LinkedBlockingQueue<>(2);
        for (int i = 0; i < 5; i++) {
            boolean result = getCache.offer(i);
            logger.info("i：{}，result：{}", i, result);
        }
        List<Object> copy = new ArrayList<>(getCache);
        getCache.clear();
        logger.info("getCache：{}，copy：{}", getCache, copy);

        Map<String, Integer> pipelinePool = new HashMap<>();
        for (int i = 1; i < 10; i++) pipelinePool.put("" + i, i);
        for (int j = 0; j < 5; j++)
            for (int i : pipelinePool.values()) {
                logger.info("{} {}", j, i);
            }
    }

    @Test
    public void set() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        while (add <= num) {
            redisClient.set(add + "", add++ + "");
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        isShowResult = false;//查询结果不显示
        //查询
//        redisQuery();
    }

    @Test
    public void clusterOffSet() {
        redisClient.set("0", "0");
    }

    @Test
    public void del() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        while (add <= num) {
            redisClient.del(add++ + "");
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        //查询
//        redisQuery();
    }

    @Test
    public void get() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        List<String> get = new ArrayList<>();
        while (add <= num) {
            get.add(redisClient.get(add++ + ""));
        }
        logger.info("cost：{}，get：{}", costUtil.stopAndGet(), get);
    }

    @Test
    public void redisQuery() {
        int cnt = 0;
        for (String key : redisClient.keys("*")) {
            if (isShowResult) {
                String type = redisClient.type(key);
                switch (type) {
                    case "none":
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                    case "string":
                        logger.info("key：{}，type：{}，value：{}", key, type, redisClient.get(key));
                        break;
                    case "set":
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                    case "zset":
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                    case "hash":
                        logger.info("key：{}，type：{}，value：{}", key, type, redisClient.hgetAll(key));
                        break;
                    default:
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                }
            }
            cnt++;
        }
        logger.info("cnt：{}", cnt);
    }

    @Test
    public void pipelineGet() {
        List<Object> objectList;
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline(500, 2000)) {
            while (add <= num) {
                redisPipeline.request_get(add + "");
                if (add % 499 == 0) {
                    objectList = redisPipeline.get();
                    logger.info("add：{}，size：{}，objectList：{}", add, objectList.size(), objectList);
                }
                add++;
            }
            objectList = redisPipeline.get();
            logger.info("size：{}，objectList：{}", objectList.size(), objectList);
        }
        logger.info("cost：{}", costUtil.stopAndGet());
    }

    @Test
    public void pipelineSet() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.set(add + "", add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        isShowResult = false;//查询结果不显示
        //查询
//        redisQuery();
    }

    @Test
    public void pipelineSetex() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.setex(add + "", 10, add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        isShowResult = false;//查询结果不显示
        //查询
//        redisQuery();
    }

    @Test
    public void pipelineDel() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.del(add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        //查询
//        redisQuery();
    }

    @Test
    public void pipelineHSet() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.hset("pipelineHSet", add + "", add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
    }

    @Test
    public void pipelineHGet() {
        List<Object> objectList;
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.request_hget("pipelineHSet", add + "");
                if (add % 100 == 0) {
                    objectList = redisPipeline.get();
                    logger.info("add：{}，size：{}，objectList：{}", add, objectList.size(), objectList);
                }
                add++;
            }
            objectList = redisPipeline.get();
            logger.info("size：{}，objectList：{}", objectList.size(), objectList);
        }
        logger.info("cost：{}", costUtil.stopAndGet());
    }

    @Test
    public void pipelienHDel() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.hdel("pipelineHSet", add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
    }

    @Test
    public void flushDB() {
//        redisClient.flushDB();
    }

    @Test
    public void testServerDown() {
        int max = 5;
        for (int i = 0; i < max; i++) {
            try {
                pipelineSet();
                add = 1;
//                pipelineDel();
//                add = 1;
                pipelineGet();
//                get();
            } catch (Exception e) {
                logger.warn("===error , sleep 2");
//                logger.error(e.getMessage(), e);
                SleepUtil.sleepSecond(2);
            }
            if (i != (max - 1)) {
                logger.warn("===wait 3");
                SleepUtil.sleepSecond(3);
            }
            add = 1;
        }
    }

    @Test
    public void testRecursion() {
        cluster = new Cluster();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("timer.schedule 4000，dead 1");
                cluster.dead(1);
            }
        }, 4000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("timer.schedule 8000，resurrection 1");
                cluster.resurrection(1);
            }
        }, 8000);
        for (int i = 0; i < 5; i++) {
            List<Slot> dis = discover();
            logger.info("dis：{}", dis);
            SleepUtil.sleepSecond(3);
        }
    }

    @Test
    public void hgetTest() {
        String key = "error:kafka_single_partition_sync_users";
        Map<String, String> valMap = redisClient.hgetAll(key);
        logger.info("valMap：{}，valMap is null：{}，valMap.size：{}", valMap, valMap == null, valMap.size());
        ErrorBean errorBean = new ErrorBean();
        if (valMap != null && valMap.size() > 0) {
            String field = null;
            long maxSeq = 0L;
            for (Map.Entry<String, String> entry : valMap.entrySet()) {
                String[] value_array = entry.getValue().split(",", -1);
                if (value_array != null && value_array.length >= 3) {
                    long tmpSeq = Long.valueOf(value_array[0]);
                    if (maxSeq == 0L) {
                        field = entry.getKey();
                        maxSeq = tmpSeq;
                    }
                    if (tmpSeq > maxSeq) {
                        field = entry.getKey();
                        maxSeq = tmpSeq;
                    }
                }
            }
            logger.info("field：{}，maxSeq：{}", field, maxSeq);
            errorBean.setIndex(Integer.valueOf(field));
            int new_cnt = errorBean.newError();
            String errorVal = errorBean.getErrorVal("异常测试");
            logger.info("new_cnt：{}，errorVal：{}", new_cnt, errorVal);
            redisClient.hset(key, new_cnt + "", errorVal);
        } else {
            int new_cnt = errorBean.newError();
            String errorVal = errorBean.getErrorVal("异常测试");
            logger.info("new_cnt：{}，errorVal：{}", new_cnt, errorVal);
            redisClient.hset(key, new_cnt + "", errorVal);
        }
    }

    @Test
    public void hsetTest() {
        String key = "error:kafka_single_partition_sync_users";
        redisClient.hset(key, "1", "1000,time,errMsg");
        redisClient.hset(key, "2", "1001,time,errMsg");
        redisClient.hset(key, "3", "1002,time,errMsg");
        redisClient.hset(key, "4", "1003,time,errMsg");
        redisClient.hset(key, "5", "999,time,errMsg");
        redisClient.hdel(key, "1");
        redisClient.hdel(key, "2");
        redisClient.hdel(key, "3");
        redisClient.hdel(key, "4");
        redisClient.hdel(key, "5");
    }

    @Test
    public void setStatusTest() {
        List<String> keys = new ArrayList<>();
        keys.add("status:kafka_single_partition_sync_users");
        keys.add("status:kafka_single_partition_sync_itv_users");
        for (String key : keys) {
            redisClient.set(key, "run");
            String val = redisClient.get(key);
            logger.info("key：{}，val：{}", key, val);
        }
    }

    @Test
    public void concurrentINCRTest() {
        CallableTool<Long> callableTool = new CallableTool<>(5);
        INCRGet incrGet1 = new INCRGet("hincrget1", "4G", "20220427-01");
        INCRGet incrGet2 = new INCRGet("hincrget2", "4G", "20220427-01");
        INCRGet incrGet3 = new INCRGet("hincrget3", "4G", "20220427-01");
        callableTool.submitCallable(incrGet1);
        callableTool.submitCallable(incrGet2);
        callableTool.submitCallable(incrGet3);
        callableTool.await();
        callableTool.stop();
    }

    @Test
    public void timerTest() {
        logger.info("now: {}", TimeUtil.getNow());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("timer.schedule 4000");
            }
        }, 5, 4000);
        SleepUtil.sleepSecond(20);
        logger.info("end: {}", TimeUtil.getNow());
    }

    @Test
    public void setexTest() {
        String key = "123ex";
        String ret = redisClient.setex(key, 3, "---");
        logger.info("ret: {}", ret);
        for (int i = 0; i < 4; i++) {
            SleepUtil.sleepSecond(1);
            logger.info("sleep 1 get: {}", redisClient.get(key));
        }
    }

    private List<Slot> discover() {
        //轮询缓存，ping不通过的移除
        //重新获取集群
        //先测试下集群是否都能ping
        //直到都能ping通
        //已经在缓存的跳过，不在缓存的加入
        List<Slot> cache = new ArrayList<>(getSlots());
        boolean cachePing = testPing(cache, true);
        if (!cachePing) {
            List<Slot> slots = getSlots();
            while (!testPing(slots, false)) {
                slots = getSlots();
                SleepUtil.sleepMilliSecond(500);
            }

            for (Slot newSlot : slots) {
                if (!cache.contains(newSlot)) {
                    logger.info("cache add：{}", newSlot);
                    cache.add(newSlot);
                }
            }
        }
        return cache;
    }

    private boolean testPing(List<Slot> src, boolean isRemove) {
        logger.info("testPing：{}", src);
        for (Iterator<Slot> it = src.iterator(); it.hasNext(); ) {
            Slot old = it.next();
            if (!old.ping().equals("PONG")) {
                if (isRemove) {
                    //从缓存移除
                    logger.info("remove：{}", old);
                    it.remove();
                }
                return false;
            }
        }
        return true;
    }

    private List<Slot> getSlots() {
        return cluster.getSlots();
    }

    class Cluster {
        volatile List<Slot> cluster = new ArrayList<>();

        Cluster() {
            Slot s1 = new Slot(1);
            Slot s2 = new Slot(2);
            Slot s3 = new Slot(3);
            cluster.add(s1);
            cluster.add(s2);
            cluster.add(s3);
        }

        List<Slot> getSlots() {
            return cluster;
        }

        void dead(int i) {
            cluster.get(i).setStatus("DEAD");
        }

        void resurrection(int i) {
            cluster.get(i).setStatus("PONG");
        }

        int size() {
            return cluster.size();
        }
    }

    class Slot {
        int port;
        String status = "PONG";

        Slot(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return getPort() + "，" + ping();
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String ping() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class INCRGet extends ICallableTool<Long> {
        private RedisClient _redisClient;
        private String key;
        private String field;

        public INCRGet(String taskName, String key, String field) {
            super(taskName);
            this.key = key;
            this.field = field;
            this._redisClient = RedisFactory.builder()
                    .setIp_ports("10.1.8.200:10000,10.1.8.201:10000,10.1.8.202:10000")
                    .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                    .setPipeline(false)
                    .build();
        }

        @Override
        public Long icall() throws Exception {
            long cnt = 0;
            while (cnt < 1000) {
                cnt++;
                long val = _redisClient.hincrBy(key, field, 1L);
                logger.info("val: {}", val);
            }
            _redisClient.close();
            return cnt;
        }
    }
}