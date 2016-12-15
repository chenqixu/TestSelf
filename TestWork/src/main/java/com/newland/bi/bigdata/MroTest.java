package com.newland.bi.bigdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * <b>msisdn 、lon、lat、objtimestamp（开始时间）、 residence_time(驻留时间）、sum_date  </b><br>
 * <b>处理逻辑：</b><br>
 * <b>1、</b>每天按用户号码分组，按objtimestamp（开始时间）从小到大排序<br>
 * <b>2、</b>对于同一个号码，按时间排序后的记录，（LON、LAT）相同的连续相邻记录需进行汇总，汇总后记录的开始时间要取这些经纬度相同的记录中时间最早的，记为时间A。<br>
 * residence_time(驻留时间），取下一条经纬度不同的记录）（如果存在）的objtimestamp（开始时间）-时间A。<br>
 * 对于不存在下一条经纬度不同的记录情况下，驻留时间取最后一条记录的objtimestamp（开始时间）-时间A。<br>
 * */
public class MroTest {
	private static List<MroBean> list = new ArrayList<MroBean>();
	public static void status_a() throws Exception {
		list.clear();
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT2"));
		// 处理
		run(list);
	}
	
	public static void status_b() throws Exception {
		list.clear();
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		// 处理
		run(list);
	}
	
	public static void status_c() throws Exception {
		list.clear();
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON2", "LAT2"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON1", "LAT1"));
		Thread.sleep(100);
		list.add(new MroBean("13509323824", new Date().getTime(), "LON3", "LAT3"));
		// 处理
		run(list);
	}
	
	public static void run(List<MroBean> list){		
		// 按时间排序
		ComparatorMroBean cmb = new ComparatorMroBean();
		Collections.sort(list, cmb);
		// 判断经度、纬度是否不一样，不一样就汇总统计
		MroSumDeal msd = new MroSumDeal();
		for(MroBean mb : list){
			msd.add(mb);
		}
		// 收尾
		msd.endDeal();
		// 打印结果
		for(MroBean mb : msd.getSumlist()){
			System.out.println(mb.toString());
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("======status a:======");
		// 情况1
		MroTest.status_a();
		System.out.println("======status b:======");
		// 情况2
		MroTest.status_b();
		System.out.println("======status c:======");
		// 情况3
		MroTest.status_c();
	}
}

/**
 * 汇总处理类
 * */
class MroSumDeal {
	List<MroBean> sumlist = new ArrayList<MroBean>();
	public List<MroBean> getSumlist() {
		return sumlist;
	}
	// 原始记录
	MroBean first = null;
	// 当前记录
	MroBean current = null;
	// 最后记录
	public void add(MroBean mb){
		if(mb!=null){
			// 判断sumlist是否为0，为0则初始化原始记录
			if(sumlist.size()==0 && first==null){
				// 原始记录 = mb
				first = mb;
			}
			// 当前记录 = mb;
			current = mb;
			// 判断当前记录 和 原始记录的经度、纬度是否不一样，不一样就汇总统计
			if(!first.getLon().equals(current.getLon()) || !first.getLat().equals(current.getLat())){
				sumlist.add(new MroBean(first.getMsisdn(), current.getDate()-first.getDate(), first.getLon(), first.getLat()));
				// 修改原始记录
				first = current;
			}
		}
	}
	// 收尾处理
	public void endDeal(){
		if(first!=null && current!=null)
			sumlist.add(new MroBean(first.getMsisdn(), current.getDate()-first.getDate(), first.getLon(), first.getLat()));
	}
}

/**
 * MRO数据bean
 * */
class MroBean {
	private String msisdn;
	private Long date;
	private String lon;
	private String lat;
	public MroBean(){}
	public MroBean(String _msisdn, Long _date, String _lon, String _lat){
		this.msisdn = _msisdn;
		this.date = _date;
		this.lon = _lon;
		this.lat = _lat;
	}
	@Override
	public String toString(){
		return this.msisdn+" "+this.date+" "+this.lon+" "+this.lat;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
}

/**
 * 按时间排序
 * */
class ComparatorMroBean implements Comparator<Object> {
	@Override
	public int compare(Object o1, Object o2) {
		MroBean n1 = (MroBean) o1;
		MroBean n2 = (MroBean) o2;
		Long date1 = 0L;
		Long date2 = 0L;
		try {
			date1 = n1.getDate();
			date2 = n2.getDate();
		} catch (Exception e) {
			System.out.println("MroBean Comparator ERR:"+e.toString());
			e.printStackTrace();
		}
		return date1.compareTo(date2);
	}
}
