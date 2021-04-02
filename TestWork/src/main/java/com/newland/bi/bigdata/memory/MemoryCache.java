package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.redis.client.RedisClient;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 缓存
 *
 * @author chenqixu
 */
public class MemoryCache {
    private static final long TIMER_DELAY = 500l;
    private Map<String, String> cacheold;
    private Map<String, String> cachenew;
    private String changeTag = "old";
    private Timer timer;
    private long timer_period = 10 * 60 * 1000l;//间隔默认10分钟
    private RedisClient redisClient;
    private String key;

    public MemoryCache(long timer_period, String key) {
        this.timer_period = timer_period;
        this.key = key;
    }

    private void init() {
        timer = new Timer(true);
        timer.schedule(new MemoryCacheTimer(), TIMER_DELAY, timer_period);
        //初始化到old
        cacheold = change();
    }

    private Map<String, String> change() {
        return redisClient.hgetAll(key);
    }

    public Map<String, String> getCache() {
        if (changeTag.equals("new"))
            return cachenew;
        else
            return cacheold;
    }

    class MemoryCacheTimer extends TimerTask {

        @Override
        public void run() {
            //当前在new，需要切换到old
            if (changeTag.equals("new")) {
                cacheold.clear();
                cacheold = change();
                //切换成功
                changeTag = "old";
            }
            //当前在old，需要切换到new
            else {
                cachenew.clear();
                cachenew = change();
                //切换成功
                changeTag = "new";
            }
        }
    }
}
