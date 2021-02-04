package com.cqx.work.jdk8.model;

import com.cqx.common.model.commit.BatchCommit;
import com.cqx.common.model.stream.IStreamBolt;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JDBCStreamBolt
 *
 * @author chenqixu
 */
public class JDBCStreamBolt extends IStreamBolt<BoltBean> {
    private final static Logger logger = LoggerFactory.getLogger(JDBCStreamBolt.class);
    private String name;
    private AtomicLong cnt = new AtomicLong(0L);
    private BatchCommit<BoltBean> batchCommit;
    private JDBCUtil jdbcUtil;

    public JDBCStreamBolt(String name) {
        this.name = name;
    }

    @Override
    public void prepare(Map params) {
        DBBean dbBean = DBBean.newbuilder().parserMap(params);
        jdbcUtil = new JDBCUtil(dbBean);

        batchCommit = new BatchCommit<>(5000, 4000, 10000,
                new BatchCommit.ICallBack<BoltBean>() {
                    @Override
                    public void call(List<BoltBean> list) {
                        cnt.addAndGet(list.size());
                        logger.info("{} call list.size：{}", name, list.size());
                        if (name.equals("83-cluster")) {
                            SleepUtil.sleepSecond(2);
                        }
                    }
                });
    }

    @Override
    public void execute(BoltBean boltBean) {
        try {
            batchCommit.add(boltBean);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void clean() {
        logger.info("{} execute-all {}", name, cnt.get());
        if (batchCommit != null) batchCommit.close();
    }
}
