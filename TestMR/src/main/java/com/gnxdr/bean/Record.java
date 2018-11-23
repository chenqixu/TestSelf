package com.gnxdr.bean;

import com.gnxdr.constant.Constants;

public class Record {
	
	//标识记录归属业务类型
	private String flag;
	//输出字段
	private String ip_id;
	private String msisdn;
	private String terminal_model;
	private String apn;
	private String start_time;
	private String end_time;
	private String up_data;
	private String down_data;
	private String rat;
	private String app_class;
	private String lac;
	private String cid;
	private String charge_id;
	private String server_ip;
	private String imsi;
	private String l4_protocol;
	private String app_class_top;
	private String server_prot;
	private String duration;
	private String ul_tcp_disordered_packets;
	private String dl_tcp_disordered_packets;
	private String ul_tcp_retransmission_packets;
	private String dl_tcp_retransmission_packets;
	private String ul_ip_frag_packets;
	private String dl_ip_frag_packets;
	
	
	//http独有属性
	private String host;
	private String uri;
	private String user_agent;
	
	//rtsp独有属性
	private String rtp_server_ip;
	//email独有属性-收件人
	private String user_name;
	
	//需要imei关联终端配置表得出终端类型terminaltype
	private String imei;
	private String terminaltype;
	
	//计算流量均摊需要使用的字段
	private String up_ip_pkgs;
	private String down_ip_pkgs;
	
	//汇总计算使用的字段，精确到小时
	private String period_of_time;
	//汇总计算使用的字段，精确到日
	private String sum_date;
	
	
	private String web_classify;
	private String web_name;
	
	public Record() {
		super();
	}
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	//根据各个业务决定返回值
	public String getUrl() {
		
		if(Constants.DNS_FILE_FLAG.equals(this.getFlag().toString())){
			if(null!=server_ip&&!"".equals(server_ip)){
				return server_ip+"(DNS)";
			}else{
				return server_ip;
			}
		}else if(Constants.HTTP_FILE_FLAG.equals(this.getFlag().toString())){
			if(null!=uri&&!"".equals(uri)){
				return uri;
			}else{
				return server_ip;
			}
		}else if(Constants.RTSP_FILE_FLAG.equals(this.getFlag().toString())){
			if(null!=rtp_server_ip&&!"".equals(rtp_server_ip)){
				return rtp_server_ip;
			}else{
				return server_ip;
			}
		}else if(Constants.MMS_FILE_FLAG.equals(this.getFlag().toString())){
			return uri;
		}else if(Constants.EMAIL_FILE_FLAG.equals(this.getFlag().toString())){
			return user_name;
		}else{
			return server_ip;
		}
	}

	public String getServer_ip() {
		return server_ip;
	}

	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getRtp_server_ip() {
		return rtp_server_ip;
	}

	public void setRtp_server_ip(String rtp_server_ip) {
		this.rtp_server_ip = rtp_server_ip;
	}

