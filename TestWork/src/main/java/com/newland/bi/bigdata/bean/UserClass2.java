package com.newland.bi.bigdata.bean;

public class UserClass2 implements UserClassInterface {
	private String classify_name = "";
	private float visit_time = 0;
	private float all_visit_time = 0;
	private float flux = 0;
	private float all_flux = 0;
	public UserClass2(){}
	public UserClass2(String _classify_name,float _visit_time,float _all_visit_time,
			float _flux,float _all_flux){
		this.classify_name = _classify_name;
		this.visit_time = _visit_time;
		this.all_visit_time = _all_visit_time;
		this.flux = _flux;
		this.all_flux = _all_flux;
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
}
