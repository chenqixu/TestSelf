package com.bussiness.bi.bigdata.thread;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enterprisedt.net.ftp.FTPException;
import com.bussiness.bi.bigdata.utils.CollectionUtils;
import com.newland.storm.common.pub.bean.ComponentDevParam;
import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;
import com.newland.storm.component.etl.common.model.impl.FileOffset;
import com.newland.storm.component.etl.ftp.spout.FileBatchReader;
import com.newland.storm.component.etl.ftp.spout.FileReader;
import com.newland.storm.component.etl.ftp.spout.FileReaderProcesssStatus;
import com.newland.storm.component.etl.ftp.spout.IFileListManager;
import com.newland.storm.component.etl.ftp.spout.ParseException;
import com.newland.storm.component.etl.ftp.spout.improve.FileTimeParser;
import com.newland.storm.component.etl.ftp.spout.lock.FileLock;
import com.newland.storm.component.etl.ftp.spout.reader.FtpNullFileReader;
import com.newland.storm.component.etl.ftp.spout.reader.IXmlBatchMsgs;
import com.newland.storm.component.etl.util.EtlUtils;

/**
 * <pre>
 * 采集线程
 * 单个线程对应单个源端
 * 取文件列表，单队列少于多少值，就去拉取文件列表，进行下载
 * 采集过程：锁判断、加锁、采集到内存、采集碎文件到本地、清理文件、锁释放
 * 对外提供服务：心跳、结束采集
 * </pre>
 * */
