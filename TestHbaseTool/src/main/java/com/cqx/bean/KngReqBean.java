package com.cqx.bean;

public class KngReqBean {
	private String telnumber ;// �ֻ�����
	private String starttime_s;// ʱ��Σ���ʼʱ��
	private String starttime_e;// ʱ��Σ�����ʱ��
	private String ggsip = "";// net����ip	
	private String ggsport="";// net�����˿�	
	private String url="";// ����rul
	public String getGgsip() {
		return ggsip;
	}
	public void setGgsip(String ggsip) {
		this.ggsip = ggsip;
	}
	public String getGgsport() {
		return ggsport;
	}
	public void setGgsport(String ggsport) {
		this.ggsport = ggsport;
	}
	public String getStarttime_e() {
		return starttime_e;
	}
	public void setStarttime_e(String starttime_e) {
		this.starttime_e = starttime_e;
	}
	public String getStarttime_s() {
		return starttime_s;
	}
	public void setStarttime_s(String starttime_s) {
		this.starttime_s = starttime_s;
	}
	public String getTelnumber() {
		return telnumber;
	}
	public void setTelnumber(String telnumber) {
		this.telnumber = telnumber;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
