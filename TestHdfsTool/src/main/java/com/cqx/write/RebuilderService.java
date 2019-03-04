package com.cqx.write;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * RebuilderService
 *
 * @author chenqixu
 */
public class RebuilderService<K, V> {

    public static Logger logger = LoggerFactory.getLogger(RebuilderService.class);
    //线程池
    private ExecutorService executor = null;
    private IRebuilder<K, V> iRebuilder;
    private RebuilderWatcher rebuilderWatcher;
    private List<Future<K>> futures;
    private List<K> caches;
    private List<V> datalist;

    private RebuilderService(List<V> datalist) {
        this.datalist = datalist;
        init();
    }

    public static RebuilderService builder(List datalist) {
        if (datalist == null) throw new NullPointerException("datalist 不能为空!");
        return new RebuilderService(datalist);
    }

    public void init() {
        executor = Executors.newFixedThreadPool(datalist.size());
        rebuilderWatcher = RebuilderWatcher.builder();
    }

    public void setRebuilerDeal(IRebuilder<K, V> iRebuilder) {
        this.iRebuilder = iRebuilder;
    }

    private void addRebuilder(V v) throws CloneNotSupportedException {
        RebuilderCallable rebuilderCallable = new RebuilderCallable((IRebuilder) iRebuilder.clone(), v);
        rebuilderWatcher.addWatcher(rebuilderCallable);
    }

    private void addTask(List<V> tmplist) throws CloneNotSupportedException {
        //add task
        logger.info("add task");
        for (V tmp : tmplist) {
            addRebuilder(tmp);
        }
    }

    private void submitTask() {
        futures = new ArrayList<>();
        //submit task
        logger.info("submit task");
        for (IWatcher watcher : rebuilderWatcher.getWatcherList()) {
            futures.add(executor.submit(watcher));
        }
    }

    private void await() throws Exception {
        caches = new ArrayList<>();
        //await
        logger.info("await");
        //如果成功则需要close，并加入缓存列表
        for (Future<K> future : futures) {
            //timeout：30s
            K tmp = future.get(30l, TimeUnit.SECONDS);
            if (tmp != null)
                caches.add(tmp);
        }
    }

    public void start() throws Exception {
        addTask(datalist);
        submitTask();
        await();
        //如果缓存大小和tmplist一致，则进行重连操作
        if (caches.size() == datalist.size()) {
            logger.info("caches.size == tmplist.size");
            //commit
            logger.info("commit");
            rebuilderWatcher.commit();
            submitTask();
            await();
        }
        logger.info("shutdown");
        executor.shutdown();
//        //await
//        executor.awaitTermination(10l, TimeUnit.SECONDS);
    }
}
