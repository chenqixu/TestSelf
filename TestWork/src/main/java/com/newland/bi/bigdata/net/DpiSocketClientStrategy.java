package com.newland.bi.bigdata.net;

import com.cqx.exception.TestSelfException;
import com.newland.bi.bigdata.utils.SleepUtils;
import com.newland.bi.bigdata.utils.net.IpUtil;
import com.newland.bi.bigdata.zookeeper.ZookeeperTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DpiSocketClientStrategy
 * <pre>
 *     path：/task_id/ip/port
 *     1、try lock
 *     2、lock exists，getting failure time from content，if expired，delete node
 *     3、locks do not exist，Successful attempt to lock
 *     4、locks do not exist，Failed attempt to lock
 * </pre>
 *
 * @author chenqixu
 */
public class DpiSocketClientStrategy {

    public static final String fileSparator = "/";
    private static final String zk_path = fileSparator + IpUtil.ip;
    private static Logger logger = LoggerFactory.getLogger(DpiSocketClientStrategy.class);
    private ZookeeperTools zookeeperTools;
    private String[] port_arr = {"10111", "10112", "10113"};

    public DpiSocketClientStrategy(String connectionInfo) {
        zookeeperTools = ZookeeperTools.getInstance();
        zookeeperTools.init(connectionInfo);
    }

    /**
     * 不停重试，每次循环间隔500毫秒
     *
     * @return
     * @throws Exception
     */
    public void tryLock() throws Exception {
        boolean lockResult = false;
        while (true) {
            for (String port : port_arr) {
                String lockZkPath = zk_path + fileSparator + port;
                logger.info("try lock：{}", lockZkPath);
                //创建临时会话
                lockResult = zookeeperTools.createPersistentEphemeralNode(lockZkPath);
                if (!lockResult) {//加锁失败
                    logger.info("lockZkPath：{} fail！", lockZkPath);
                } else {//加锁成功
                    logger.info("lockZkPath：{} success！", lockZkPath);
                    return;
                }
            }
            SleepUtils.sleepMilliSecond(500);
        }
    }

    public void close() {
        if (zookeeperTools != null) {
            try {
                zookeeperTools.close();
            } catch (TestSelfException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
