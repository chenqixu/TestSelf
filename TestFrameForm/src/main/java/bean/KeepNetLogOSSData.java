package bean;

import java.util.List;

public class KeepNetLogOSSData {
	private String RespCode = "-1";//0:�ɹ�;-1:ʧ��
	private String RespDesc = "ʧ��:û�в�ѯ";//code����
	private String totalCount = "0";//�ܼ�¼��
	private List DetailList = null;//��ϸ�б�
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
