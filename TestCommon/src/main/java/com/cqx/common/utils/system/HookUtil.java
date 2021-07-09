package com.cqx.common.utils.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>钩子工具</h3>
 * <p>
 * 对于kill -9，一点作用也没有
 * <p>
 * 最好是kill -15，但是这个会调用资源释放，加不加hook就无所谓了
 *
 * @author chenqixu
 */
public class HookUtil {
    private static final Logger logger = LoggerFactory.getLogger(HookUtil.class);

    public void addHook(IHook iHook) {
        assert iHook != null;
        Runtime.getRuntime().addShutdownHook(
                new Thread("relase-shutdown-hook" + this) {
                    @Override
                    public void run() {
                        // 释放资源
                        logger.info("hook-release：{}", this);
                        try {
                            iHook.hook();
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
        );
    }

    public interface IHook {
        void hook() throws Exception;
    }
}
