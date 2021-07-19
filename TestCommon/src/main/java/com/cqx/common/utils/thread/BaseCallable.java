package com.cqx.common.utils.thread;

import com.cqx.common.utils.file.FileConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * BaseCallable
 *
 * @author chenqixu
 */
public abstract class BaseCallable<V> implements Callable<List<V>> {
    private static final Logger logger = LoggerFactory.getLogger(BaseCallable.class);
    private AtomicBoolean runFlag = new AtomicBoolean(true);
    private AtomicLong max_size = new AtomicLong(0L);
    private long commitTime;

    public void init(Map params) {
        String max_poll_size = params.get(FileConsumer.MAX_POLL_SIZE).toString();
        if (max_poll_size != null) {
            setMax_size(Long.valueOf(max_poll_size));
        }
    }

    public void restart(long timeout) {
        this.commitTime = System.currentTimeMillis() + timeout;
        this.runFlag.compareAndSet(false, true);
    }

    private boolean isStop() {
        return runFlag.get();
    }

    public void stop() {
        runFlag.set(false);
    }

    @Override
    public List<V> call() throws Exception {
        List<V> vList = new ArrayList<>();
        logger.debug("{} start.", this);
        while (isStop()) {
            try {
                if ((max_size.get() > 0 && (vList.size() == max_size.get()))
                        ||
                        (System.currentTimeMillis() >= commitTime)) {
                    stop();
                } else {
                    V v = exec();
                    if (v != null) {
                        vList.add(v);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }
        logger.debug("{} stop.", this);
        return vList;
    }

    public abstract V exec() throws Exception;

    public void setMax_size(long max_size) {
        this.max_size.set(max_size);
    }
}
