package com.bussiness.bi.bigdata.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserClass1 implements UserClassInterface {
	private String classify_name = "";
	private float visit_time = 0;
	private float all_visit_time = 0;
	private float flux = 0;
	private float all_flux = 0;
	private Map<String, UserClass2> userclass2list;
	private List<UserClass2> u2;
	private String u2_jsonstr;
	public UserClass1(){}
	public UserClass1(String _classify_name,float _visit_time,float _all_visit_time,
			float _flux,float _all_flux){
		this.classify_name = _classify_name;
		this.visit_time = _visit_time;
		this.all_visit_time = _all_visit_time;
		this.flux = _flux;
		this.all_flux = _all_flux;
	}
	public String getU2_jsonstr() {
		return u2_jsonstr;
	}
	public void setU2_jsonstr(String u2_jsonstr) {
		this.u2_jsonstr = u2_jsonstr;
	}
	public List<UserClass2> getU2() {
		return u2;
	}
	public void setU2(List<UserClass2> u2) {
		this.u2 = u2;
	}
	public Map<String, UserClass2> getUserclass2list() {
		return userclass2list;
	}
	public void setUserclass2list(Map<String, UserClass2> userclass2list) {
		this.userclass2list = userclass2list;
	}
	public String getClassify_name() {
		return classify_name;
	}
	public void setClassify_name(String classify_name) {
		this.classify_name = classify_name;
	}
	public float getVisit_time() {
		return visit_time;
	}
	public void setVisit_time(float visit_time) {
		this.visit_time = visit_time;
	}
	public float getAll_visit_time() {
		return all_visit_time;
	}
	public void setAll_visit_time(float all_visit_time) {
		this.all_visit_time = all_visit_time;
	}
	public float getFlux() {
		return flux;
	}
	public void setFlux(float flux) {
		this.flux = flux;
	}
	public float getAll_flux() {
		return all_flux;
	}
	public void setAll_flux(float all_flux) {
		this.all_flux = all_flux;
	}
	public void addFlux(float _flux){
		this.flux += _flux;
	}
	public void addVisit_time(float _visit_time){
		this.visit_time += _visit_time;
	}
	public void addUserClass2(UserClass2 _userClass2){
		if(userclass2list==null){
			userclass2list = new HashMap<String, UserClass2>();
		}
		if(userclass2list.get(_userClass2.getClassify_name())==null){
			userclass2list.put(_userClass2.getClassify_name(), _userClass2);
		}else{
			userclass2list.get(_userClass2.getClassify_name()).addFlux(_userClass2.getFlux());
			userclass2list.get(_userClass2.getClassify_name()).addVisit_time(_userClass2.getVisit_time());
		}
	}
	public void sort(int flag){
		if(u2==null){
			u2 = new ArrayList<UserClass2>();
		}
		for(Map.Entry<String,UserClass2> e : userclass2list.entrySet()){
			u2.add(e.getValue());
		}		
		ComparatorUserClassbean cuc = new ComparatorUserClassbean();//默认flux排序
		cuc.setFlag(flag);
		Collections.sort(u2,cuc);
	}
	public void dealList() {
		int i = 0;		
		if(u2!=null && u2.size()>4){
			Iterator<UserClass2> it = u2.iterator();
			UserClass2 objother = null;
			while(it.hasNext()){
				i++;
				UserClass2 obj = it.next();
				if(i==4){
					objother = obj;
					if(!this.classify_name.equals("其他"))
						objother.setClassify_name(classify_name+"其他");
				}else if(i>4){
					objother.addFlux(obj.getFlux());
					it.remove();
				}
			}
		}
	}
	public void dealListOther() {
		int i = 0;		
		if(u2!=null && u2.size()>0){
			Iterator<UserClass2> it = u2.iterator();
			UserClass2 objother = null;
			while(it.hasNext()){
				i++;
				UserClass2 obj = it.next();
				if(i==1){
					objother = obj;
					objother.setClassify_name("其他");
				}else if(i>1){
					objother.addFlux(obj.getFlux());
					it.remove();
				}
			}
		}
	}
}