public class CollectionThread extends CollectionRunnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(CollectionThread.class);
	public static final String PARAM_READER_TYPE = "reader-type";
	private FileLockRedisService lockService;
	private IFileListManager fileListManager;
	private ComponentDevParam param;
	// 按时间转换
	private FileTimeParser parser;
	// 上次检测到过期的时间
	private long lastExpiredLockTime = 0;
	private long lastComplateTime = 0;
	private String log_tag = "";
	// 当前所有未关闭的reader
	private PendingFileReader pendingFileReader = null;
	// 输入流帮助类
	private ReaderHelper readerhelper = null;
	
	public CollectionThread(String uuid) {
		super(uuid);
		this.fileListManager = CollectionUtils.getInstance().getFileListManager();
		this.lockService = CollectionUtils.getInstance().getLockService();
		this.param = CollectionUtils.getInstance().getParam();
		this.pendingFileReader = new PendingFileReader();
		this.parser = new FileTimeParser();
		this.readerhelper = new ReaderHelper();
		LOG.info("启动CollectionThread");
	}
	
	/**
	 * 从文件队列取数据，进行采集，并提交到文件内容解析队列
	 * */
	@Override
	public void run() {
		while(flag) {
			// 从文件队列取数据，或者接管超时文件
			FileReaderProcesssStatus nowReader = null;	
			while((nowReader=getDataFromQueue())!=null) {
				LOG.info("取到处理对象{}", nowReader);
				FileReader reader = nowReader.getReader();
				// 获取到文件流，进行处理
				if( reader!=null ) {
					ExtractFileInfo currentFile = reader.getFileInfo();
					try {
						if (reader instanceof FileBatchReader) {
							IXmlBatchMsgs batchMsg = ((FileBatchReader) reader).getBatch(CollectionConfig.BATCH_SIZE);
							while (batchMsg != null && batchMsg.getRecords() != null) {
									putData(currentFile, batchMsg.getRecords(), reader.getFileOffset());
							}
						} else {
							String record = null;
							int recourdcnt = 0;
							List<String> recordlist = new ArrayList<String>();
							while((record = reader.next())!=null) {
								recordlist.add(record);
								recourdcnt++;
								// 每BATCH_SIZE就put一次
								if( recourdcnt%CollectionConfig.BATCH_SIZE==0 ){
									putData(currentFile, recordlist, reader.getFileOffset());
									recordlist = new ArrayList<String>();
								}									
							}
							// 处理剩余数据
							if(recordlist.size()>0) {
								putData(currentFile, recordlist, reader.getFileOffset());
							}
						}
					} catch (IOException | ParseException e) {// 如果读取文件记录发生异常，将文件移动往错误目录
						LOG.error("文件" + reader.getFileName() + "读取记录失败,当前offset:" + reader.getFileOffset(), e);
						try {
							// 文件采集完成后调用此方法 用来做文件删除、移动备份等操作
							fileListManager.errorFile(reader.getFileInfo());
						} catch (Exception ex) {
							LOG.error("将文件{}移动往错误目录失败，失败信息{}", reader.getFileName(), ex.getMessage());
						}
						LOG.info("文件" + reader.getFileName() + "成功移动往异常目录");
						// 锁释放，从pendingFileReader队列移除
						markFileAsDone(nowReader, reader.getFileName());
						// 下一个循环
						continue;
					}
					// 已经到达文件末尾
					LOG.info("{}文件已读取到末尾", reader.getFileName());
					// 最后发个空的putData，设置结束标志
					putData(currentFile, reader.getFileOffset());
					// 关闭输出流，释放连接资源
					readerhelper.closeReader(reader);
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 清理pengdingFileReader中超时的任务
	 * */
	private void clearNoAnswer() {
		// 循环pendingFileReader
		while(pendingFileReader.hasNext()) {
			FileReaderProcesssStatus nextreader = pendingFileReader.next();
			long readComplateLast = System.currentTimeMillis() - nextreader.getReadComplateTime();
			// 时间判断，等待时间不能超过锁过期时间，否则文件会重复
			if (readComplateLast > CollectionConfig.DEFAULT_LOCK_TIMEOUT * 1000 - 5 * 1000) {
				LOG.warn(log_tag + "读取完成的文件" + nextreader.getInflight() +
						",当前等待超时last，直接结束文件=======" + nextreader.getReader().getFileName());
				// 锁释放，从pendingFileReader队列移除
				markFileAsDone(nextreader, nextreader.getReader().getFileName());
			}
		}
	}
	
	/**
	 * 从文件队列取数据
	 * 或者接管处理超时文件
	 * */
	private FileReaderProcesssStatus getDataFromQueue() {
		FileReaderProcesssStatus newReader = null;
		FileReader reader = null;
		// 先判断下游队列是否超过上限
		if(pendingFileReader.isOutOfSize()) {
			// 先查找是否有需要过期重做的文件
			FileLock lockold = getOldestExpiredLock();
			if (lockold != null) {// 有需要过期重做的文件
				LOG.info("开始重做失败的文件 : {}", lockold);
				try {
					reader = readerhelper.createFileReader(lockold);
				} catch (FTPException e) {
					// 文件处理异常，跳过此文件
					LOG.warn("续作程序无法读取到文件" + lockold + "，跳过此程序的处理 " + e.getMessage());
					// 释放锁
					new LockProcess(lockold).releaseLock();
				} catch (Exception e) {
					LOG.error("续作程序无法读取到文件" + lockold + "，跳过此程序的处理 ", e);
					// 释放锁
					new LockProcess(lockold).releaseLock();
				}
				if (reader == null) {
					// FTP连接不可用，跳过次文件}
					LOG.error("续作程序无法读取到文件" + lockold + "，跳过此程序的处理 ");
				} else {
					Date date = null;
					String fileDate = null; 
					try {
						date = parser.getDate(reader.getFileInfo().getFileTime());
						fileDate = parser.toDateString(date);
					} catch (java.text.ParseException e) {
						LOG.info("时间格式转换异常，获取时间{}，转换成字符串{} ", reader.getFileInfo().getFileTime(), date);
						return null;
					}
					newReader = new FileReaderProcesssStatus(reader, lockold, fileDate);
					// 加入处理队列，等待ACK关闭
					pendingFileReader.put(newReader.getReader().getFileName(), newReader);
					return newReader;
				}
			}
			// 没有需要过期重做的，从扫描服务队列中获取文件
			else {
				LOG.info("没有需要过期重做的，从扫描服务队列中获取文件");
				ExtractFileInfo currentFile = null;
				// 从队列中获取文件
				if((currentFile = CollectionUtils.getInstance().getToDoFilesQueue().poll())!=null) {
					FileLock locknew = null;
					String fileDate = null;
					LockProcess lp = null;
					try {
						Date date = parser.getDate(parser.getTime(currentFile.getFileName(),new Date()));
						fileDate = parser.toDateString(date);
					} catch (Exception e) {
						LOG.error("时间格式无法解析，跳过文件Skipping file " + currentFile, e);
						return null;
					}
					// 先判断文件是否存在，如果存在则加锁处理
					try {
						if (!fileListManager.isFileExist(currentFile)) {// 文件已不存在.
							LOG.warn("文件已不存在，或文件已被其他进程处理，跳过此文件.{}", currentFile.getAbsolutePath());
							return null;
						} else {
							// 尝试加锁
							lp = new LockProcess(currentFile);
							// 加锁成功
							if(lp.addLock()) {
								// 在判定一次是否存在.// 由于处理文件都很快，可能存在其他进程已经处理完成并释放锁，这里才开始加锁的情况
								if (!fileListManager.isFileExist(currentFile)) {
									LOG.info("文件已不存在，释放锁.{}", currentFile.getAbsolutePath());
									lp.releaseLock();
									return null;
								}
								locknew = lp.getLock();
								reader = readerhelper.createFileReader(currentFile, locknew);
								if (reader == null) {
									
								} else {
									long waitTime = System.currentTimeMillis() - lastComplateTime;
									LOG.info("开始处理文件Processing : {} . 等待时间{} ", currentFile, waitTime);
									LOG.info("开始处理文件Processing :" + currentFile + " spout等待时间" + waitTime);
									newReader = new FileReaderProcesssStatus(reader, locknew, fileDate);
									pendingFileReader.put(newReader.getReader().getFileName(), newReader);// 加入处理队列，待ack关闭
									return newReader;
								}
							}
							// 加锁失败
							else {
								// "无法获得文件锁 FileLock for {}, so skipping it.", currentFile
								return null;
							}
						}
					} catch (Exception e) {// 异常捕获，可能是isFileExist获取异常，也可能是createFileReader异常
						LOG.error("跳过文件Skipping file " + currentFile, e);
						if (reader != null) {
							try {
								reader.close();
							} catch (Exception e1) {
								LOG.error("释放reader出现异常", e1);
							}
						}
						// 发生异常，释放文件锁
						if(lp!=null)
							lp.releaseLock();
					}
				}
			}
		}
		// 假如有10个文件后面都还没有应答.那么本次需要进行等待.并清理超时的消息
		else {
			LOG.info(log_tag + "Spout待应答的文件数量超过上限值10，暂停处理.等待下游文件应答.文件明细:{}", pendingFileReader.keySet());
			// 清理pengdingFileReader中超时的任务
			clearNoAnswer();
		}
		return null;
	}
	
	/**
	 * 发送数据到消息队列<p>
	 * 数据格式：List<String>
	 * @param currentFile 文件信息
	 * @param recordlist 内容
	 * @param offset 位置
	 * @param isFileReadCompletely 是否完成
	 * */
	private void putData(ExtractFileInfo currentFile, List<String> recordlist, FileOffset offset, boolean isFileReadCompletely) {
		FileMessageId msgId = new FileMessageId(offset.lineNumber, currentFile.getFileName());
		msgId.setCharOffset(offset.charOffset);
		if(isFileReadCompletely)msgId.setFileReadCompletely();// 设置完成标志
		FileContentBean fileContentBean = new FileContentBean();
		fileContentBean.setRecords(recordlist);
		fileContentBean.setFileInfo(currentFile);
		fileContentBean.setFileMessageId(msgId);
		fileContentBean.setLinePosition(offset.lineNumber);
		CollectionUtils.getInstance().putMesssageQueue(fileContentBean);
	}
	
	/**
	 * 发送数据到消息队列<p>
	 * 数据格式：List<String>
	 * @param currentFile 文件信息
	 * @param recordlist 内容
	 * @param offset 位置
	 * */
	protected void putData(ExtractFileInfo currentFile, List<String> recordlist, FileOffset offset) {
		putData(currentFile, recordlist, offset, false);
	}
	
	/**
	 * 发送完成数据到消息队列<p>
	 * 数据格式：List<String>
	 * @param currentFile 文件信息
	 * @param offset 位置
	 * */
	protected void putData(ExtractFileInfo currentFile, FileOffset offset) {
		putData(currentFile, null, offset, true);
	}
		
	/**
	 * 如果某个应用处理文件一半半并挂了无法恢复，通过这个接管
	 * @return FileLock 锁对象
	 */
	private FileLock getOldestExpiredLock() {
		FileLock lock = null;
		long startTime = System.currentTimeMillis();
		LOG.info("开始获取过期文件");
		try {
			// 不需要太过频繁的扫描过期文件，浪费性能。默认配置2分钟扫描1次。
			if (CollectionUtils.hasExpired(lastExpiredLockTime, 120)) {
//			if (CollectionUtils.hasExpired(lastExpiredLockTime, 10)) {
				return null;
			}
			// 获取过期文件
			lock = lockService.acquireOldestExpiredLock(CollectionConfig.DEFAULT_LOCK_TIMEOUT);
//			lock = lockService.acquireOldestExpiredLock(5);
			// 如果扫到空，等待后在扫描。 如果为非空，下一次继续扫
			if (lock == null) {
				lastExpiredLockTime = System.currentTimeMillis();
			}
		} catch (Exception e) {
			LOG.error("获取过期文件锁发生异常", e);
		}
		LOG.info("获取过期文件用时{}", System.currentTimeMillis() - startTime);
		return lock;
	}
	
	/**
	 * 锁释放，从pendingFileReader队列移除
	 * */
	protected void markFileAsDone(FileReaderProcesssStatus reader, String fileName) {
		markFileAsDone(reader, fileName, null);
	}
	
	/**
	 * ACK操作
	 * */
	protected void markFileAsDone(FileReaderProcesssStatus reader, FileMessageId id) {
		markFileAsDone(reader, null, id);
	}
	
	/**
	 * 锁释放，从pendingFileReader队列移除
	 * 或者进行ACK操作
	 * */
	private void markFileAsDone(FileReaderProcesssStatus reader, String fileName, FileMessageId id) {
		if (reader == null) {
			LOG.warn("无法从pendingFileReader中获取到" + id.getFileName() + "当pending缓存:" + pendingFileReader);
			return;
		} else {
			FileLock fileLock = reader.getLock();
			// 有fileName，就是锁释放，从pendingFileReader队列移除
			if(fileName!=null) {
				// 释放锁
				new LockProcess(fileLock).releaseLock();
				// 最后从队列中移出
				pendingFileReader.remove(fileName);
			}
			// 有FileMessageId，就是做ACK操作
			else if(id!=null) {
				// 判断是否需要删除文件
				if (id.isFileReadCompletely()) {
					LOG.info("文件{}读取完成，进行后续操作(备份、清理、删除)", id.getFileName());					
					FileReader filereader = reader.getReader();
					// 将文件删除或者移走，或者其他清理操作。并记录指标信息
					try {
						fileListManager.complateFile(filereader.getFilePath(), filereader.getFileInfo(), filereader.getFileInfo().getFtpEnvVarName());
						// 释放锁
						new LockProcess(fileLock).releaseLock();
					} catch (Exception e) {// catch到的是fileListManager.complateFile抛出的异常
						// 锁未释放，但是已从pendingFileReader移出，等过期接管再处理
						LOG.error("文件处理完成后的清理操作发生异常。等待锁过期后重做此步骤。", e);
					}
					// 最后从队列中移出
					pendingFileReader.remove(filereader.getFileName());
				}
				// 更新Redis中文件位置
				else {
//					LOG.info("更新Redis中文件{}，位置{}", id.getFileName(), "char="+id.getCharOffset()+":line="+id.getLineNum());
//					// char=123:line=5
//					new LockProcess(fileLock).updateLock(id.getFileName(), "char="+id.getCharOffset()+":line="+id.getLineNum());					;
					new LockProcess(fileLock).heartBeat(new FileOffset(id.getCharOffset(), id.getLineNum()));
				}
			}
		}
	}
	
	/**
	 * ACK操作
	 * */
	public void ack(FileMessageId id) {
		pendingFileReader.ack(id);
	}
	
	/**
	 * 输入流帮助类
	 * */
	class ReaderHelper {
		
		/**
		 * 创建从文件开始读取的读取器
		 * @param file 读取文件对象
		 * @param lock 文件锁
		 * @return FileReader 文件流
		 * @throws IOException
		 */
		public FileReader createFileReader(ExtractFileInfo file, FileLock lock) throws Exception {
			try {
				String readerType = param.getComponentInitParam().get(PARAM_READER_TYPE);
				return fileListManager.createFileReader(readerType, param, file, null, true);
			} catch (EOFException e) {// 如果是空文件
				FtpNullFileReader reader = new FtpNullFileReader(null, file.getAbsolutePath(), param.getComponentInitParam(), file);
				return reader;
			}
		}
		
		/**
		 * 创建一个从“偏移”开始读取的读取器
		 * @param fileLock 文件锁
		 * @return FileReader 文件流
		 * @throws IOException
		 */
		public FileReader createFileReader(FileLock fileLock) throws Exception {
			String resumeFromOffset = fileLock.getFileOffset();
			String filePath = EtlUtils.getAbsolutePath(fileLock.getSourcePath(), fileLock.getFileName());
			ExtractFileInfo file = CollectionUtils.getFileInfo(fileLock);// 从锁信息中返构造出ExtractFileInfo对象
			FileReader fr = null;
			try {
				String readerType = param.getComponentInitParam().get(PARAM_READER_TYPE);			
				fr = fileListManager.createFileReader(readerType, param, file, resumeFromOffset, true);
			} catch (FTPException e) {// 文件不存在也只会抛出FTPException，所以需要特别处理
				LOG.error("异常信息："+e.getMessage());
				try {
					CollectionException.catchFtpException(e);// 区分FTP具体异常
				} catch (FileNotFoundException ex) {
					LOG.warn("文件" + filePath + "已不存在，无法重做.删除该文件过期锁。" + ex.getMessage());
					new LockProcess(fileLock).releaseLock();
				}
			} catch (EOFException e) {// 如果是空文件
				FtpNullFileReader reader = new FtpNullFileReader(null, file.getAbsolutePath(), param.getComponentInitParam(), file);
				return reader;
			} catch (FileNotFoundException e) {// 这里是为了HDFS采集而catch的
				LOG.warn("文件" + filePath + "已不存在，无法重做.删除该文件过期锁。" + e.getMessage());
				new LockProcess(fileLock).releaseLock();
			} catch (IOException e) {
				LOG.error("文件" + filePath + "读取时出现异常，将文件移动往异常目录", e);
				fileListManager.errorFile(file);// 异常文件处理
			}
			return fr;
		}
		
		/**
		 * 关闭流
		 * @param reader 文件流
		 * */
		public void closeReader(FileReader reader) {
			try {
				// 需要在这里释放连接。否则出错文件多了，会导致连接泄露
				if (reader != null) {
					LOG.info("关闭读取文件流：{}", reader.getFileName());
					reader.close();
				}
			} catch (Exception e) {
				LOG.error("关闭reader发生异常:", e);
			}
		}
	}
	
	/**
	 * 当前所有未关闭的reader
	 * */
	class PendingFileReader {
		// 当前所有未关闭的reader
		private Map<String, FileReaderProcesssStatus> pendingFileReader = null;
		
		public PendingFileReader() {
			this.pendingFileReader = new ConcurrentHashMap<>();
		}
		
		/**
		 * 是否超出队列限制
		 * */
		public boolean isOutOfSize() {
			return (pendingFileReader!=null && pendingFileReader.size()<CollectionConfig.DEAL_SIZE);
		}
		
		/**
		 * 增加值
		 * */
		public void put(String key, FileReaderProcesssStatus value) {
			pendingFileReader.put(key, value);
		}
		
		/**
		 * 移除值
		 * */
		public void remove(Object key) {
			pendingFileReader.remove(key);
		}
		
		/**
		 * 获取keySet
		 * */
		public Set<String> keySet() {
			return pendingFileReader.keySet();
		}
		
		/**
		 * 是否有下一个元素
		 * */
		public boolean hasNext() {
			return pendingFileReader.entrySet().iterator().hasNext();
		}
		
		/**
		 * 获取下一个Value
		 * */
		public FileReaderProcesssStatus next() {
			return pendingFileReader.entrySet().iterator().next().getValue();
		}		
		
		/**
		 * 通过FileMessageId进行ack
		 * */
		public void ack(FileMessageId id) {
			if(id!=null) {
				LOG.info("ack收到应答消息:{} {}", id.fileName, id.lineNumber);
				FileReaderProcesssStatus reader = pendingFileReader.get(id.getFileName());
				markFileAsDone(reader, id);
			}
		}
	}
	
	/**
	 * 锁处理
	 * */
	class LockProcess {
		// 需要加锁的对象
		private ExtractFileInfo currentFile = null;
		// 加锁后的对象
		private FileLock lock = null;
		// 组件实例唯一ID
		private String componentId = lockService.getSpoutId();
		
		public LockProcess(ExtractFileInfo currentFile) {
			this.currentFile = currentFile;
		}
		
		public LockProcess(FileLock lock) {
			this.lock = lock;
		}
		
		public FileLock getLock() {
			return lock;
		}

		/**
		 * 加锁
		 * */
		public boolean addLock() {
			boolean flag = false;
			if( lock==null && currentFile!=null ){
				try {
					lock = lockService.tryLock(currentFile);
					// 加锁失败
					if( lock == null) {
						LOG.info("无法获得文件锁 FileLock for {}, so skipping it.", currentFile);
						flag = false;
					}
					// 加锁成功
					else {
						flag = true; 
					}
				} catch (NodeExistsException e) {
					LOG.warn("无法加上FileLock {}，已经被锁定{}", currentFile.getFileName(), componentId);
					lock = null;
				} catch (Exception e) {
					LOG.error("对文件加锁时发生异常 " + currentFile.getFileName(), e);
					lock = null;
				}
			}
			return flag;
		}
		
		/**
		 * 释放锁
		 * */
		public boolean releaseLock() {
			boolean flag = false;
			try {
				if (lock != null) {
					LOG.info("开始请求释放锁");
					lockService.release(lock);
					LOG.info("释放文件锁 {}. SpoutId = {}", lock.getLockFileNode(), this.componentId);
					flag = true;
				}
			} catch (Exception e) {// 重试一次
				try {
					lockService.release(lock);
					flag = true;
				} catch (Exception e1) {
					LOG.error("Unable to delete lock file : " + lock.getLockFileNode() + " SpoutId =" + componentId, e1);
				}
			}
			return flag;
		}
		
		/**
		 * 更新锁中的文件位置
		 * */
		public void heartBeat(FileOffset position) {
			if (lock!=null && position!=null) {
				String pos = position.toString();
				try {
					lockService.heartBeat(lock, pos);
					LOG.info("保存文件锁处理进度 {} progress. position {}", lock.getLockFileNode(), pos);
				} catch (Exception e) {
					LOG.error("Unable to commit progress Will retry later. Spout ID = " + componentId, e);
				}
			}
		}
		
		/**
		 * 更新锁
		 * */
		public boolean updateLock(String fileName, String fileOffset) {
			return lockService.updateFileRecord(fileName, fileOffset);
		}
	}
}
