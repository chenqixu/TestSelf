package com.cqx.distributed;

import com.cqx.distributed.manager.ManagerService;
import com.cqx.distributed.resource.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * DistributedApp
 *
 * @author chenqixu
 */
public class DistributedApp {

    private static final Logger logger = LoggerFactory.getLogger(DistributedApp.class);
    private List<Thread> threadList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new DistributedApp().start();
    }

    private void start() throws Exception {
        ManagerService managerService = new ManagerService();
        //启动管理服务
        startService(managerService);
        //启动资源服务
        startService(new ResourceService());
        //推送配置，管理服务分配任务到资源并启动任务
        managerService.init(null);
        //服务监控&运行
        joinService();
    }

    private void startService(Runnable runnable) {
        logger.info("startService：{}", runnable);
        Thread thread = new Thread(runnable);
        thread.start();
        threadList.add(thread);
    }

    private void joinService() {
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
