package bean;

public class NgReqBean {
	private Long telnumber ;// �ֻ�����

	private String starttime_s;// ʱ��Σ���ʼʱ��

	private String starttime_e;// ʱ��Σ�����ʱ��

	private String apn = "";// �������� 
	
	private String servicename="";//�����������
	
	private String reqSource="";// ����Դ  ҵ���鰴servicename����ͳ�� �̶���ֵ ��bus��  ���� ��apnͳ�� ���Բ��ô�ֵ
	
	private String charging_id="";// �Ʒ�id

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
