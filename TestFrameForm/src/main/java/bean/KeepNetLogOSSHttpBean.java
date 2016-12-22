package bean;

public class KeepNetLogOSSHttpBean {
	private String ipsid;//http.ipsid
	private String starttime;//上线时间
	private String lasttime;//下线时间
	private String url;//url
	public String getIpsid() {
		return ipsid;
	}
	public void setIpsid(String ipsid) {
		this.ipsid = ipsid;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
