package bean;

import java.util.List;
import java.util.Vector;

public class KeepNetLogOSSWlanBean {
	private String starttime_s = "";//
	private String starttime_e = "";//
	private String telnumber = "";//
	private String mac = "";//
	private String ac_home_county = "";//
	private String user_home_county = "";//
	private String acip = "";//
	private String nasid = "";//
	private String up = "";//
	private String down = "";//
	private String user_gw_ip = "";//
	
	public List changeList(){
		List<String> list = new Vector<String>();
		list.add(starttime_s);
		list.add(starttime_e);
		list.add(telnumber);
		list.add(mac);
		list.add(ac_home_county);
		list.add(user_home_county);
		list.add(acip);
		list.add(nasid);
		list.add(up);
		list.add(down);
		list.add(user_gw_ip);
		return list;
	}
	
	public String getAc_home_county() {
		return ac_home_county;
	}
	public void setAc_home_county(String ac_home_county) {
		this.ac_home_county = ac_home_county;
	}
	public String getAcip() {
		return acip;
	}
	public void setAcip(String acip) {
		this.acip = acip;
	}
	public String getDown() {
		return down;
	}
	public void setDown(String down) {
		this.down = down;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getNasid() {
		return nasid;
	}
	public void setNasid(String nasid) {
		this.nasid = nasid;
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
	public String getUp() {
		return up;
	}
	public void setUp(String up) {
		this.up = up;
	}
	public String getUser_gw_ip() {
		return user_gw_ip;
	}
	public void setUser_gw_ip(String user_gw_ip) {
		this.user_gw_ip = user_gw_ip;
	}
	public String getUser_home_county() {
		return user_home_county;
	}
	public void setUser_home_county(String user_home_county) {
		this.user_home_county = user_home_county;
	}
}
