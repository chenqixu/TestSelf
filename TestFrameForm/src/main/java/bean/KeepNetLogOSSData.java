package bean;

import java.util.List;

public class KeepNetLogOSSData {
	private String RespCode = "-1";//0:成功;-1:失败
	private String RespDesc = "失败:没有查询";//code描述
	private String totalCount = "0";//总记录数
	private List DetailList = null;//详细列表
	public List getDetailList() {
		return DetailList;
	}
	public void setDetailList(List detailList) {
		DetailList = detailList;
	}
	public String getRespCode() {
		return RespCode;
	}
	public void setRespCode(String respCode) {
		RespCode = respCode;
	}
	public String getRespDesc() {
		return RespDesc;
	}
	public void setRespDesc(String respDesc) {
		RespDesc = respDesc;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

}
