package bean;

public class KeepNetLogOSSIpBean {
	private String sid;//ip.sid
	private String telnumber;//�ֻ�����
	private String ggsnip;//�û�NAT����IP��ַ
	private String userip;//�û�˽��IP��ַ
	private String usrsport;//NAT��Դ�˿�
	private String usrdip;//Ŀ��IP
	private String usrdport;//Ŀ�Ķ˿�
	private String starttime;//����ʱ��
	private String lasttime;//����ʱ��
	private String apn;//APN
	private String rattype;//2g/3g
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
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
}
