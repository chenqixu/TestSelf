package com.newland.bi.bigdata.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.bd.model.cfg.RedisCfg;
import com.newland.bd.utils.log.IDistributedLogger;
import com.newland.bi.bigdata.thread.CollectionConfig;
import com.newland.bi.bigdata.thread.FileContentBean;
import com.newland.bi.bigdata.thread.FileListManagerFactory;
import com.newland.bi.bigdata.thread.FileLockRedisService;
import com.newland.bi.bigdata.thread.CollectionRunnable;
import com.newland.bi.bigdata.thread.ScanServer;
import com.newland.edc.config.client.GlobalEnvConfig;
import com.newland.storm.common.pub.bean.ComponentDevParam;
import com.newland.storm.common.pub.bean.ComponentInstanceInfo;
import com.newland.storm.common.pub.utils.ZookeeperTool;
import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;
import com.newland.storm.component.etl.ftp.spout.IFileListManager;
import com.newland.storm.component.etl.ftp.spout.improve.FileTimeParser;
import com.newland.storm.component.etl.ftp.spout.lock.FileLock;

public class CollectionUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CollectionUtils.class);
	private ZookeeperTool zk = null;
	private String serviceUrl = null;
	private IFileListManager fileListManager = null;
	private FileLockRedisService lockService = null;
	private ComponentDevParam param = null;
	public static final String REDIS_CLUSTER = "redis_cluster";
	private static CollectionUtils cu = new CollectionUtils();
	
	// 待处理的文件列表
	private LinkedBlockingQueue<ExtractFileInfo> toDoFilesQueue = new LinkedBlockingQueue<ExtractFileInfo>();
	// 文件内容解析后填充往这里
	private LinkedBlockingQueue<FileContentBean> messsageQueue = new LinkedBlockingQueue<FileContentBean>();
	
	private CollectionUtils() {
		init();
	}
	
	public static CollectionRunnable generator(String str, Class<? extends CollectionRunnable> cs){
		try {
			Constructor<? extends CollectionRunnable> c = cs.getDeclaredConstructor(new Class[]{String.class});			
			return c.newInstance(new Object[]{str});
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static CollectionUtils getInstance(){
		if(cu==null){
			synchronized (cu) {
				if(cu==null)
					cu = new CollectionUtils();
			}
		}
		return cu;
	}
	
	private void init() {
		serviceUrl = getServiceUrl();
	}
	
	/**
	 * 通过zookeeper查询扫描服务地址
	 * 重试3次
	 * 1分钟后再重试
	 * 告警
	 * */
	private String getServiceUrl() {
		byte[] result;
		try {
//			result = zk.getInstance().getDataWithOutCheck("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 通过扫描服务获取文件列表
	 * 如果取不到文件，再更新服务地址会更好
	 * */
	public Collection<ExtractFileInfo> queryFtpFileListByParams(String name){
		//仅供测试
		return ScanServer.queryFtpFileListByParams(name);
	}

	public LinkedBlockingQueue<ExtractFileInfo> getToDoFilesQueue() {
		return toDoFilesQueue;
	}
	
	public void putMesssageQueue(FileContentBean fcb) {
		try {
			messsageQueue.put(fcb);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public LinkedBlockingQueue<FileContentBean> getMesssageQueue() {
		return messsageQueue;
	}
	
	public IFileListManager getFileListManager() {
		return fileListManager;
	}

	public FileLockRedisService getLockService() {
		return lockService;
	}

	public void createFileListManager(String componentType, IDistributedLogger distributeLogger,
			ComponentDevParam param, FileTimeParser parser, ComponentInstanceInfo instanceInfo) {
		if(fileListManager==null)
			fileListManager = FileListManagerFactory
					.getBuilder()
					.setComponentType(componentType)
					.setDistributeLogger(distributeLogger)
					.setParam(param)
					.setParser(parser)
					.setInstanceInfo(instanceInfo)
					.build();
	}
	
	public void createLockServer(IDistributedLogger distributeLogger, ComponentInstanceInfo instanceInfo,
			ComponentDevParam param){
		if(lockService==null){
			RedisCfg redisCfg = this.getRedisCfg(param);
			LOG.info("采用Redis文件锁。");
			try {
				lockService = new FileLockRedisService(redisCfg, distributeLogger, instanceInfo, param, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public RedisCfg getRedisCfg(ComponentDevParam param) {
		// 读取redis配置
		String redisClusterName = param.getComponentInitParam().get(REDIS_CLUSTER);
		if (redisClusterName == null || "".endsWith(redisClusterName.trim())) {
			throw new RuntimeException("配置redis集群连接不能为空！");
		}
		RedisCfg redisCfg = GlobalEnvConfig.getRedisCfg(redisClusterName);
		if (redisCfg.getPwd() != null && (redisCfg.getPwd().equals("none") || redisCfg.getPwd().equals("null"))) {
			redisCfg.setPwd(null);
			LOG.warn("加载到redis配置:" + redisCfg + "将密码设置为空");
		}
		return redisCfg;
	}

	public ComponentDevParam getParam() {
		return param;
	}

	public void setParam(ComponentDevParam param) {
		this.param = param;
	}
		
	/**
	 * 判断上次处理的时间到当前时间的间隔是否超出
	 * 默认：CollectionConfig.lockTimeoutSec * 1000
	 * @param lastModifyTime 最后处理时间
	 * @param expriedTimeoutSec 时间间隔，可以为空或0
	 * @return false表示过期
	 */
	public static boolean hasExpired(long lastModifyTime, long expriedTimeoutSec) {
		if(expriedTimeoutSec>0)// 如果有值
			return (System.currentTimeMillis() - lastModifyTime) < expriedTimeoutSec * 1000;
		// 默认
		return (System.currentTimeMillis() - lastModifyTime) < CollectionConfig.DEFAULT_LOCK_TIMEOUT * 1000;
	}
		
	/**
	 * 从锁中获取文件信息
	 * @param entry FileLock对象
	 * @return 解析返回ExtractFileInfo
	 * */
	public static ExtractFileInfo getFileInfo(FileLock entry) {
		ExtractFileInfo file = new ExtractFileInfo();
		file.setFileName(entry.getFileName());
		file.setFileSize(Long.parseLong(entry.getFileSize()));
		file.setSourcePath(entry.getSourcePath());
		file.setSourceMachine(entry.getSourceHost());
		file.setFtpEnvVarName(entry.getFtpEnv());
		file.setSourceBakPath(entry.getSourceBakPath());
		file.setSourceCheckPath(entry.getSourceCheckPath());
		file.setErrorBakPath(entry.getErrorBakPath());
		file.setFileTime(entry.getFileTime());
		return file;
	}
}
