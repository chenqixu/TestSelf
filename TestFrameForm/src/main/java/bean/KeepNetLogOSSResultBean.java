package bean;

import java.util.List;
import java.util.Vector;

public class KeepNetLogOSSResultBean {
	private String telnumber = "";//手机号码
	private String ggsnip = "";//用户NAT后公网IP地址
	private String userip = "";//用户私网IP地址
	private String usrsport = "";//NAT后源端口
	private String usrdip = "";//目的IP
	private String usrdport = "";//目的端口
	private String url = "";//url
	private String querytime = "";//访问时间
	private String starttime = "";//上线时间
	private String lasttime = "";//下线时间
	private String apn = "";//APN
	private String rattype = "";//2g/3g
	
	public List joinIpAndHttp(KeepNetLogOSSIpBean ip, KeepNetLogOSSHttpBean http){
		KeepNetLogOSSResultBean bean = null;
		bean = new KeepNetLogOSSResultBean();
		bean.setTelnumber(ip.getTelnumber());
		bean.setGgsnip(ip.getGgsnip());//用户NAT后公网IP地址
		bean.setUserip(ip.getUserip());//用户私网IP地址
		bean.setUsrsport(ip.getUsrsport());//NAT后源端口
		bean.setUsrdip(ip.getUsrdip());//目的IP
		bean.setUsrdport(ip.getUsrdport());//目的端口
		bean.setUrl(http.getUrl());//url
		bean.setQuerytime(ip.getStarttime());//访问时间
		bean.setStarttime(http.getStarttime());//上线时间
		bean.setLasttime(http.getLasttime());//下线时间
		bean.setApn(ip.getApn());//APN
		bean.setRattype(ip.getRattype());//2g/3g
		return bean.changeList();
	}
	
	public List joinIp(KeepNetLogOSSIpBean ip){
		KeepNetLogOSSResultBean bean = null;
		bean = new KeepNetLogOSSResultBean();
		bean.setTelnumber(ip.getTelnumber());
		bean.setGgsnip(ip.getGgsnip());//用户NAT后公网IP地址
		bean.setUserip(ip.getUserip());//用户私网IP地址
		bean.setUsrsport(ip.getUsrsport());//NAT后源端口
		bean.setUsrdip(ip.getUsrdip());//目的IP
		bean.setUsrdport(ip.getUsrdport());//目的端口
		bean.setUrl("");//url
		bean.setQuerytime(ip.getStarttime());//访问时间
		bean.setStarttime(ip.getStarttime());//上线时间
		bean.setLasttime(ip.getLasttime());//下线时间
		bean.setApn(ip.getApn());//APN
		bean.setRattype(ip.getRattype());//2g/3g
		return bean.changeList();
	}
	
	public List changeList(){
		List<String> list = new Vector<String>();
		list.add(telnumber);//手机号码
		list.add(ggsnip);//用户NAT后公网IP地址
		list.add(userip);//用户私网IP地址
		list.add(usrsport);//NAT后源端口
		list.add(usrdip);//目的IP
		list.add(usrdport);//目的端口
		list.add(url);//url
		list.add(querytime);//访问时间
		list.add(starttime);//上线时间
		list.add(lasttime);//下线时间
		list.add(apn);//APN
		list.add(rattype);//2g/3g
		return list;
	}
	
	public KeepNetLogOSSResultBean setBeanByList(List list){
		KeepNetLogOSSResultBean bean = null;
		if(list!=null & list.size()>0){
			bean = new KeepNetLogOSSResultBean();
			bean.setTelnumber(list.get(0).toString());
			bean.setGgsnip(list.get(1).toString());//用户NAT后公网IP地址
			bean.setUserip(list.get(2).toString());//用户私网IP地址
			bean.setUsrsport(list.get(3).toString());//NAT后源端口
			bean.setUsrdip(list.get(4).toString());//目的IP
			bean.setUsrdport(list.get(5).toString());//目的端口
			bean.setUrl(list.get(6).toString());//url
			bean.setQuerytime(list.get(7).toString());//访问时间
			bean.setStarttime(list.get(8).toString());//上线时间
			bean.setLasttime(list.get(9).toString());//下线时间
			bean.setApn(list.get(10).toString());//APN
			bean.setRattype(list.get(11).toString());//2g/3g
		}
		return bean;
	}
	
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}
	public String getGgsnip() {
		return ggsnip;
	}
	public void setGgsnip(String ggsnip) {
		this.ggsnip = ggsnip;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
	public String getQuerytime() {
		return querytime;
	}
	public void setQuerytime(String querytime) {
		this.querytime = querytime;
	}
	public String getRattype() {
		return rattype;
	}
	public void setRattype(String rattype) {
		this.rattype = rattype;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
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
	public String getUserip() {
		return userip;
	}
	public void setUserip(String userip) {
		this.userip = userip;
	}
	public String getUsrdip() {
		return usrdip;
	}
	public void setUsrdip(String usrdip) {
		this.usrdip = usrdip;
	}
	public String getUsrdport() {
		return usrdport;
	}
	public void setUsrdport(String usrdport) {
		this.usrdport = usrdport;
	}
	public String getUsrsport() {
		return usrsport;
	}
	public void setUsrsport(String usrsport) {
		this.usrsport = usrsport;
	}

}
