package com.newland.bi.bigdata.net;

import com.cqx.exception.TestSelfException;
import com.newland.bi.bigdata.utils.SleepUtils;
import com.newland.bi.bigdata.utils.net.IpUtil;
import com.newland.bi.bigdata.zookeeper.ZookeeperTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void choiceMod(int parallelism, String[] dpiPorts) {
        if (dpiPorts.length > 0 && parallelism > dpiPorts.length) {
            int quotient = parallelism / dpiPorts.length;
            int mod = parallelism % dpiPorts.length;
            // mod 判断能否被整除
            // 不停加1直到被整除为止
            logger.info("quotient：{}，mod：{}", quotient, mod);
            int _parallelism = parallelism;
            while (mod != 0) {
                _parallelism++;
                mod = _parallelism % dpiPorts.length;
                logger.info("_parallelism：{}，mod：{}", _parallelism, mod);
            }
        }
    }

    /**
     * <per>
     * zookeeper对应结构
     * ip/port/num
     * 重要的是port/num，List要能体现这个关系即可
     * </per>
     *
     * @param parallelism
     * @param dpiPorts
     */
    public void choiceSequentialAllocation(int parallelism, String[] dpiPorts) {
        if (dpiPorts.length > 0 && parallelism > dpiPorts.length) {
            int all = parallelism;
            // 先分配，在根据分配循环去抢占zookeeper
            // 分配
            List<SequentialAllocation> allocationList = new ArrayList<>();
            int cycles = 0;
            while (all > 0) {
                for (int i = 0; i < dpiPorts.length; i++) {
                    allocationList.add(new SequentialAllocation(dpiPorts[i], cycles));
                    logger.info("dpiPorts[i]：{}，cycles：{}，all：{}", dpiPorts[i], cycles, all);
                    all--;
                    if (all == 0) break;
                }
                cycles++;
            }
            // 根据分配循环去抢占zookeeper
            for (SequentialAllocation sequentialAllocation : allocationList) {
                logger.info("sequentialAllocation：{}", sequentialAllocation);
            }
        }
    }

    class SequentialAllocation {
        private String port;
        private int num;

        public SequentialAllocation(String port, int num) {
            this.port = port;
            this.num = num;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String toString() {
            return "port：" + port + "，num：" + num;
        }
    }
}
