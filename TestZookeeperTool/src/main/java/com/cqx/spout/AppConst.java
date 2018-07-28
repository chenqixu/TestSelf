package com.cqx.spout;

/**
 * 应用程序的静态常量
 * */
public interface AppConst {
    // log and collect log
    public static final String COLLECT_SPOUT = "collect_spout";
    public static final String COLLECT_FILE_LOG_SPOUT = "collect_file_log_spout";

    public static final String LOG_BOLT = "log_bolt";
    public static final String COLLECT_LOG_BOLT = "collect_log_bolt";
    public static final String ATTACH_BOLT = "attach_bolt";

    public static final String LOG_STREAM = "log_stream";
    public static final String COLLECT_LOG_STREAM = "collect_log_stream";
    public static final String ATTACH_STREAM = "attach_stream";

    // status
    public static final String STATUS_SPOUT = "status_spout";
    public static final String STATUS_BOLT = "status_bolt";

    public static final String STATUS_STREAM = "status_stream";

    // process
    public static final String PROCESS_NAME = "nl-task-control-collect";

    // path constant
    public static final String PATH_ZK_CONTROL_COLLECT = "/computecenter/process-status/nl-task-control-collect";
    public static final String PATH_OOZIE_JVM = "/oozie/jvm";
//    public static final String PATH_BATCH = "/TASK/1000";
    public static final String PATH_BATCH = "/zktest";
    public static final String PATH_STREAM = "/TASK/2000";
    public static final String PATH_CUSTOMAPP = "/TASK/3000";

    // other
    public static final String ZK_SEP = "/";

    // 采集类型(0: 批量采集; 1: 流采集; 2: 应用发布)
    public static final int COLL_BATCH = 0;
    public static final int COLL_STREAM = 1;
    public static final int COLL_CUSTOMAPP = 2;

    // 操作类型(0: 新增; 1: 更新)
    public static final int ACTION_ADD = 0;
    public static final int ACTION_UPDATE = 1;

    // 节点类型
    public static final String NODE_TASK = "task";
    public static final String NODE_STAGE = "stage";
    public static final String NODE_SERVICE = "service";
    public static final String NODE_COMPONENT = "component";

    // note:
    // collectType: batch, stream, customerapp
    // actionType: add, update, remove
    // component: task, stage, service, component
}
