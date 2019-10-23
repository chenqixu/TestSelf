package com.cqx.sync;

import com.cqx.sync.bean.BeanUtil;
import com.cqx.sync.bean.DBBean;
import com.cqx.sync.bean.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * DBIncrementSync
 *
 * @author chenqixu
 */
public class DBIncrementSync implements IDBSync {

    private static Logger logger = LoggerFactory.getLogger(DBIncrementSync.class);
    private Map<String, Object> params;
    private BlockingQueue<String> collectTimeQueue;
    private JDBCUtil srcJDBC;
    private JDBCUtil dstJDBC;
    private String sync_name;
    private String src_tab_name;
    private String dst_tab_name;
    private String src_fields;
    private String dst_fields;
    private String[] dst_field_arr;
    private BeanUtil src_beanUtil;
    private BeanUtil dst_beanUtil;

    @Override
    public void init(Map<String, Object> params) throws Exception {
        this.params = params;
        collectTimeQueue = new LinkedBlockingQueue<>();
        DBBean srcdbBean = (DBBean) params.get("srcdbBean");
        DBBean dstdbBean = (DBBean) params.get("dstdbBean");
        sync_name = (String) params.get("sync_name");
        src_tab_name = (String) params.get("src_tab_name");
        dst_tab_name = (String) params.get("dst_tab_name");
        src_fields = (String) params.get("src_fields");
        dst_fields = (String) params.get("dst_fields");
        dst_field_arr = dst_fields.split(",", -1);
        srcJDBC = new JDBCUtil(srcdbBean);
        dstJDBC = new JDBCUtil(dstdbBean);
        // 源采集周期字段
        // 源采集周期字段时间格式
        // 目标采集周期字段
        // 目标采集周期字段时间格式
        logger.info("初始化参数：源端数据库：{}，目标端数据库：{}，同步名称：{}，源表：{}，目标表：{}，源字段：{}，目标字段：{}，目标字段拆分后的数组：{}",
                srcdbBean, dstdbBean, sync_name, src_tab_name, dst_tab_name, src_fields, dst_fields, dst_field_arr);
        // 构造源端表javabean
        src_beanUtil = srcJDBC.generateBeanByTabeNameAndFields(src_fields, src_tab_name);
        // 构造目标端javabean
        dst_beanUtil = dstJDBC.generateBeanByTabeNameAndFields(dst_fields, dst_tab_name);
        logger.info("构造源端表javabean：{}，构造目标端javabean：{}", src_beanUtil.getObj(), dst_beanUtil.getObj());
    }

    /**
     * <pre>
     *     查询历史表，获取采集时间，按采集周期顺序抽取，采集端保存最新采集周期
     * </pre>
     *
     * @throws Exception
     */
    @Override
    public void run() throws Exception {
        try {
            // 查询配置表，获取最新采集时间
            String cfg_collect_time = getCfgCollectTime(sync_name);
            // 从源表获取需要采集的周期
            getSrcCollectTimeList(cfg_collect_time);
            // 按周期进行数据同步，不成功则重来
            String _collect_time;
            while ((_collect_time = collectTimeQueue.poll()) != null) {
                boolean result = false;
                try {
                    result = sync(_collect_time);
                } catch (Exception e) {
                    logger.error("按周期进行数据同步异常：" + e.getMessage(), e);
                    // 异常就退出
                    break;
                }
            }
        } finally {
            srcJDBC.closeAll();
            dstJDBC.closeAll();
        }
    }

    /**
     * 查询配置表，获取最新采集时间
     *
     * @param sync_name
     * @return
     * @throws Exception
     */
    private String getCfgCollectTime(String sync_name) throws Exception {
        String sql = "select to_char(sync_time,'yyyy-mm-dd hh24:mi:ss') as sync_time from sm2_rsmgr_sync_conf where sync_name='" + sync_name + "'";
        logger.info("查询配置表获取最新采集时间_sql：{}", sql);
        List<List<QueryResult>> syncConfs = dstJDBC.executeQuery(sql);
        if (syncConfs.size() == 1 && syncConfs.get(0).size() == 1) return (String) syncConfs.get(0).get(0).getValue();
        return null;
    }

