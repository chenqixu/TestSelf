package com.cqx.sync;

import com.cqx.sync.bean.DBBean;
import com.cqx.sync.bean.RealTimeBean;

import java.util.List;
import java.util.Map;

/**
 * DBRealTimeUpdateSync
 *
 * @author chenqixu
 */
public class DBRealTimeUpdateSync implements IDBSync {

    private JDBCUtil srcJDBC;
    private JDBCUtil dstJDBC;

    @Override
    public void init(Map<String, Object> params) throws Exception {
        DBBean srcdbBean = (DBBean) params.get("srcdbBean");
        DBBean dstdbBean = (DBBean) params.get("dstdbBean");
        srcJDBC = new JDBCUtil(srcdbBean);
        dstJDBC = new JDBCUtil(dstdbBean);
    }

    @Override
    public void run() throws Exception {
        try {
            String sql1 = "select id,amount from real_time1";
            List<RealTimeBean> realTimeBeanList1 = srcJDBC.executeQuery(sql1, RealTimeBean.class);
            for (RealTimeBean realTimeBean : realTimeBeanList1) {
                System.out.println(realTimeBean);
            }
            String sql2 = "select id,amount from real_time2";
            List<RealTimeBean> realTimeBeanList2 = srcJDBC.executeQuery(sql2, RealTimeBean.class);
            for (RealTimeBean realTimeBean : realTimeBeanList2) {
                System.out.println(realTimeBean);
            }
            // 根据PK_KEY，以T1为准，找出变更的、新增的、删除的
            // 生成变更的、新增的、删除的语句
            ListBeanCompare<RealTimeBean> listBeanCompare = new ListBeanCompare<>(RealTimeBean.class);
            listBeanCompare.setT1(realTimeBeanList1);
            listBeanCompare.setT2(realTimeBeanList2);
            listBeanCompare.compare();
        } finally {
            srcJDBC.closeAll();
            dstJDBC.closeAll();
        }
    }
}
