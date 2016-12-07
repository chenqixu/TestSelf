package com.newland.bi.bigdata.bean;

public class Cfg_web_class_name {
	private String web_classify1;
	private String web_classify1_name;
	private String web_classify2;
	private String web_classify2_name;
	private String web_id;
	private String web_name;
	private String domain_level1;
	private String domain_level1_name;
	private String web_classify3;
	private String web_classify3_name;
	private String domain_level2;
	private String domain_level2_name;
	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getWeb_classify1() {
		return web_classify1;
	}
	public void setWeb_classify1(String web_classify1) {
		this.web_classify1 = web_classify1;
	}
	public String getWeb_classify1_name() {
		return web_classify1_name;
	}
	public void setWeb_classify1_name(String web_classify1_name) {
		this.web_classify1_name = web_classify1_name;
	}
	public String getWeb_classify2() {
		return web_classify2;
	}
	public void setWeb_classify2(String web_classify2) {
		this.web_classify2 = web_classify2;
	}
	public String getWeb_classify2_name() {
		return web_classify2_name;
	}
	public void setWeb_classify2_name(String web_classify2_name) {
		this.web_classify2_name = web_classify2_name;
	}
	public String getWeb_id() {
		return web_id;
	}
	public void setWeb_id(String web_id) {
		this.web_id = web_id;
	}
	public String getWeb_name() {
		return web_name;
	}
	public void setWeb_name(String web_name) {
		this.web_name = web_name;
	}
	public String getDomain_level1() {
		return domain_level1;
	}
	public void setDomain_level1(String domain_level1) {
		this.domain_level1 = domain_level1;
	}
	public String getDomain_level1_name() {
		return domain_level1_name;
	}
	public void setDomain_level1_name(String domain_level1_name) {
		this.domain_level1_name = domain_level1_name;
	}
	public String getWeb_classify3() {
		return web_classify3;
	}
	public void setWeb_classify3(String web_classify3) {
		this.web_classify3 = web_classify3;
	}
	public String getWeb_classify3_name() {
		return web_classify3_name;
	}
	public void setWeb_classify3_name(String web_classify3_name) {
		this.web_classify3_name = web_classify3_name;
	}
	public String getDomain_level2() {
		return domain_level2;
	}
	public void setDomain_level2(String domain_level2) {
		this.domain_level2 = domain_level2;
	}
	public String getDomain_level2_name() {
		return domain_level2_name;
	}
	public void setDomain_level2_name(String domain_level2_name) {
		this.domain_level2_name = domain_level2_name;
	}
	public void toBean(String str) {
		if(str!=null && str.trim().length()>0){
			String[] tmp = str.split(",");
			this.url = tmp[0];
			this.web_id=tmp[1];
			this.web_classify1=tmp[2];
			this.web_classify1_name=tmp[3];
			this.web_classify2=tmp[4];
			this.web_classify2_name=tmp[5];
			if(tmp.length==7)
				this.web_name=tmp[6];
			else
				this.web_name="";
		}
	}
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(this.web_classify1);
		sb.append(",");
		sb.append(this.web_classify1_name);
		sb.append(",");
		sb.append(this.web_classify2);
		sb.append(",");
		sb.append(this.web_classify2_name);
		sb.append(",");
		sb.append(this.web_id);
		sb.append(",");
		sb.append(this.web_name);
		sb.append(",");
		sb.append(this.domain_level1);
		sb.append(",");
		sb.append(this.domain_level1_name);
		sb.append(",");
		// web_classify3
		sb.append(",");
		// web_classify3_name
		sb.append(",");
		sb.append(this.domain_level2);
		sb.append(",");
		sb.append(this.domain_level2_name);		
		return sb.toString();
	}
}
