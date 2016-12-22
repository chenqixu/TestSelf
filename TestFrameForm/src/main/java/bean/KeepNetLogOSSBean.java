package bean;

public class KeepNetLogOSSBean {

	private String telnumber;//手机号码
	private String ggsnip;//公网IP地址
	private String userip;//私网IP地址
	private String usrsport;//NAT后源端口
	private String usrdip;//目的IP
	private String usrdport;//目的端口
	private String url;//访问URL
	private String querytime_s;//访问时间
	private String starttime_s;//上线时间
	private String starttime_e;//下线时间
	private String apn;//apn
	private String rattype;//上网类型 2g/3g/wlan
	private String filename;//导出excel文件名
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
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
	public String getQuerytime_s() {
		return querytime_s;
	}
	public void setQuerytime_s(String querytime_s) {
		this.querytime_s = querytime_s;
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
	public String getRattype() {
		return rattype;
	}
	public void setRattype(String rattype) {
		this.rattype = rattype;
	}
	
}
