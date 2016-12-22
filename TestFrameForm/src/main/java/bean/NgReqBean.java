package bean;

public class NgReqBean {
	private Long telnumber ;// 手机号码

	private String starttime_s;// 时间段－开始时间

	private String starttime_e;// 时间段－结束时间

	private String apn = "";// 流量类型 
	
	private String servicename="";//服务代码名称
	
	private String reqSource="";// 请求源  业务组按servicename进行统计 固定传值 “bus”  其它 按apn统计 可以不用传值
	
	private String charging_id="";// 计费id

	public Long getTelnumber() {
		return telnumber;
	}
	public void setTelnumber(Long telnumber) {
		this.telnumber = telnumber;
	}

	public String getStarttime_s() {
		return starttime_s;
	}

	public void setStarttime_s(String starttime_s) {
		this.starttime_s = starttime_s;
	}

	public String getStarttime_e() {
		return starttime_e;
	}

	public void setStarttime_e(String starttime_e) {
		this.starttime_e = starttime_e;
	}

	public String getApn() {
		return apn;
	}

	public void setApn(String apn) {
		this.apn = apn;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getReqSource() {
		return reqSource;
	}

	public void setReqSource(String reqSource) {
		this.reqSource = reqSource;
	}
	public String getCharging_id() {
		return charging_id;
	}
	public void setCharging_id(String charging_id) {
		this.charging_id = charging_id;
	}
}
