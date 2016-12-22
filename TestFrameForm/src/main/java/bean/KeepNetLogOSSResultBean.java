package bean;

import java.util.List;
import java.util.Vector;

public class KeepNetLogOSSResultBean {
	private String telnumber = "";//�ֻ�����
	private String ggsnip = "";//�û�NAT����IP��ַ
	private String userip = "";//�û�˽��IP��ַ
	private String usrsport = "";//NAT��Դ�˿�
	private String usrdip = "";//Ŀ��IP
	private String usrdport = "";//Ŀ�Ķ˿�
	private String url = "";//url
	private String querytime = "";//����ʱ��
	private String starttime = "";//����ʱ��
	private String lasttime = "";//����ʱ��
	private String apn = "";//APN
	private String rattype = "";//2g/3g
	
	public List joinIpAndHttp(KeepNetLogOSSIpBean ip, KeepNetLogOSSHttpBean http){
		KeepNetLogOSSResultBean bean = null;
		bean = new KeepNetLogOSSResultBean();
		bean.setTelnumber(ip.getTelnumber());
		bean.setGgsnip(ip.getGgsnip());//�û�NAT����IP��ַ
		bean.setUserip(ip.getUserip());//�û�˽��IP��ַ
		bean.setUsrsport(ip.getUsrsport());//NAT��Դ�˿�
		bean.setUsrdip(ip.getUsrdip());//Ŀ��IP
		bean.setUsrdport(ip.getUsrdport());//Ŀ�Ķ˿�
		bean.setUrl(http.getUrl());//url
		bean.setQuerytime(ip.getStarttime());//����ʱ��
		bean.setStarttime(http.getStarttime());//����ʱ��
		bean.setLasttime(http.getLasttime());//����ʱ��
		bean.setApn(ip.getApn());//APN
		bean.setRattype(ip.getRattype());//2g/3g
		return bean.changeList();
	}
	
	public List joinIp(KeepNetLogOSSIpBean ip){
		KeepNetLogOSSResultBean bean = null;
		bean = new KeepNetLogOSSResultBean();
		bean.setTelnumber(ip.getTelnumber());
		bean.setGgsnip(ip.getGgsnip());//�û�NAT����IP��ַ
		bean.setUserip(ip.getUserip());//�û�˽��IP��ַ
		bean.setUsrsport(ip.getUsrsport());//NAT��Դ�˿�
		bean.setUsrdip(ip.getUsrdip());//Ŀ��IP
		bean.setUsrdport(ip.getUsrdport());//Ŀ�Ķ˿�
		bean.setUrl("");//url
		bean.setQuerytime(ip.getStarttime());//����ʱ��
		bean.setStarttime(ip.getStarttime());//����ʱ��
		bean.setLasttime(ip.getLasttime());//����ʱ��
		bean.setApn(ip.getApn());//APN
		bean.setRattype(ip.getRattype());//2g/3g
		return bean.changeList();
	}
	
	public List changeList(){
		List<String> list = new Vector<String>();
		list.add(telnumber);//�ֻ�����
		list.add(ggsnip);//�û�NAT����IP��ַ
		list.add(userip);//�û�˽��IP��ַ
		list.add(usrsport);//NAT��Դ�˿�
		list.add(usrdip);//Ŀ��IP
		list.add(usrdport);//Ŀ�Ķ˿�
		list.add(url);//url
		list.add(querytime);//����ʱ��
		list.add(starttime);//����ʱ��
		list.add(lasttime);//����ʱ��
		list.add(apn);//APN
		list.add(rattype);//2g/3g
		return list;
	}
	
	public KeepNetLogOSSResultBean setBeanByList(List list){
		KeepNetLogOSSResultBean bean = null;
		if(list!=null & list.size()>0){
			bean = new KeepNetLogOSSResultBean();
			bean.setTelnumber(list.get(0).toString());
			bean.setGgsnip(list.get(1).toString());//�û�NAT����IP��ַ
			bean.setUserip(list.get(2).toString());//�û�˽��IP��ַ
			bean.setUsrsport(list.get(3).toString());//NAT��Դ�˿�
			bean.setUsrdip(list.get(4).toString());//Ŀ��IP
			bean.setUsrdport(list.get(5).toString());//Ŀ�Ķ˿�
			bean.setUrl(list.get(6).toString());//url
			bean.setQuerytime(list.get(7).toString());//����ʱ��
			bean.setStarttime(list.get(8).toString());//����ʱ��
			bean.setLasttime(list.get(9).toString());//����ʱ��
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
