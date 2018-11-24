package com.newland.bi.bigdata.thread;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.bd.model.cfg.RedisCfg;
import com.newland.bd.utils.log.IDistributedLogger;
import com.newland.storm.common.context.ITaskContext;
import com.newland.storm.common.pub.bean.ComponentDevParam;
import com.newland.storm.common.pub.bean.ComponentInstanceInfo;
import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;
import com.newland.storm.component.etl.ftp.spout.lock.FileLock;
import com.newland.storm.component.etl.ftp.spout.lock.FileLockService;
import com.newland.storm.component.etl.ftp.spout.lock.redis.RedisLockDao;

/**
 */
public class FileLockRedisService implements FileLockService {

	private static final Logger LOG = LoggerFactory.getLogger(FileLockRedisService.class);

	private RedisLockDao dao;
	private final String lockMapKey; // redis上保存的map的key值
	private final String dirKey;
	private final IDistributedLogger distributeLogger;
	private ComponentInstanceInfo instanceInfo;
	private String spoutId;

	public FileLockRedisService(RedisCfg redisCfg, IDistributedLogger distributeLogger, ComponentInstanceInfo instanceInfo, ComponentDevParam param, ITaskContext context) throws Exception {
		this.instanceInfo = instanceInfo;
		this.dao = RedisLockDao.getInstance(redisCfg);
		this.spoutId = this.instanceInfo.getComponentId();

		this.lockMapKey = "filelock:" + instanceInfo.getStepInfo().getTemplateId();
		distributeLogger.info("Redis文件锁关键KEY：" + lockMapKey);

		this.dirKey = "dirLock:" + instanceInfo.getStepInfo().getTemplateId();
		distributeLogger.info("Redis文件目录锁关键KEY：" + this.dirKey);

		this.distributeLogger = distributeLogger;

	}

	@Override
	public FileLock tryLock(ExtractFileInfo file) throws Exception {
		FileLock lock = new FileLock("", spoutId, file);
		String lockJson = lock.toString();
		boolean success = this.dao.hsetnx(lockMapKey, file.getFileName(), lockJson);
		if (success) {
			// this.LOG.info("文件加锁" + file.getFileName());
			return lock;
		} else
			return null;
	}
	
	/**
	 * 更新Redis中锁的文件位置
	 * */
	public boolean updateFileRecord(String fileName, String fileOffset) {
		String lockJson = null;
		boolean result = false;
		try {
			lockJson = this.dao.hget(lockMapKey, fileName);
			FileLock lock = FileLock.deserialize(lockJson);
//			if(Long.valueOf(lock.getFileOffset())<fileOffset) {
				lock.setFileOffset(fileOffset);
				lockJson = lock.toString();
				result = this.dao.hset(lockMapKey, fileName, lockJson);
				LOG.info("文件位置{}，更新结果{}", fileName+"，"+fileOffset, result);
				/**
				 * 公用方法这里的返回值有点不对
				 * Set the specified hash field to the specified value. 
				 * If key does not exist, a new key holding a hash is created. 
				 * Time complexity: O(1)
				 * 
				 * Specified by: hset(...) in JedisCommands
				 * Parameters:key field value Returns:If the field already exists, and the HSET just produced an update of the value,
				 * 0 is returned, otherwise if a new field is created 1 is returned.
				 * */
//			}			
		} catch (Exception e) {
			LOG.error("更新Redis中锁的文件位置{}，异常信息{}", fileName+"，"+fileOffset, e.getMessage());
		}
		return result;
	}

	@Override
	public FileLock acquireOldestExpiredLock(int locktimeoutSec) throws Exception {
		boolean ret = this.dirLock();
		if (!ret) {
			this.LOG.info("未获取到文件目录锁。");
			return null;
		}
		try {
			// list files
			long now = System.currentTimeMillis();
			long olderThan = now - (locktimeoutSec * 1000);
			Map<String, String> locks = this.dao.getAll(lockMapKey);
			for (Entry<String, String> fileInfo : locks.entrySet()) {
				FileLock lock = FileLock.deserialize(fileInfo.getValue());
				if (lock.getEventTime() < olderThan) {
					this.distributeLogger.info("检测到过期" + locktimeoutSec + "S的文件锁" + lock.getFileName() + "，将锁设置为当前进程");
					this.release(lock);
					lock = this.tryExpiredLock(lock);
					return lock;
				/**
				 * 由于只要处理过期文件，所以以下注释掉
				 * */
//				} else {
//					if (lock.getComponentID() != null && spoutId.equals(lock.getComponentID())) // 判断如果组件实际例ID和自己相等，直接解锁续作
//					{
//						LOG.info("检测到本实例之前锁定的文件,解锁后续作. lastEntry{}", lock);
//						return lock;
//					}
				}
			}
			if (locks.isEmpty()) {
				LOG.info("No abandoned lock files found by Spout {}", spoutId);
			}
		} catch (Exception e) {
			LOG.error("" + lockMapKey + "检测文件锁是否过期发生异常。", e);
		} finally {
			if (ret) {
				this.dirRelease();
			}
		}
		return null;
	}

	private FileLock tryExpiredLock(FileLock lock) throws Exception {
		lock.setComponentID(spoutId);
		lock.setEventTime(System.currentTimeMillis());
		String lockJson = lock.toString();
		boolean success = this.dao.hsetnx(lockMapKey, lock.getFileName(), lockJson);
		if (success) {
			this.distributeLogger.debug("获取过期文件锁" + lock.getFileName() + "的权限，开始进行处理");
			return lock;
		} else {
			String newlock = this.dao.hget(lockMapKey, lock.getFileName());
			FileLock otlock = FileLock.deserialize(newlock);
			this.distributeLogger.warn("未获取过期文件锁，" + lock.getFileName() + "的权限已被" + otlock.getComponentID() + "接管。" + lock);
			return null;
		}
	}

	@Override
	public void release(FileLock lock) throws Exception {
		// this.distributeLogger.debug("释放文件锁" + lock.getFileName() + "");
		this.dao.hdel(lockMapKey, lock.getFileName());
	}

	@Override
	public void heartBeat(FileLock lock, String fileOffset) throws Exception {
		lock.setFileOffset(fileOffset);
		lock.setEventTime(System.currentTimeMillis());
		String lockJson = lock.toString();
		this.dao.hset(lockMapKey, lock.getFileName(), lockJson);
	}

	/**
	 * 接管过期文件的时候，防止某个节点挂了后永远无法继续处理
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean dirLock() throws Exception {
		boolean ret = this.dao.setnx(this.dirKey, this.instanceInfo.getComponentId(), 30); // 30秒过期
		return ret;
	}

	/**
	 * 接管过期文件的时候，防止
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean dirRelease() throws Exception {
		boolean ret = this.dao.del(this.dirKey);
		return ret;
	}

	public String getSpoutId() {
		return spoutId;
	}

	public void setSpoutId(String spoutId) {
		this.spoutId = spoutId;
	}

	public void setInstanceInfo(ComponentInstanceInfo instanceInfo) {
		this.instanceInfo = instanceInfo;
	}

}

