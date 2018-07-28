package com.cqx.spout;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqx.zookeeper.ChangeNodeInfo;
import com.cqx.zookeeper.CuratorTools;
import com.cqx.zookeeper.PathBatchWatcher;
import com.cqx.zookeeper.ZkInfo;

public class StatusSpout {
    private static final Logger logger = LoggerFactory.getLogger(StatusSpout.class);
    //	private static final String FILE_SEP = System.getProperty("file.separator");
    private ZkInfo zkInfo;
    private CuratorTools curatorTools;
    private LinkedBlockingQueue<ChangeNodeInfo> queueStatusInfo;
    
    public StatusSpout(ZkInfo zkInfo) {
        this.zkInfo = zkInfo;
    }
    
	public void open(){
		curatorTools = new CuratorTools(zkInfo);
		try {
			// 设置PATH_BATCH_NODE, PATH_STREAM_NODE, PATH_CUSTOMERAPP
            curatorTools.setDataForce(AppConst.PATH_BATCH, "");
            // watcher
            queueStatusInfo = new LinkedBlockingQueue<ChangeNodeInfo>(20000);
            curatorTools.watchTree(AppConst.PATH_BATCH, new PathBatchWatcher(queueStatusInfo));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
	}
}
