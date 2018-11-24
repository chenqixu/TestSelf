package com.newland.bi.bigdata.thread;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.storm.component.etl.ftp.spout.FileReader;
import com.newland.storm.component.etl.ftp.spout.lock.FileLock;

public class CollectionThreadPending {
	
	private static final Logger log = LoggerFactory.getLogger(CollectionThreadPending.class);
	// 当前所有未关闭的reader
	private PendingFileReader pendingFileReader = null;
	
	public CollectionThreadPending() {
		init();
	}
	
	public void init() {
		this.pendingFileReader = new PendingFileReader();
		this.pendingFileReader.put("123", new FileReaderProcesssStatus("HTTP_03_20181030_222524_60.TXT"));
		this.pendingFileReader.put("234", new FileReaderProcesssStatus("HTTP_03_20181030_222524_61.TXT"));
		this.pendingFileReader.put("345", new FileReaderProcesssStatus("HTTP_03_20181030_222524_62.TXT"));
		this.pendingFileReader.put("456", new FileReaderProcesssStatus("HTTP_03_20181030_222524_63.TXT"));
		this.pendingFileReader.put("567", new FileReaderProcesssStatus("HTTP_03_20181030_222524_64.TXT"));
		this.pendingFileReader.put("678", new FileReaderProcesssStatus("HTTP_03_20181030_222524_65.TXT"));
		this.pendingFileReader.put("789", new FileReaderProcesssStatus("HTTP_03_20181030_222524_66.TXT"));
		this.pendingFileReader.put("890", new FileReaderProcesssStatus("HTTP_03_20181030_222524_67.TXT"));
		this.pendingFileReader.put("901", new FileReaderProcesssStatus("HTTP_03_20181030_222524_68.TXT"));
		this.pendingFileReader.put("011", new FileReaderProcesssStatus("HTTP_03_20181030_222524_69.TXT"));
	}
	
	/**
	 * 当前所有未关闭的reader
	 * */
	class PendingFileReader {
		// 当前所有未关闭的reader
		private Map<String, FileReaderProcesssStatus> pendingFileReader = null;
		// 用于循环
		private Iterator<Map.Entry<String, FileReaderProcesssStatus>> iterator = null;
		
		public PendingFileReader() {
			this.pendingFileReader = new ConcurrentHashMap<>();
			this.iterator = pendingFileReader.entrySet().iterator();
		}
		
		/**
		 * 当前reader队列大小
		 * @return
		 */
		public int size() {
			return pendingFileReader.size();
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
			iterator = pendingFileReader.entrySet().iterator();
		}
		
		/**
		 * 移除值
		 * */
		public void remove(Object key) {
			pendingFileReader.remove(key);
			iterator = pendingFileReader.entrySet().iterator();
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
			return iterator.hasNext();
		}
		
		/**
		 * 获取下一个Value
		 * */
		public FileReaderProcesssStatus next() {
//			Iterator<Map.Entry<String, FileReaderProcesssStatus>> it = pendingFileReader.entrySet().iterator();
//			Map.Entry<String, FileReaderProcesssStatus> obj = it.next();
//			String key = obj.getKey();
//			FileReaderProcesssStatus value = obj.getValue();
//			pendingFileReader.remove(key);
			return iterator.next().getValue();
//			return pendingFileReader.entrySet().iterator().next().getValue();
		}		
		
		/**
		 * 通过FileMessageId进行ack
		 * */
		public void ack(FileMessageId id) {
			if(id!=null) {
				log.info("ack收到应答消息:{} {}", id.fileName, id.lineNumber);
				pendingFileReader.get(id.getFileName());
			}
		}
		
		@Override
		public String toString() {
			return pendingFileReader.toString();
		}
	}

	/**
	 * 文件处理类
	 * 
	 * @author chenqixu
	 *
	 */
	class FileReaderProcesssStatus {

		protected FileReader reader;
		protected FileLock lock;
		// 开始处理时间
		protected long startReadTime;
		protected String fileName;

		public FileReaderProcesssStatus(String fileName) {
			this.fileName = fileName;
		}

		public FileReaderProcesssStatus(FileReader reader, FileLock lock) {
			this.reader = reader;
			this.lock = lock;
			this.startReadTime = System.currentTimeMillis();
		}

		public String getFileName() {
			return fileName;
		}

		public FileReader getReader() {
			return reader;
		}

		public void setReader(FileReader reader) {
			this.reader = reader;
		}

		public FileLock getLock() {
			return lock;
		}

		public void setLock(FileLock lock) {
			this.lock = lock;
		}

		public long getStartReadTime() {
			return startReadTime;
		}

		@Override
		public String toString() {
			if (reader != null) {
				return reader.getFileName();
			} else {
				return fileName;
			}
		}
	}
	
	public void clearNoAnswer() {
		log.info("pendingFileReader {}", pendingFileReader);
		log.info("☆☆☆ 清理pengdingFileReader中超时的任务，任务大小：{}", pendingFileReader.size());
		// 循环pendingFileReader
		while (pendingFileReader.hasNext()) {
			FileReaderProcesssStatus nextreader = pendingFileReader.next();
			log.info("☆☆☆ 准备清理文件：{}", nextreader.getFileName());
		}
		log.info("pendingFileReader {}", pendingFileReader);
	}
	
	class FiniteQueue {
		Map<String, String> finitequeue;
		int finitesize = 20;
		public FiniteQueue() {
			finitequeue = new LinkedHashMap<>();
			init();
		}
		public void init() {
			for(int i=0;i<20;i++){
				put(i+"");
			}
		}
		/**
		 * 如果大于finitesize，则从队头移走一个，数据加入队尾
		 * @param key
		 */
		public synchronized void put(String key) {
			if(finitequeue.size()>finitesize) {
				finitequeue.remove(next());				
			}
			finitequeue.put(key, key);
		}
		private synchronized String next() {
			Iterator<String> it = finitequeue.keySet().iterator();
			while(it.hasNext()) {
				return it.next();
			}
			return null;
		}
		public synchronized boolean find(String key) {
			return (finitequeue.get(key))==null?false:true;
			
		}
		public synchronized void query() {
			Iterator<String> it = finitequeue.keySet().iterator();
			while(it.hasNext()) {
				log.info("next {}", it.next());
			}
		}
	}
	
	public void finiteQueueTest() {
		FiniteQueue finiteQueue = new FiniteQueue();
		log.info("find {}", finiteQueue.find("0"));
		finiteQueue.put("20");
		finiteQueue.put("21");
		finiteQueue.put("22");
		finiteQueue.put("23");
		finiteQueue.query();
		log.info("find {}", finiteQueue.find("0"));
		log.info("find {}", finiteQueue.find("23"));
	}

	public static void main(String[] args) {
		CollectionThreadPending collectionThreadPending = new CollectionThreadPending();
		collectionThreadPending.finiteQueueTest();
	}
}
