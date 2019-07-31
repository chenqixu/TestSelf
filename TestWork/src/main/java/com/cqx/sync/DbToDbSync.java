package com.cqx.sync;

import java.util.Map;

/**
 * DbToDbSync
 * <pre>
 *     数据库同步到数据库
 *     选择数据库连接(含用户名、密码、tns)：mysql、oracle
 *     需要同步的表
 *     需要同步的字段
 *     格式转换
 *     同步方式：全量同步、增量同步
 * </pre>
 *
 * @author chenqixu
 */
public class DbToDbSync {

    private IDBSync idbSync;
    private DBSyncBean dbSyncBean;
    private Map<String, Object> params;

    public void init(Map<String, Object> params) {
        this.params = params;
        dbSyncBean = (DBSyncBean) params.get("dbSyncBean");
    }

    public void run() throws Exception {
        switch (dbSyncBean.getDbSyncType()) {
            case FullSync:
                idbSync = new DBFullSync();
                break;
            case IncrementSync:
                idbSync = new DBIncrementSync();
                break;
            default:
                idbSync = new DBFullSync();
                break;
        }
        idbSync.init(params);
        idbSync.run();
    }
}
