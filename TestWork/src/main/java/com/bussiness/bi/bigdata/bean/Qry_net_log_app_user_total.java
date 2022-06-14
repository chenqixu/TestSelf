package com.bussiness.bi.bigdata.bean;

public class Qry_net_log_app_user_total implements Qry_net_log {
	private int rank_id;
	private String date;
	private String msisdn;
	private String home_county;
	private String flux;
	private String visit_cnt;
	private String visit_time;
	private String flux_2g;
	private String flux_3g;
	private String flux_4g;
	private String apply_classify1_name;
	private String apply_classify2_name;
	private String app_name;
	private String flux_id;
	public int getRank_id() {
		return rank_id;
	}
	public void setRank_id(int rank_id) {
		this.rank_id = rank_id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getHome_county() {
		return home_county;
	}
	public void setHome_county(String home_county) {
		this.home_county = home_county;
	}
	public String getFlux() {
		return flux;
	}
	public void setFlux(String flux) {
		this.flux = flux;
	}
	public String getVisit_cnt() {
		return visit_cnt;
	}
	public void setVisit_cnt(String visit_cnt) {
		this.visit_cnt = visit_cnt;
	}
	public String getVisit_time() {
		return visit_time;
	}
	public void setVisit_time(String visit_time) {
		this.visit_time = visit_time;
	}
	public String getFlux_2g() {
		return flux_2g;
	}
	public void setFlux_2g(String flux_2g) {
		this.flux_2g = flux_2g;
	}
	public String getFlux_3g() {
		return flux_3g;
	}
	public void setFlux_3g(String flux_3g) {
		this.flux_3g = flux_3g;
	}
	public String getFlux_4g() {
		return flux_4g;
	}
	public void setFlux_4g(String flux_4g) {
		this.flux_4g = flux_4g;
	}
	public String getApply_classify1_name() {
		return apply_classify1_name;
	}
	public void setApply_classify1_name(String apply_classify1_name) {
		this.apply_classify1_name = apply_classify1_name;
	}
	public String getApply_classify2_name() {
		return apply_classify2_name;
	}
	public void setApply_classify2_name(String apply_classify2_name) {
		this.apply_classify2_name = apply_classify2_name;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public String getFlux_id() {
		return flux_id;
	}
	public void setFlux_id(String flux_id) {
		this.flux_id = flux_id;
	}
	@Override
	public void toBean(String str) {
		String[] arr = str.split("\\|");
		if(arr==null) return;
		this.date = arr[0];
		this.msisdn = arr[1];
		this.home_county = arr[2];
		this.flux = arr[3];
		this.visit_cnt = arr[4];
		this.visit_time = arr[5];
		this.flux_2g = arr[6];
		this.flux_3g = arr[7];
		this.flux_4g = arr[8];
		this.apply_classify1_name = arr[9];
		this.apply_classify2_name = arr[10];
		this.app_name = arr[11];
		this.flux_id = arr[12];
	}
}
