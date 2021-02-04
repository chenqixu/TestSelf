package com.cqx.common.model.stream;

import com.cqx.common.utils.thread.BaseRunableThread;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * StreamBolt
 *
 * @author chenqixu
 */
public class StreamBolt<T> extends BaseRunableThread {
    private LinkedBlockingQueue<T> data = new LinkedBlockingQueue<>(100);
    private AtomicLong cnt = new AtomicLong(0L);
    private IStreamBolt<T> iStreamBolt;

    public StreamBolt(IStreamBolt<T> iStreamBolt) {
        this.iStreamBolt = iStreamBolt;
    }

    public void init(Map params) {
        this.iStreamBolt.prepare(params);
    }

    public void add(T t) {
        try {
            data.put(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void runnableExec() throws Exception {
        T t = data.poll();
        if (t != null) {
            iStreamBolt.execute(t);
            cnt.incrementAndGet();
        }
    }

    public long getCnt() {
        return cnt.get();
    }

    @Override
    protected void runnableLastExec() throws Exception {
        T t;
        while ((t = data.poll()) != null) {
            iStreamBolt.execute(t);
            cnt.incrementAndGet();
        }
        iStreamBolt.clean();
    }
}
