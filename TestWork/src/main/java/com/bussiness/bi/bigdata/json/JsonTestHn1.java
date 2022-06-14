package com.bussiness.bi.bigdata.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.bussiness.bi.bigdata.bean.ComparatorUserClassbean;
import com.bussiness.bi.bigdata.bean.UserClass1;
import com.bussiness.bi.bigdata.bean.UserClass2;

public class JsonTestHn1 {
	private Map<String, UserClass1> userclass1list;	
	public Map<String, UserClass1> getUserclass1list() {
		return userclass1list;
	}
	public void setUserclass1list(Map<String, UserClass1> userclass1list) {
		this.userclass1list = userclass1list;
	}

	public static void main(String[] args) throws Exception {
//		String js = "{\"app_name\":\"17173\",\"apply_classify1_name\":\"游戏\",\"apply_classify2_name\":\"综合游戏\",\"date\":\"20160101\",\"flux\":\"1024\",\"flux_2g\":\"0\",\"flux_3g\":\"0\",\"flux_4g\":\"1024\",\"flux_id\":\"1\",\"home_county\":\"HNSY\",\"msisdn\":\"13509323824\",\"rank_id\":0,\"visit_cnt\":\"1\",\"visit_time\":\"1\"}";
//		JSONObject jsonobj = JSONObject.fromObject(js);
//		System.out.println(((Qry_net_log_app_user_total)JSONObject.toBean(jsonobj, Qry_net_log_app_user_total.class)).getApp_name());
//		Person p1 = new Person("cc", "a1", 20);
//		JSONObject jsonobj = JSONObject.fromObject(p1);
//		System.out.println(jsonobj.toString());
//		System.out.println(((Person)JSONObject.toBean(jsonobj, Person.class)).getAge());
//		String table_name = "person_d";
//		Class<?> clazz = Class.forName("com.newland.bi.bigdata.bean."+captureName(table_name.substring(0, table_name.length()-2)));
//		Method mtoBean = clazz.getDeclaredMethod("toBean", String.class);
//		Object obj = clazz.newInstance();
//		mtoBean.invoke(obj, "bb");
//		Object o1 = p1;
//		mtoBean.invoke(o1, "bb");
//		String sort_filed = "name";
//		Method mget = clazz.getDeclaredMethod("get"+captureName(sort_filed));
//		System.out.println(mget.invoke(o1));
		
//		String jsclass = "{\"all_flux\":10240,\"all_visit_time\":100,\"classify_name\":\"社交\",\"flux\":3516,\"u2\":[],\"u2_jsonstr\":\"[{\\\"all_flux\\\":10240,\\\"all_visit_time\\\":100,\\\"classify_name\\\":\\\"唱歌\\\",\\\"flux\\\":2048,\\\"visit_time\\\":20},{\\\"all_flux\\\":10240,\\\"all_visit_time\\\":100,\\\"classify_name\\\":\\\"社交网站\\\",\\\"flux\\\":1024,\\\"visit_time\\\":10},{\\\"all_flux\\\":10240,\\\"all_visit_time\\\":100,\\\"classify_name\\\":\\\"视频\\\",\\\"flux\\\":248,\\\"visit_time\\\":3},{\\\"all_flux\\\":10240,\\\"all_visit_time\\\":100,\\\"classify_name\\\":\\\"社交其他\\\",\\\"flux\\\":196,\\\"visit_time\\\":2}]\",\"userclass2list\":null,\"visit_time\":36}";
//		JSONObject jj = JSONObject.fromObject(jsclass);
//		UserClass1 ur = (UserClass1)JSONObject.toBean(jj, UserClass1.class);
//		String urjsonstr = ur.getU2_jsonstr();
//		JSONArray urjsonstrobj = JSONArray.fromObject(urjsonstr);
//		UserClass2[] ur2 = (UserClass2[])JSONArray.toArray(urjsonstrobj, UserClass2.class);
//		for(UserClass2 uu2 : ur2){
//			System.out.println(uu2.getClassify_name()+" "+uu2.getFlux());
//		}
		
//		String tableName = "qry_net_log_web_user_total_m";
//		System.out.println("[string to bean]"+tableName.substring(0, tableName.length()-2));
//		if(tableName.substring(tableName.length()-1, tableName.length()).equals("m")){//年表,到月
//			System.out.println("m");
//		}else if(tableName.substring(tableName.length()-1, tableName.length()).equals("d")){//月表,到天
//			System.out.println("d");
//		}
		
		JsonTestHn1 jth = new JsonTestHn1();
		List<String> valuelist = new ArrayList<String>();
//		valuelist.add("20160101,13509323824,微博,社交,社交网站,10,100,1024,10240");
//		valuelist.add("20160101,13509323824,K歌,社交,唱歌,20,100,2048,10240");
//		valuelist.add("20160101,13509323824,百度贴吧,论坛,聊天发牢骚,50,100,2049,10240");
//		valuelist.add("20160101,13509323824,17173,游戏,综合游戏,5,100,510,10240");
//		valuelist.add("20160101,13509323824,115网盘,其他,其他,5,100,512,10240");
//		valuelist.add("20160101,13509323824,12530WAP门户,视听,音乐,5,100,511,10240");
//		valuelist.add("20160101,13509323824,12580生活,系统工具,下载工具,5,100,489,10240");
//		valuelist.add("20160101,13509323824,126邮箱,系统工具,综合软件,5,100,443,10240");
//		valuelist.add("20160101,13509323824,19楼,生活服务,便捷生活,5,100,515,10240");
//		valuelist.add("20160101,13509323824,QQ,社交,聊天,20,100,248,10240");
//		valuelist.add("20160101,13509323824,QQ视频,社交,视频,20,100,148,10240");
//		valuelist.add("20160101,13509323824,真心话大冒险,社交,丽人网,20,100,48,10240");

		valuelist.add("20160101,13509323824,微博,社交,NULL,10,100,1024,10240");
		valuelist.add("20160101,13509323824,K歌,社交,NULL,20,100,2048,10240");
		valuelist.add("20160101,13509323824,百度贴吧,论坛,聊天发牢骚,50,100,2049,10240");
		valuelist.add("20160101,13509323824,17173,游戏,综合游戏,5,100,510,10240");
		valuelist.add("20160101,13509323824,115网盘,其他,其他,5,100,512,10240");
		valuelist.add("20160101,13509323824,12530WAP门户,视听,音乐,5,100,511,10240");
		valuelist.add("20160101,13509323824,12580生活,系统工具,下载工具,5,100,489,10240");
		valuelist.add("20160101,13509323824,126邮箱,系统工具,综合软件,5,100,443,10240");
		valuelist.add("20160101,13509323824,19楼,生活服务,便捷生活,5,100,515,10240");
		valuelist.add("20160101,13509323824,QQ,社交,聊天,20,100,248,10240");
		valuelist.add("20160101,13509323824,QQ视频,社交,视频,20,100,148,10240");
		valuelist.add("20160101,13509323824,真心话大冒险,社交,丽人网,20,100,48,10240");
		for(String _tmp: valuelist){
			String[] arr = _tmp.split(",");
			UserClass1 _userClass1 = new UserClass1(arr[3],Float.valueOf(arr[5]),Float.valueOf(arr[6]),Float.valueOf(arr[7]),Float.valueOf(arr[8]));			
			jth.addUserClass1(_userClass1);
			UserClass2 _userClass2 = new UserClass2(arr[4],Float.valueOf(arr[5]),Float.valueOf(arr[6]),Float.valueOf(arr[7]),Float.valueOf(arr[8]));			
			jth.getUserclass1list().get(arr[3]).addUserClass2(_userClass2);
		}
		List<UserClass1> u1 = new ArrayList<UserClass1>();
		for(Map.Entry<String,UserClass1> e : jth.getUserclass1list().entrySet()){
			u1.add(e.getValue());
			e.getValue().sort(0);//class2排序
		}		
		ComparatorUserClassbean cuc = new ComparatorUserClassbean();//默认flux排序		
		Collections.sort(u1,cuc);//class1排序
		jth.dealList(u1);
		
		for(UserClass1 ux : u1){
			JSONArray jsonobj = JSONArray.fromObject(ux.getU2());
			ux.setU2_jsonstr(jsonobj.toString());
			ux.setU2(null);
			ux.setUserclass2list(null);
			JSONObject jsonobj1 = JSONObject.fromObject(ux);
			System.out.println(jsonobj1.toString());
		}

//		float f1 = 20481234567l/1024l;
//		System.out.println(f1);
//		System.out.println(String.format("%f", f1));
//		System.out.println(String.valueOf(f1));
//		System.out.println(new String("中午".getBytes()));
//		System.out.println(java.net.URLDecoder.decode("", "GB2312"));
		
//		String a = "中午";
//		for(Byte ab : a.getBytes()){
//			System.out.println(ab);
//		}
//		byte[] abc = {-48,-62,83,76,83,48,55,54,0,0,0,0,0,0,0,0};
//		System.out.println(new String(abc, "GB2312"));
//		System.out.println(java.net.URLDecoder.decode(new String(abc, "GB2312"), "GB2312"));
	}
	
