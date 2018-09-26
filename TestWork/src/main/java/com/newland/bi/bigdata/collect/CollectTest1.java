package com.newland.bi.bigdata.collect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cqx.process.LogInfoFactory;

public class CollectTest1 {

	private LogInfoFactory log = LogInfoFactory.getInstance();
	public static final String SPLIT_STR = ",";
	
	/**
	 * 去重
	 * @param list
	 * */
	public static void duplicateRemoval(String s){
		HashSet<String> hs = new HashSet<String>();
		String[] list = s.split(SPLIT_STR);
		for(String str: list){
			hs.add(str);
		}
		Iterator<String> it = hs.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	
	public void iterator(){
		List<String> list = new ArrayList<String>();
		Iterator<String> it = list.iterator();		
	}
	
	/**
	 * 业务模拟-连接查询
	 * @throws Exception 
	 * */
	public List<VarInfo> qryConn() throws Exception{
		List<VarInfo> varInfos = new ArrayList<>();//总的
		List<VarInfo> conns = new ArrayList<VarInfo>();//连接的		
		conns.add(new VarInfo("tns", "123"));
		conns.add(new VarInfo("username", "hive"));
		conns.add(new VarInfo("password", "3edc#EDC"));
		varInfos.addAll(conns);
		List<VarInfo> resources = new ArrayList<>();//资源的
		resources.add(new VarInfo("tns", ""));
		resources.add(new VarInfo("ip", "10.1.8.75"));
		resources.add(new VarInfo("port", "10000"));
		varInfos.addAll(resources);
		return varInfos;
	}
	
	/**
	 * 业务模拟-连接查询-如果连接有，就不在使用资源
	 * @throws Exception 
	 * */
	public List<VarInfo> qryConnDist() {
		DistArrayList<VarInfo> distarraylist =  new DistArrayList<VarInfo>();
//		distarraylist.setDistKeyMethod(VarInfo.class.getMethod("getVarName"));
//		distarraylist.setDistKeyMethod(VarInfo.class.getMethod("getId"));
//		distarraylist.setDistKeyMethod(VarInfo.class.getMethod("a"));
		distarraylist.setDistKeyMethod(VarInfo.class, "getVarName");
//		distarraylist.setDistKeyMethod(VarInfo.class, "a");
		List<VarInfo> conns = new ArrayList<VarInfo>();//连接的		
		conns.add(new VarInfo("tns", "123"));
		conns.add(new VarInfo("username", "hive"));
		conns.add(new VarInfo("password", "3edc#EDC"));
		distarraylist.addAll(conns);
		List<VarInfo> resources = new ArrayList<>();//资源的
		resources.add(new VarInfo("tns", ""));
		resources.add(new VarInfo("ip", "10.1.8.75"));
		resources.add(new VarInfo("port", "10000"));
		distarraylist.addAll(resources);
		return distarraylist.getDistList();
	}
	
	class VarInfo {
	    private String varName;//变量名称
	    private String varValue;//变量值
	    public VarInfo(String varName, String varValue){
	    	this.varName = varName;
	    	this.varValue = varValue;
	    }
		public String getVarName() {
			return varName;
		}
		public void setVarName(String varName) {
			this.varName = varName;
		}
		public String getVarValue() {
			return varValue;
		}
		public void setVarValue(String varValue) {
			this.varValue = varValue;
		}
		public int getId(){
			return 1;
		}
		@Override
		public String toString(){
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}
	
	/**
	 * 
	 * */
	class DistArrayList<E> {
		private Map<String, E> distmap = new HashMap<String, E>();
		private Method distKeymethod;
		private long mount = 0;
		
		public void setDistKeyMethod(Method method){
			if(method.getReturnType().equals(String.class)){
				this.distKeymethod = method;
			}
		}
		
		public void setDistKeyMethod(Class<E> clazz, String methodName){
			Method method = null;
			try {
				method = clazz.getMethod(methodName);
			} catch (NoSuchMethodException | SecurityException e1) {
				log.warn(e1.toString());
			}
			if(method!=null && method.getReturnType().equals(String.class)){
				this.distKeymethod = method;
			}
		}
		
		private Method getDistKeyMethod(){
			return this.distKeymethod;
		}
		
		private void add(E e) {
			if(this.distKeymethod == null){
				distmap.put(String.valueOf(mount++), e);
			}else{
				String k = null;
				try {
					k = (String) getDistKeyMethod().invoke(e);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e1) {
					log.warn(e1.toString());
				}
				if(distmap.get(k) ==null )
					distmap.put(k, e);
			}
		}
		
		public void addAll(Collection<? extends E> e) {
			if( e !=null )
				for(E ment : e){
					add(ment);
				}
		}
		
		private List<E> MapToList(){
			List<E> distlist = new ArrayList<E>();
			for(java.util.Map.Entry<String, E> es : distmap.entrySet()){
				distlist.add(es.getValue());
			}
			return distlist;
		}
		
		public List<E> getDistList(){			
			return MapToList();
		}
	}
	
	public static void printList(List<?> list){
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
	
	public static void main(String[] args) throws Exception {
//		List a;
//		Set b;		
//		HashSet c;
//		TreeSet d;
//		d = new TreeSet();
//		d.add("a");
//		d.add("a");
//		d.add("a");
//		Iterator it = d.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next());
//		}		
		
//		String str = "数据导入,数据导入,数据导入,数据导入,数据导入,数据导出,数据导出,数据传送,数据传送,数据传送,数据传送,数据删除,数据删除,数据删除,调度,公共函数,预处理,采集,mr程序,mr程序,webservice服务,公共脚本,公共脚本";
//		CollectTest1.duplicateRemoval(str);
		
		CollectTest1 ct1 = new CollectTest1();
//		printList(ct1.qryConn());
		printList(ct1.qryConnDist());
	}
}
