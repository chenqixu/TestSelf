package bean;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetLogClientBean {
	private String RespCode = "-1";//0:成功;-1:失败
	private String RespDesc = "失败:没有查询";//code描述
	private String totalCount = "0";//总记录数
	private List DetailList = null;//详细列表
	private List GatherList = null;//汇总列表
	private List<List<String>> DealDetailList = null;//处理过的详细列表
	private List<String> DealGatherList = null;//处理过的汇总列表
	
	public List getDetailList() {
		return DetailList;
	}
	public void setDetailList(List detailList) {
		DetailList = detailList;
		//处理
		if(detailList!=null && detailList.size()>0){
			DealDetailList = new ArrayList<List<String>>();
			for(int jk=0;jk<detailList.size();jk++){
				String ngdata = detailList.get(jk).toString();//详细内容
				if(ngdata.endsWith(",")){//如果是以逗号结尾,说明 应用名称没有数据
					ngdata += " ";//补个空格
				}
				//System.out.println("jk:"+jk+"ngdata:"+ngdata);
				String ngdataArray[] = ngdata.split(",");//用逗号分割
				List<String> list1 = new ArrayList<String>();
				if(ngdataArray.length==11){//总共11个字段,如果没有就是错误数据,不能展现
					list1.add(ngdataArray[0]);//手机号码
					list1.add(ngdataArray[1]);//终端型号
					list1.add(ngdataArray[3]);//终端类型
					list1.add(ngdataArray[2]);//APN
					list1.add(ngdataArray[9]);//网络类型
					list1.add(ngdataArray[4]);//开始时间
					list1.add(ngdataArray[5]);//结束时间
					list1.add(ngdataArray[6]);//上行流量(K)
					list1.add(ngdataArray[7]);//下行流量(K)
					list1.add(ngdataArray[8]);//访问地址
					list1.add(ngdataArray[10]);//应用名称
					//System.out.println("jk:"+jk+"list1:"+list1);
					DealDetailList.add(list1);
				}
				//页面:手机号码,终端型号,终端类型,APN,网络类型,开始时间,结束时间,上行流量(K),下行流量(K),访问地址,应用名称
				//数据分割:号码,终端型号,apn,终端类型,开始时间,结束时间,上行流量,下行流量,地址,2g/3g,应用名称
				//hbase:<content>13616989826,A278t,CMNET,,2013-12-16 18:53:56.966,2013-12-16 18:53:57.325,111,139,,1,DNS</content>
				//greenplum:<content>13509323824,MI2012052,CMWAP,未知,2013-10-20 16:19:00,2013-10-20 16:19:00,659,374,http://mmsc.monternet.com,2G,彩信发送</content>
			}
		}
	}
	public List getGatherList() {
		return GatherList;
	}
	public void setGatherList(List gatherList) {
		GatherList = gatherList;
		//处理
		if(gatherList!=null && gatherList.size()==2){
			DealGatherList = new ArrayList<String>();
			String[] cmnet = gatherList.get(0).toString().split(",");
			String[] cmwap = gatherList.get(1).toString().split(",");			
			BigDecimal cmnetbyte = new BigDecimal(cmnet[1]);
			BigDecimal cmwapbyte = new BigDecimal(cmwap[1]);
			DealGatherList.add(cmnetbyte.add(cmwapbyte).setScale(0, BigDecimal.ROUND_FLOOR).toString());//总流量(K)
			DealGatherList.add(cmnetbyte.setScale(0, BigDecimal.ROUND_FLOOR).toString());//cmnet流量(K)gp有3位小数,需要取整;hbase不用处理
			DealGatherList.add(cmwapbyte.setScale(0, BigDecimal.ROUND_FLOOR).toString());//cmwap流量(K)gp有3位小数,需要取整;hbase不用处理
			BigDecimal cmnetdelaytime = new BigDecimal(cmnet[3]);
			BigDecimal cmwapdelaytime = new BigDecimal(cmwap[3]);
			DealGatherList.add(cmnetdelaytime.add(cmwapdelaytime).toString());//总时长(秒)3位小数
			DealGatherList.add(cmnetdelaytime.toString());//cmnet时长(秒)3位小数
			DealGatherList.add(cmwapdelaytime.toString());//cmwap时长(秒)3位小数			
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
	 * 根据ascii码判断是否是数字,是数字 true,不是数字 false
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
     * 将日期字符串按指定日期格式成新字符串
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

