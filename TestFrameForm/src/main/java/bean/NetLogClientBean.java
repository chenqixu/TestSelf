package bean;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetLogClientBean {
	private String RespCode = "-1";//0:�ɹ�;-1:ʧ��
	private String RespDesc = "ʧ��:û�в�ѯ";//code����
	private String totalCount = "0";//�ܼ�¼��
	private List DetailList = null;//��ϸ�б�
	private List GatherList = null;//�����б�
	private List<List<String>> DealDetailList = null;//���������ϸ�б�
	private List<String> DealGatherList = null;//������Ļ����б�
	
	public List getDetailList() {
		return DetailList;
	}
	public void setDetailList(List detailList) {
		DetailList = detailList;
		//����
		if(detailList!=null && detailList.size()>0){
			DealDetailList = new ArrayList<List<String>>();
			for(int jk=0;jk<detailList.size();jk++){
				String ngdata = detailList.get(jk).toString();//��ϸ����
				if(ngdata.endsWith(",")){//������Զ��Ž�β,˵�� Ӧ������û������
					ngdata += " ";//�����ո�
				}
				//System.out.println("jk:"+jk+"ngdata:"+ngdata);
				String ngdataArray[] = ngdata.split(",");//�ö��ŷָ�
				List<String> list1 = new ArrayList<String>();
				if(ngdataArray.length==11){//�ܹ�11���ֶ�,���û�о��Ǵ�������,����չ��
					list1.add(ngdataArray[0]);//�ֻ�����
					list1.add(ngdataArray[1]);//�ն��ͺ�
					list1.add(ngdataArray[3]);//�ն�����
					list1.add(ngdataArray[2]);//APN
					list1.add(ngdataArray[9]);//��������
					list1.add(ngdataArray[4]);//��ʼʱ��
					list1.add(ngdataArray[5]);//����ʱ��
					list1.add(ngdataArray[6]);//��������(K)
					list1.add(ngdataArray[7]);//��������(K)
					list1.add(ngdataArray[8]);//���ʵ�ַ
					list1.add(ngdataArray[10]);//Ӧ������
					//System.out.println("jk:"+jk+"list1:"+list1);
					DealDetailList.add(list1);
				}
				//ҳ��:�ֻ�����,�ն��ͺ�,�ն�����,APN,��������,��ʼʱ��,����ʱ��,��������(K),��������(K),���ʵ�ַ,Ӧ������
				//���ݷָ�:����,�ն��ͺ�,apn,�ն�����,��ʼʱ��,����ʱ��,��������,��������,��ַ,2g/3g,Ӧ������
				//hbase:<content>13616989826,A278t,CMNET,,2013-12-16 18:53:56.966,2013-12-16 18:53:57.325,111,139,,1,DNS</content>
				//greenplum:<content>13509323824,MI2012052,CMWAP,δ֪,2013-10-20 16:19:00,2013-10-20 16:19:00,659,374,http://mmsc.monternet.com,2G,���ŷ���</content>
			}
		}
	}
	public List getGatherList() {
		return GatherList;
	}
	public void setGatherList(List gatherList) {
		GatherList = gatherList;
		//����
		if(gatherList!=null && gatherList.size()==2){
			DealGatherList = new ArrayList<String>();
			String[] cmnet = gatherList.get(0).toString().split(",");
			String[] cmwap = gatherList.get(1).toString().split(",");			
			BigDecimal cmnetbyte = new BigDecimal(cmnet[1]);
			BigDecimal cmwapbyte = new BigDecimal(cmwap[1]);
			DealGatherList.add(cmnetbyte.add(cmwapbyte).setScale(0, BigDecimal.ROUND_FLOOR).toString());//������(K)
			DealGatherList.add(cmnetbyte.setScale(0, BigDecimal.ROUND_FLOOR).toString());//cmnet����(K)gp��3λС��,��Ҫȡ��;hbase���ô���
			DealGatherList.add(cmwapbyte.setScale(0, BigDecimal.ROUND_FLOOR).toString());//cmwap����(K)gp��3λС��,��Ҫȡ��;hbase���ô���
			BigDecimal cmnetdelaytime = new BigDecimal(cmnet[3]);
			BigDecimal cmwapdelaytime = new BigDecimal(cmwap[3]);
			DealGatherList.add(cmnetdelaytime.add(cmwapdelaytime).toString());//��ʱ��(��)3λС��
			DealGatherList.add(cmnetdelaytime.toString());//cmnetʱ��(��)3λС��
			DealGatherList.add(cmwapdelaytime.toString());//cmwapʱ��(��)3λС��			
		}
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
	public List getDealDetailList() {
		return DealDetailList;
	}
	public List getDealGatherList() {
		return DealGatherList;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
		if(totalCount==null || totalCount.trim().length()==0 || !isNumeric(totalCount)){
			this.totalCount = "0";
		}
	}
	
	/**
	 * ����ascii���ж��Ƿ�������,������ true,�������� false
	 * */
	public boolean isNumeric(String strs) {
		String str = strs;
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57){
				return false;
			}
		}
		return true;
	}
	
    /**
     * �������ַ�����ָ�����ڸ�ʽ�����ַ���
     * */
    public String formatDateString(String date, String origFormat, String destFormat){
    	SimpleDateFormat sf1 = new SimpleDateFormat(origFormat);
    	Date d = null;
		try {
			d = sf1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	SimpleDateFormat sf2 = new SimpleDateFormat(destFormat);
    	return sf2.format(d);
    }
}