	public String getIp_id() {
		return ip_id;
	}
	public void setIp_id(String ip_id) {
		this.ip_id = ip_id;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getTerminal_model() {
		return terminal_model;
	}
	public void setTerminal_model(String terminal_model) {
		this.terminal_model = terminal_model;
	}
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getUp_data() {
		return up_data;
	}
	public void setUp_data(String up_data) {
		this.up_data = up_data;
	}
	public String getDown_data() {
		return down_data;
	}
	public void setDown_data(String down_data) {
		this.down_data = down_data;
	}
	public String getRat() {
		return "1".equals(rat)?"1":"0";
	}
	public void setRat(String rat) {
		this.rat = rat;
	}
	public String getApp_class() {
		return app_class;
	}
	public void setApp_class(String app_class) {
		this.app_class = app_class;
	}
	public String getLac() {
		return lac;
	}
	public void setLac(String lac) {
		this.lac = lac;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCharge_id() {
		return charge_id;
	}
	public void setCharge_id(String charge_id) {
		this.charge_id = charge_id;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getTerminaltype() {
		return terminaltype;
	}
	public void setTerminaltype(String terminaltype) {
		this.terminaltype = terminaltype;
	}
	public String getUp_ip_pkgs() {
		return up_ip_pkgs;
	}
	public void setUp_ip_pkgs(String up_ip_pkgs) {
		this.up_ip_pkgs = up_ip_pkgs;
	}
	public String getDown_ip_pkgs() {
		return down_ip_pkgs;
	}
	public void setDown_ip_pkgs(String down_ip_pkgs) {
		this.down_ip_pkgs = down_ip_pkgs;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getL4_protocol() {
		return l4_protocol;
	}

	public void setL4_protocol(String l4_protocol) {
		this.l4_protocol = l4_protocol;
	}

	public String getApp_class_top() {
		return app_class_top;
	}

	public void setApp_class_top(String app_class_top) {
		this.app_class_top = app_class_top;
	}

	public String getServer_prot() {
		return server_prot;
	}

	public void setServer_prot(String server_prot) {
		this.server_prot = server_prot;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getUl_tcp_disordered_packets() {
		return ul_tcp_disordered_packets;
	}

	public void setUl_tcp_disordered_packets(String ul_tcp_disordered_packets) {
		this.ul_tcp_disordered_packets = ul_tcp_disordered_packets;
	}

	public String getDl_tcp_disordered_packets() {
		return dl_tcp_disordered_packets;
	}

	public void setDl_tcp_disordered_packets(String dl_tcp_disordered_packets) {
		this.dl_tcp_disordered_packets = dl_tcp_disordered_packets;
	}

	public String getUl_tcp_retransmission_packets() {
		return ul_tcp_retransmission_packets;
	}

	public void setUl_tcp_retransmission_packets(
			String ul_tcp_retransmission_packets) {
		this.ul_tcp_retransmission_packets = ul_tcp_retransmission_packets;
	}

	public String getDl_tcp_retransmission_packets() {
		return dl_tcp_retransmission_packets;
	}

	public void setDl_tcp_retransmission_packets(
			String dl_tcp_retransmission_packets) {
		this.dl_tcp_retransmission_packets = dl_tcp_retransmission_packets;
	}

	public String getUl_ip_frag_packets() {
		return ul_ip_frag_packets;
	}

	public void setUl_ip_frag_packets(String ul_ip_frag_packets) {
		this.ul_ip_frag_packets = ul_ip_frag_packets;
	}

	public String getDl_ip_frag_packets() {
		return dl_ip_frag_packets;
	}

	public void setDl_ip_frag_packets(String dl_ip_frag_packets) {
		this.dl_ip_frag_packets = dl_ip_frag_packets;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPeriod_of_time() {
		return period_of_time;
	}

	public void setPeriod_of_time(String period_of_time) {
		this.period_of_time = period_of_time;
	}

	public String getSum_date() {
		return sum_date;
	}

	public void setSum_date(String sum_date) {
		this.sum_date = sum_date;
	}
	
	//华为应用识别
	public String  getWeb_classify(){
		return web_classify;
	}
	public void setWeb_classify(String web_classify) {
		this.web_classify = web_classify;	
	}
	public String  getWeb_name(){
		return web_name;
	}
	public void setWeb_name(String web_name) {
		this.web_name = web_name;	
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("flag:").append( null == this.flag?"":this.flag).append(Constants.COMMA_SEPARATOR)
		.append("ip_id:").append( null == this.ip_id?"":this.ip_id).append(Constants.COMMA_SEPARATOR)
		.append("msisdn:").append( null == this.msisdn?"":this.msisdn).append(Constants.COMMA_SEPARATOR)
		.append("terminal_model:").append( null == this.terminal_model?"":this.terminal_model).append(Constants.COMMA_SEPARATOR)
		.append("apn:").append( null == this.apn?"":this.apn).append(Constants.COMMA_SEPARATOR)
		.append("start_time:").append( null == this.start_time?"":this.start_time).append(Constants.COMMA_SEPARATOR)
		.append("end_time:").append( null == this.end_time?"":this.end_time).append(Constants.COMMA_SEPARATOR)
		.append("up_data:").append( null == this.up_data?"":this.up_data).append(Constants.COMMA_SEPARATOR)
		.append("down_data:").append( null == this.down_data?"":this.down_data).append(Constants.COMMA_SEPARATOR)
		.append("rat:").append( null == this.rat?"":this.rat).append(Constants.COMMA_SEPARATOR)
		.append("app_class:").append( null == this.app_class?"":this.app_class).append(Constants.COMMA_SEPARATOR)
		.append("lac:").append( null == this.lac?"":this.lac).append(Constants.COMMA_SEPARATOR)
		.append("cid:").append( null == this.cid?"":this.cid).append(Constants.COMMA_SEPARATOR)
		.append("charge_id:").append( null == this.charge_id?"":this.charge_id).append(Constants.COMMA_SEPARATOR)
		.append("server_ip:").append( null == this.server_ip?"":this.server_ip).append(Constants.COMMA_SEPARATOR)
		.append("imsi:").append( null == this.imsi?"":this.imsi).append(Constants.COMMA_SEPARATOR)
		.append("l4_protocol:").append( null == this.l4_protocol?"":this.l4_protocol).append(Constants.COMMA_SEPARATOR)
		.append("app_class_top:").append( null == this.app_class_top?"":this.app_class_top).append(Constants.COMMA_SEPARATOR)
		.append("server_prot:").append( null == this.server_prot?"":this.server_prot).append(Constants.COMMA_SEPARATOR)
		.append("duration:").append( null == this.duration?"":this.duration).append(Constants.COMMA_SEPARATOR)
		.append("ul_tcp_disordered_packets:").append( null == this.ul_tcp_disordered_packets?"":this.ul_tcp_disordered_packets).append(Constants.COMMA_SEPARATOR)
		.append("dl_tcp_disordered_packets:").append( null == this.dl_tcp_disordered_packets?"":this.dl_tcp_disordered_packets).append(Constants.COMMA_SEPARATOR)
		.append("ul_tcp_retransmission_packets:").append( null == this.ul_tcp_retransmission_packets?"":this.ul_tcp_retransmission_packets).append(Constants.COMMA_SEPARATOR)
		.append("dl_tcp_retransmission_packets:").append( null == this.dl_tcp_retransmission_packets?"":this.dl_tcp_retransmission_packets).append(Constants.COMMA_SEPARATOR)
		.append("ul_ip_frag_packets:").append( null == this.ul_ip_frag_packets?"":this.ul_ip_frag_packets).append(Constants.COMMA_SEPARATOR)
		.append("dl_ip_frag_packets:").append( null == this.dl_ip_frag_packets?"":this.dl_ip_frag_packets).append(Constants.COMMA_SEPARATOR)
		.append("host:").append( null == this.host?"":this.host).append(Constants.COMMA_SEPARATOR)
		.append("uri:").append( null == this.uri?"":this.uri).append(Constants.COMMA_SEPARATOR)
		.append("user_agent:").append( null == this.user_agent?"":this.user_agent).append(Constants.COMMA_SEPARATOR)
		.append("rtp_server_ip:").append( null == this.rtp_server_ip?"":this.rtp_server_ip).append(Constants.COMMA_SEPARATOR)
		.append("user_name:").append( null == this.user_name?"":this.user_name).append(Constants.COMMA_SEPARATOR)
		.append("imei:").append( null == this.imei?"":this.imei).append(Constants.COMMA_SEPARATOR)
		.append("terminaltype:").append( null == this.terminaltype?"":this.terminaltype).append(Constants.COMMA_SEPARATOR)
		.append("up_ip_pkgs:").append( null == this.up_ip_pkgs?"":this.up_ip_pkgs).append(Constants.COMMA_SEPARATOR)
		.append("down_ip_pkgs:").append( null == this.down_ip_pkgs?"":this.down_ip_pkgs);

		return sb.toString();
	}

	public String getStatisticsCol() {
		StringBuffer sb = new StringBuffer();
		sb.append( null == this.msisdn?"":this.getMsisdn()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.imsi?"":this.getImsi()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.imei?"":this.getImei()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.apn?"":this.getApn()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.l4_protocol?"":this.getL4_protocol()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.rat?"":this.getRat()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.lac?"":this.getLac()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.cid?"":this.getCid()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.app_class_top?"":this.getApp_class_top()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.app_class?"":this.getApp_class()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.web_classify?"":this.getWeb_classify()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.web_name?"":this.getWeb_name()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.host?"":this.getHost()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.server_ip?"":this.getServer_ip()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.server_prot?"":this.getServer_prot()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.user_agent?"":this.getUser_agent()).append(Constants.COMMA_SEPARATOR)
		.append( this.getUrl()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.period_of_time?"":this.getPeriod_of_time()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.ip_id?"":this.getIp_id()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.duration?"":this.getDuration()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.start_time?"":this.getStart_time()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.end_time?"":this.getEnd_time()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.up_data?"":this.getUp_data()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.down_data?"":this.getDown_data()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.up_ip_pkgs?"":this.getUp_ip_pkgs()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.down_ip_pkgs?"":this.getDown_ip_pkgs()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.ul_tcp_disordered_packets?"":this.getUl_tcp_disordered_packets()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.dl_tcp_disordered_packets?"":this.getDl_tcp_disordered_packets()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.ul_tcp_retransmission_packets?"":this.getUl_tcp_retransmission_packets()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.dl_tcp_retransmission_packets?"":this.getDl_tcp_retransmission_packets()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.ul_ip_frag_packets?"":this.getUl_ip_frag_packets()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.dl_ip_frag_packets?"":this.getDl_ip_frag_packets()).append(Constants.COMMA_SEPARATOR)
		.append( null == this.sum_date?"":this.getSum_date());

		return sb.toString();
	}
	
	public String getStatisticsColTest() {
		StringBuffer sb = new StringBuffer();
		sb.append("msisdn:").append( null == this.msisdn?"":this.getMsisdn()).append(Constants.COMMA_SEPARATOR)
		.append("imsi:").append( null == this.imsi?"":this.getImsi()).append(Constants.COMMA_SEPARATOR)
		.append("imei:").append( null == this.imei?"":this.getImei()).append(Constants.COMMA_SEPARATOR)
		.append("apn:").append( null == this.apn?"":this.getApn()).append(Constants.COMMA_SEPARATOR)
		.append("l4_protocol:").append( null == this.l4_protocol?"":this.getL4_protocol()).append(Constants.COMMA_SEPARATOR)
		.append("rat:").append( null == this.rat?"":this.getRat()).append(Constants.COMMA_SEPARATOR)
		.append("lac:").append( null == this.lac?"":this.getLac()).append(Constants.COMMA_SEPARATOR)
		.append("cid:").append( null == this.cid?"":this.getCid()).append(Constants.COMMA_SEPARATOR)
		.append("app_class_top:").append( null == this.app_class_top?"":this.getApp_class_top()).append(Constants.COMMA_SEPARATOR)
		.append("app_class:").append( null == this.app_class?"":this.getApp_class()).append(Constants.COMMA_SEPARATOR)
		.append("web_classify:").append("").append(Constants.COMMA_SEPARATOR)
		.append("web_name:").append("").append(Constants.COMMA_SEPARATOR)
		.append("host:").append( null == this.host?"":this.getHost()).append(Constants.COMMA_SEPARATOR)
		.append("server_ip:").append( null == this.server_ip?"":this.getServer_ip()).append(Constants.COMMA_SEPARATOR)
		.append("server_prot:").append( null == this.server_prot?"":this.getServer_prot()).append(Constants.COMMA_SEPARATOR)
		.append("user_agent:").append( null == this.user_agent?"":this.getUser_agent()).append(Constants.COMMA_SEPARATOR)
		.append("url:").append(this.getUrl()).append(Constants.COMMA_SEPARATOR)
		.append("period_of_time:").append(this.getPeriod_of_time()).append(Constants.COMMA_SEPARATOR)
		.append("ip_id:").append( null == this.ip_id?"":this.getIp_id()).append(Constants.COMMA_SEPARATOR)
		.append("duration:").append( null == this.duration?"":this.getDuration()).append(Constants.COMMA_SEPARATOR)
		.append("up_data:").append( null == this.up_data?"":this.getUp_data()).append(Constants.COMMA_SEPARATOR)
		.append("down_data:").append( null == this.down_data?"":this.getDown_data()).append(Constants.COMMA_SEPARATOR)
		.append("up_ip_pkgs:").append( null == this.up_ip_pkgs?"":this.getUp_ip_pkgs()).append(Constants.COMMA_SEPARATOR)
		.append("down_ip_pkgs:").append( null == this.down_ip_pkgs?"":this.getDown_ip_pkgs()).append(Constants.COMMA_SEPARATOR)
		.append("start_time:").append( null == this.start_time?"":this.getStart_time()).append(Constants.COMMA_SEPARATOR)
		.append("end_time:").append( null == this.end_time?"":this.getEnd_time()).append(Constants.COMMA_SEPARATOR)
		.append("ul_tcp_disordered_packets:").append( null == this.ul_tcp_disordered_packets?"":this.getUl_tcp_disordered_packets()).append(Constants.COMMA_SEPARATOR)
		.append("dl_tcp_disordered_packets:").append( null == this.dl_tcp_disordered_packets?"":this.getDl_tcp_disordered_packets()).append(Constants.COMMA_SEPARATOR)
		.append("ul_tcp_retransmission_packets:").append( null == this.ul_tcp_retransmission_packets?"":this.getUl_tcp_retransmission_packets()).append(Constants.COMMA_SEPARATOR)
		.append("dl_tcp_retransmission_packets:").append( null == this.dl_tcp_retransmission_packets?"":this.getDl_tcp_retransmission_packets()).append(Constants.COMMA_SEPARATOR)
		.append("ul_ip_frag_packets:").append( null == this.ul_ip_frag_packets?"":this.getUl_ip_frag_packets()).append(Constants.COMMA_SEPARATOR)
		.append("dl_ip_frag_packets:").append( null == this.dl_ip_frag_packets?"":this.getDl_ip_frag_packets()).append(Constants.COMMA_SEPARATOR)
		.append("sum_date:").append(this.getSum_date());

		return sb.toString();
	}
}
