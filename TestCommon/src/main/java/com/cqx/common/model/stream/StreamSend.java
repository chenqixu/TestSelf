package com.cqx.common.model.stream;

import com.cqx.common.utils.thread.BaseRunableThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * StreamSend
 *
 * @author chenqixu
 */
public class StreamSend<T> extends BaseRunableThread {
    private LinkedBlockingQueue<T> data;
    private List<StreamBolt<T>> streamBoltList;
    private Map<Long, String> equalMap;

    public StreamSend(LinkedBlockingQueue<T> data) {
        this.data = data;
        this.streamBoltList = new ArrayList<>();
        this.equalMap = new HashMap<>();
    }

    public void add(IStreamBolt<T> iStreamBolt) {
        streamBoltList.add(new StreamBolt<>(iStreamBolt));
    }

    public void init(Map params) {
        for (StreamBolt<T> streamBolt : streamBoltList) {
            streamBolt.init(params);
        }
    }

    @Override
    protected void runnableExec() throws Exception {
        for (StreamBolt<T> streamBolt : streamBoltList) {
            equalMap.put(streamBolt.getCnt(), "");
        }
        //平衡了才能下发
        if (equalMap.size() == 1) {
            //下发
            T t = data.poll();
            if (t != null) {
                for (StreamBolt<T> streamBolt : streamBoltList) {
                    streamBolt.add(t);
                }
            }
        }
        //清理了才能继续
        equalMap.clear();
    }

    @Override
    protected void runnableLastExec() throws Exception {
        //剩余全量下发
        T t;
        while ((t = data.poll()) != null) {
            for (StreamBolt<T> streamBolt : streamBoltList) {
                streamBolt.add(t);
            }
        }
    }

    private void sendData() {

    }

    @Override
    protected void beforeStart() {
        for (StreamBolt<T> streamBolt : streamBoltList) {
            streamBolt.start();
        }
    }

    @Override
    protected void afterStop() {
        for (StreamBolt<T> streamBolt : streamBoltList) {
            streamBolt.stop();
        }
    }
}