    /**
     * 从源表获取需要采集的周期，剔除最新周期(防止最新周期数据不完整)
     *
     * @param collect_time
     * @throws Exception
     */
    private void getSrcCollectTimeList(String collect_time) throws Exception {
        String sql = "select DATE_FORMAT(collect_time,'%Y-%m-%d %H:%i:%s') as collect_time from(select collect_time from " + src_tab_name;
        String condition = " where collect_time not in(select max(collect_time) from " + src_tab_name + ") ";
        // 判断最新采集时间是否为空，为空则是第一次采集
        if (collect_time == null || collect_time.length() == 0) {
        } else {
            condition = condition + " and collect_time>'" + collect_time + "'";
        }
        sql = sql + condition + " group by collect_time order by collect_time) t";
        logger.info("从源表获取需要采集的周期_sql：{}", sql);
        List<List<QueryResult>> collectTimes = srcJDBC.executeQuery(sql);
        // 从list插入队列
        for (List<QueryResult> collectTime : collectTimes) {
            if (collectTime.size() == 1) collectTimeQueue.put((String) collectTime.get(0).getValue());
        }
    }

    /**
     * 按周期进行数据同步，不成功则重来
     *
     * @param sync_time
     * @return
     * @throws Exception
     */
    private boolean sync(String sync_time) throws Exception {
        logger.info("按周期进行数据同步，sync_time：{}", sync_time);
        // 清空目标端同步周期数据
        String clean_sql = "delete from " + dst_tab_name + " where collect_time=to_date('" + sync_time + "','yyyy-mm-dd hh24:mi:ss')";
        logger.info("清空目标端同步周期数据 clean_sql：{}", clean_sql);
        int clean_ret = dstJDBC.executeUpdate(clean_sql);
        logger.info("sync clean_ret：{}", clean_ret);
        if (clean_ret < 0) throw new Exception("清空目标端同步周期数据失败");
        // 从源端按照collect_time=sync_time查询数据，同步到目标端
        String sql = "select " + src_fields + " from " + src_tab_name + " where collect_time='" + sync_time + "'";
        logger.info("从源端按照collect_time=sync_time查询数据 select_src_sql：{}", sql);
        List<List<QueryResult>> srcResultList = srcJDBC.executeQuery(sql);
        StringBuffer prepare = new StringBuffer();
        for (int i = 0; i < dst_field_arr.length; i++) {
            prepare.append("?,");
        }
        prepare.delete(prepare.length() - 1, prepare.length());
        String sync_sql = "insert into " + dst_tab_name + " (" + dst_fields + ") values(" + prepare.toString() + ")";
        logger.info("同步到目标端 sync_sql：{}", sync_sql);
        int ret = dstJDBC.executeBatch(sync_sql, srcResultList, dst_beanUtil.getFieldsType());
        if (ret < 0) throw new Exception("同步到目标端失败");
        // 更新配置表采集周期
        int update_ret = updateCfgCollectTime(sync_name, sync_time);
        if (update_ret < 0) throw new Exception("更新配置表采集周期失败");
        return ret == 0;
    }

    /**
     * 更新配置表采集周期
     *
     * @param sync_name
     * @param sync_time
     * @throws Exception
     */
    private int updateCfgCollectTime(String sync_name, String sync_time) throws Exception {
        logger.info("更新配置表采集周期，sync_name：{}，sync_time：{}", sync_name, sync_time);
        // 更新配置表采集周期
        String update_sql = "update sm2_rsmgr_sync_conf set sync_time=to_date('" + sync_time + "','yyyy-mm-dd hh24:mi:ss') where sync_name='" + sync_name + "'";
        logger.info("更新配置表采集周期 clean_sql：{}", update_sql);
        int update_ret = dstJDBC.executeUpdate(update_sql);
        logger.info("sync update_ret：{}", update_ret);
        return update_ret;
    }
}
