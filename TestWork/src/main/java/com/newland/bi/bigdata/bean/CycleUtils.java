package com.newland.bi.bigdata.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.newland.bi.bigdata.time.TimeHelper;

/**
 * 周期比较
 * 
 * @author chenqixu
 *
 */
public class CycleUtils {

	private List<Cycle> cyclelist;

	public CycleUtils() {
		cyclelist = new ArrayList<Cycle>();
	}

	/**
	 * 添加周期
	 * @param currentFirstFileTime
	 */
	public void add(String currentFirstFileTime) {
		if(currentFirstFileTime==null || currentFirstFileTime.equalsIgnoreCase("null")) return;
		cyclelist.add(new Cycle(currentFirstFileTime));
	}

	/**
	 * 排序
	 */
	private void sort() {
		Collections.sort(cyclelist);
	}

	/**
	 * 排序后获取第一个元素的值
	 * @return
	 */
	public String getSortFirst() {
		sort();
		if (cyclelist.size() > 0)
			return cyclelist.get(0).getCurrentFirstFileTime();
		return null;
	}

	/**
	 * 周期比较
	 * @author chenqixu
	 *
	 */
	class Cycle implements Comparable<Cycle> {
		// 当前扫描最早文件对应的文件时间
		private String currentFirstFileTime;

		public Cycle(String currentFirstFileTime) {
			this.currentFirstFileTime = currentFirstFileTime;
		}

		public String getCurrentFirstFileTime() {
			return currentFirstFileTime;
		}

		@Override
		public int compareTo(Cycle o) {
			// 比较规则
			return TimeHelper.timeComparison(this.currentFirstFileTime,
					o.currentFirstFileTime);
		}
	}

}
