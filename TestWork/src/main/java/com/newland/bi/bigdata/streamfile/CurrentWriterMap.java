package com.newland.bi.bigdata.streamfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.newland.bi.bigdata.bean.CycleUtils;
import com.newland.bi.bigdata.thread.WriterBean;
import com.newland.storm.component.etl.hdfs.common.AbstractHDFSWriter;

public class CurrentWriterMap {
	
	private Map<String, WriterBean> writer;
	/**
	 * 落地对象最小周期
	 */
	private String minCycle;
	
	/**
	 * 用于循环
	 */
	private Iterator<Map.Entry<String, WriterBean>> iterator = null;

	public CurrentWriterMap() {
		this.writer = new HashMap<String, WriterBean>();
		this.iterator = writer.entrySet().iterator();
	}
	
	public synchronized void putWriter(AbstractHDFSWriter value) {
		WriterBean wb = new WriterBean(value);
		// 更新map对象
		writer.put(wb.getPathAndFileName(), wb);
		iterator = writer.entrySet().iterator();
		// 落地对象最小周期为空，更新
		if(minCycle==null)
			minCycle = wb.getFileDataDate();
	}

	private synchronized void remove(String key, boolean isErrorFile) {
		if(!isErrorFile) {
			// 移除对象设置为当前最小周期，更新
			minCycle = writer.get(key).getFileDataDate();
		}
		writer.remove(key);
		iterator = writer.entrySet().iterator();
	}
	
	public synchronized void remove(String key) {
		remove(key, false);
	}
	
	public synchronized void errorFileRemove(String key) {
		remove(key, true);
	}
	
	/**
	 * 由于周期切换要从头循环，所以需要对迭代复位
	 */
	public synchronized void resetIterator() {
		this.iterator = writer.entrySet().iterator();
	}
	
	/**
	 * 是否有下一个元素
	 * 
	 * @return
	 */
	public synchronized boolean hasNext() {
		return iterator.hasNext();
	}
	
	/**
	 * 获取下一个Value
	 * 
	 * @return
	 */
	public synchronized WriterBean next() {
		return iterator.next().getValue();
	}

	public synchronized boolean isClose(String key) {
		return writer.get(key) == null ? null : writer.get(key).getAbstractHDFSWriter().isClose();
	}

	public synchronized String toJson() {
		return JSON.toJSONString(writer.values());
	}

	/**
	 * 获取当前map最小周期
	 * 
	 * @return
	 */
	public synchronized String getMinCycle() {
		CycleUtils cycleUtils = new CycleUtils();
		for (Map.Entry<String, WriterBean> obj : writer.entrySet()) {
			cycleUtils.add(obj.getValue().getFileDataDate());
		}
		return cycleUtils.getSortFirst();
	}
	
	public static void main(String[] args) {
		CurrentWriterMap currentWriterMap = new CurrentWriterMap();
		System.out.println(currentWriterMap.getMinCycle()==null);
	}
}