	public void dealList(List<UserClass1> list){
		int i = 0;		
		if(list.size()>4){
			Iterator<UserClass1> it = list.iterator();
			UserClass1 objother = null;
			while(it.hasNext()){
				i++;
				UserClass1 obj = it.next();
				if(i<4){
					obj.dealList();
				}else if(i==4){
					objother = obj;
					objother.setClassify_name("其他");
				}else if(i>4){
					objother.addFlux(obj.getFlux());
					objother.getU2().addAll(obj.getU2());
					it.remove();
				}
			}
			objother.dealListOther();
		}
	}
	
	public void addUserClass1(UserClass1 _userClass1){
		if(userclass1list==null){
			userclass1list = new HashMap<String, UserClass1>();
		}
		if(userclass1list.get(_userClass1.getClassify_name())==null){
			userclass1list.put(_userClass1.getClassify_name(), _userClass1);
		}else{
			userclass1list.get(_userClass1.getClassify_name()).addFlux(_userClass1.getFlux());
			userclass1list.get(_userClass1.getClassify_name()).addVisit_time(_userClass1.getVisit_time());
		}
	}
	
	/**
	 * 首字母大写
	 * */
	public static String captureName(String name){
		if(name!=null && name.length()>0){
			char[] cs = name.toCharArray();
			cs[0] -= 32;
			return String.valueOf(cs);
		}else{
			return "";
		}
	}
}
