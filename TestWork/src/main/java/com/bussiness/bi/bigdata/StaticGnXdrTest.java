package com.bussiness.bi.bigdata;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class StaticGnXdrTest {

	public static final String SPLIT_FIELD_STR = ",";
	public static final String QUOTES = "'";
	public static final String QUOTES_COMMA = "',";
	public static final char SPLIT_FIELD_CHAR = ',';	
	public static final String MAP_KEY_SPLIT = ",";
	public static final String HTTP_STR = "http://";
	public static final String SPLIT_URL_STR = "/";
	public static final String COMMA_SEPARATOR = ",";
	public static final String IP_FILE_FLAG = "IP";
	public static final String DNS_FILE_FLAG = "DNS";
	public static final String EMAIL_FILE_FLAG = "EMAIL";
	public static final String FTP_FILE_FLAG = "FTP";
	public static final String HTTP_FILE_FLAG = "HTTP";
	public static final String IM_FILE_FLAG = "IM";
	public static final String MMS_FILE_FLAG = "MMS";
	public static final String P2P_FILE_FLAG = "P2P";
	public static final String RTSP_FILE_FLAG = "RTSP";
	public static final String VOIP_FILE_FLAG = "VOIP";
	public static final String JOIN_KEY = "IP_ID";
	
	private static StringBuffer tempSB = new StringBuffer();
	private static Map<String, String> map = new HashMap<String, String>();
	private static StringBuilder sb = new StringBuilder();
	
	/**
	 * 
	 * @description: 生成delayTime
	 * @author:xixg
	 * @date:2014-01-03
	 * @param lineValueArray
	 *            Map输出的value值
	 * @return String 返回delayTime
	 */
	public static long createDelayTime(List<String> lineValueList) {
		long delayTimeVal = 0;
		try {
			String delayTime = lineValueList.get(1).trim();
			String startTime = lineValueList.get(2).trim();
			String lastTime = lineValueList.get(3).trim();
			System.out.println("[delayTime]"+delayTime);
			System.out.println("[startTime]"+startTime);
			System.out.println("[lastTime]"+lastTime);
			// 如果lastTime为空或者startTime为空，则delayTime为源数据的delayTime
			if ("".equals(lastTime) || "".equals(startTime)) {
				if (!"".equals(delayTime))
					delayTimeVal = Long.parseLong(delayTime);
			} else {// 否则delayTime值为lastTime-startTime
					// 结束时间设置时间实例
					// calendarLast.setTime(sdf.parse(lastTime));
				// 获取结束时间毫秒级别的long值
				// long lastTimeLong = calendarLast.getTimeInMillis();
				long lastTimeLong = Long.parseLong(lastTime);
				// 开始时间设置时间实例
				// calendarStart.setTime(sdf.parse(startTime));
				// 获取开始时间毫秒级别的long值
				// long startTimeLong = calendarStart.getTimeInMillis();
				long startTimeLong = Long.parseLong(startTime);
				// 结束时间减去开始时间为delayTime
				delayTimeVal = lastTimeLong - startTimeLong;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delayTimeVal;
	}

	/**
	 * 通过指定的切割符号来切割字符串，最后将每个子串存在list中
	 * 
	 * @param valueString
	 * @return 存放有每个子串的list
	 */
	public static List<String> splitStringByComma(String valueString) {
		List<String> list = new ArrayList<String>();
		tempSB.setLength(0);
		try {
			StringTokenizer st = new StringTokenizer(valueString,
					SPLIT_FIELD_STR);
			while (st.hasMoreTokens()) {
				// 先判断当前token之后知否是,如果是逗号，则加入list中
				if (valueString.charAt(tempSB.length()) == SPLIT_FIELD_CHAR) {
					list.add("");
					tempSB.append(SPLIT_FIELD_STR);
					continue;
				}
				String temp = st.nextToken();
				if (temp.startsWith(QUOTES)) {
					// new一个新对象防止内存溢出
					temp = new String(temp.substring(1));
					// StringBuffer sb = new StringBuffer();

					int index = temp.indexOf(QUOTES);
					if (-1 == index) {
						// 说明该字段中含有,从源字符串中查找第n个逗号之后
						int end = valueString.indexOf(QUOTES,
								tempSB.length() + 1);
						// sb.append(new
						// String(valueString.substring(tempSB.length(), end)));
						// 去掉前后的单引号
						list.add(new String(valueString.substring(
								tempSB.length() + 1, end)));
						tempSB.append(new String(valueString.substring(
								tempSB.length(), end + 1)));
						// 如果这个字段是最后一个字段，则结束
						if (end + 2 > valueString.length()) {
							break;
						}
						tempSB.append(SPLIT_FIELD_STR);
						st = new StringTokenizer(new String(
								valueString.substring(end + 2)),
								SPLIT_FIELD_STR);
						continue;
						// 查找下一个单引号的位置，如果下个字符串中也不存在，则一直找到存在的字符串位置
					} else {
						// index不为-1则说明，该字符串中包含下一个单引号，即该单引号括起来的字符串中没有逗号
						list.add(new String(
								temp.substring(0, temp.length() - 1)));
						tempSB.append(temp).append(QUOTES_COMMA);
						continue;
					}
				}
				list.add(temp);
				tempSB.append(temp).append(SPLIT_FIELD_STR);
			}
			// 如果以逗号结尾，则判断逗号的数量，追加list的数量
			if (valueString.endsWith(SPLIT_FIELD_STR)) {
				int commaIndex = valueString
						.lastIndexOf(SPLIT_FIELD_STR);
				while (commaIndex > 1) {
					if (valueString.charAt(commaIndex--) == SPLIT_FIELD_CHAR) {
						list.add("");
					} else {
						break;
					}
				}
			}
		} catch (Exception ex) {
			return null;
		}
		return list;
	}

	/**
	 * 
	 * @description: 截取URL中前两个/的字符 例如：http://www.sina.com/sport/nba 截取后为：
	 *               http://www.sina.com/sport/ 截取后为： http://www.sina.com/
	 * @author:xixg
	 * @date:2013-9-26
	 * @param url
	 *            输入时源数据的Url
	 * @return String 返回截取后的字符串
	 */
	public static String subUrlFrontTwo(String url) {
		// 如果URL为空，则直接返回空串
		if (null == url || "".equals(url))
			return "";
		String returnUrl = "";
		try {
			StringBuffer sb = new StringBuffer();
			int httpLoc = url.indexOf(HTTP_STR);
			System.currentTimeMillis();
			// 如果Url中包括http://
			if (httpLoc > -1) {
				// 截取url,去掉http://
				returnUrl = url.substring(7);
				// 以/分隔截取后的url字符串
				String[] urlArray = returnUrl.split(SPLIT_URL_STR);
				if (null != urlArray && urlArray.length > 2) {
					// 字符串加http://
					sb.append(HTTP_STR);
					sb.append(urlArray[0]);
					// 字符串加/
					sb.append(SPLIT_URL_STR);
					sb.append(urlArray[1]);
					sb.append(SPLIT_URL_STR);
					returnUrl = sb.toString();
				} else if (null != urlArray && urlArray.length == 2) {
					// 字符串加http://
					sb.append(HTTP_STR);
					sb.append(urlArray[0]);
					// 字符串加/
					sb.append(SPLIT_URL_STR);
					returnUrl = sb.toString();
				} else {
					returnUrl = url;
				}
			} else {// 如果Url中不包括http://
				returnUrl = url;
			}
		} catch (Exception e) {
			returnUrl = "";
			e.printStackTrace();
		}
		return returnUrl;
	}
		
	/**
	 * 
	 * @description: 生成Map的输出的value
	 * @author:xixg
	 * @date:2014-01-03
	 * @param srcStrArray
	 *            输入时源数据的所有字段
	 * @return String 返回Map的value
	 */
	public static String createMapOutValue(List<String> fieldList) {
		StringBuffer mapValueSb = new StringBuffer();
		// 1 ipSid
		mapValueSb.append(fieldList.get(18) == null ? "" : fieldList.get(18));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 1 serviceName新业务需求删除了
		// mapValueSb.append(fieldList.get(15));
		// mapValueSb.append(Constants.MAP_KEY_SPLIT);
		// 2 className新业务需求删除了
		// mapValueSb.append(fieldList.get(16));
		// mapValueSb.append(Constants.MAP_KEY_SPLIT);
		// 3 httpId新业务需求删除了
		// mapValueSb.append(fieldList.get(17));
		// mapValueSb.append(Constants.MAP_KEY_SPLIT);
		// 2 delaytime/duration
		mapValueSb.append(fieldList.get(19) == null ? "" : fieldList.get(19));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 3 starttime
		mapValueSb.append(fieldList.get(20) == null ? "" : fieldList.get(20));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 4 lasttime
		mapValueSb.append(fieldList.get(21) == null ? "" : fieldList.get(21));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 5 upbytes/up_data
		mapValueSb.append(fieldList.get(22) == null ? "" : fieldList.get(22));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 6 downbytes/down_data
		mapValueSb.append(fieldList.get(23) == null ? "" : fieldList.get(23));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 7 uppkgs/up_ip_pkgs
		mapValueSb.append(fieldList.get(24) == null ? "" : fieldList.get(24));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 8 downpkgs/down_ip_pkgs
		mapValueSb.append(fieldList.get(25) == null ? "" : fieldList.get(25));
		mapValueSb.append(MAP_KEY_SPLIT);

		// 9 uperrseq/ul_tcp_disordered_packets
		mapValueSb.append(fieldList.get(26) == null ? "" : fieldList.get(26));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 10 downerrseq/dl_tcp_disordered_packets
		mapValueSb.append(fieldList.get(27) == null ? "" : fieldList.get(27));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 11 updup/ul_tcp_retransmission_packets
		mapValueSb.append(fieldList.get(28) == null ? "" : fieldList.get(28));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 12 downdup/dl_tcp_retransmission_packets
		mapValueSb.append(fieldList.get(29) == null ? "" : fieldList.get(29));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 13 upfrag/ul_ip_frag_packets
		mapValueSb.append(fieldList.get(30) == null ? "" : fieldList.get(30));
		mapValueSb.append(MAP_KEY_SPLIT);
		// 14 downfrag/dl_ip_frag_packets
		mapValueSb.append(fieldList.get(31) == null ? "" : fieldList.get(31));
		return mapValueSb.toString();
	}
	
	/**
	 * 将CombineEntity转换成Record
	 * 
	 * @param entity
	 * @return Record
	 */
	public static Record ValuesToRecordBean(CombineEntity entity) {
		Record bean = new Record();
		if (null == entity.getContent()
				|| "".equals(entity.getContent().toString())) {
			return null;
		}
		List<String> valueList = Arrays.asList(entity.getContent().toString()
				.split(COMMA_SEPARATOR, -1));
		Iterator<String> iterator = valueList.iterator();
		if (entity.getFlag().toString().equals(HTTP_FILE_FLAG)) {
			if (valueList.size() < 10) {
				return null;
			}
			bean.setFlag(HTTP_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setHost(iterator.next());
			bean.setUri(iterator.next());
			bean.setApp_class(iterator.next());
			bean.setApp_class_top(iterator.next());
			bean.setWeb_classify(iterator.next());
			bean.setWeb_name(iterator.next());
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
			bean.setUser_agent(iterator.next());

		} else if (entity.getFlag().toString().equals(RTSP_FILE_FLAG)) {
			if (valueList.size() < 9) {
				return null;
			}
			bean.setFlag(RTSP_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setRtp_server_ip(iterator.next());
			bean.setApp_class(iterator.next());
			bean.setApp_class_top(iterator.next());
			bean.setWeb_classify(iterator.next());
			bean.setWeb_name(iterator.next());
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());

		} else if (entity.getFlag().toString()
				.equals(EMAIL_FILE_FLAG)) {
			if (valueList.size() < 9) {
				return null;
			}
			bean.setFlag(EMAIL_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setApp_class("");
			bean.setApp_class_top("");
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
			bean.setUser_name(iterator.next());

		} else if (entity.getFlag().toString().equals(IP_FILE_FLAG)) {
			if (valueList.size() < 28) {
				return null;
			}
			bean.setFlag(IP_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setApp_class(iterator.next());
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
			bean.setRat(iterator.next());
			bean.setLac(iterator.next());
			bean.setCid(iterator.next());
			bean.setCharge_id(iterator.next());
			bean.setMsisdn(iterator.next());
			bean.setTerminal_model(iterator.next());
			bean.setApn(iterator.next());
			bean.setImei(iterator.next());
			bean.setStart_time(iterator.next());
			bean.setEnd_time(iterator.next());
			bean.setImsi(iterator.next());
			bean.setL4_protocol(iterator.next());
			bean.setApp_class_top(iterator.next());
			bean.setServer_prot(iterator.next());
			bean.setDuration(iterator.next());
			bean.setUl_tcp_disordered_packets(iterator.next());
			bean.setDl_tcp_disordered_packets(iterator.next());
			bean.setUl_tcp_retransmission_packets(iterator.next());
			bean.setDl_tcp_retransmission_packets(iterator.next());
			bean.setUl_ip_frag_packets(iterator.next());
			bean.setDl_ip_frag_packets(iterator.next());

		} else {
			if (valueList.size() < 8) {
				return null;
			}
			bean.setFlag(entity.getFlag().toString());
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setApp_class("");
			bean.setApp_class_top("");
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
		}
		return bean;
	}
	
	/**
	 * 将逗号分隔符的字符串拆分解析成map
	 * 
	 * @param columnNames
	 *            列名
	 * @param line
	 *            带'逗号分隔符的字符串
	 * @return
	 */
	public static Map<String, String> transformLineToMap(String[] columnNames,
			String line) {

		String[] columnValues = line.split(COMMA_SEPARATOR, -1);
		if (columnNames == null || columnValues == null
				|| columnNames.length != columnValues.length) {
			return null;
		}
		map.clear();
		for (int i = 0; i < columnNames.length; i++) {
			map.put(columnNames[i].toUpperCase(), columnValues[i] == null ? ""
					: columnValues[i]);
		}
		return map;

	}
	
	/**
	 * 获得需要的字段
	 * 
	 * @param map
	 *            所有字段与对应的值
	 * @param submitField
	 *            需要提取的字段名
	 * @return
	 */
	public static String getSubmitField(Map<String, String> map,
			String submitField) {

		sb.setLength(0);
		String[] fileds = submitField.split(COMMA_SEPARATOR, -1);
		for (String temp : Arrays.asList(fileds)) {
			sb.append(map.get(temp)).append(COMMA_SEPARATOR);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);

	}
	
	/**
	 * GNXDR轻度汇总累积时间计算
	 * */
	public static void main(String[] args) {
		// IP源数据转成关联后的产物（假设这条记录没有关联上HTTP）
		String IPSourceValue = "0592,1,460001705471462,8635640227443278,13501792475,10.199.23.80,24622,255,18091,221.177.96.172,221.177.96.172,221.177.102.241,221.177.102.241,2,CMWAP,2152,2152,100,4759325992318433400,4759325992318433400,1482449510114,148244951,15,500,网站,0,0,50581,183.232.95.186,80,-1,-1,616,3579,2240,2240,275,1597,8,6,3,2,0,0,0,0,0,0,0,25,294,0,294,5760,1394,1,0,1,0X00000000,,,,";
		// IP字段信息
		String GN_XDR_IP_FIELD = "city,interface,imsi,imei,msisdn,user_ip,lac,rac,cid,sgsn_c_ip,sgsn_u_ip,ggsn_c_ip,ggsn_u_ip,rat,apn,sgsn_port,ggsn_port,xdr_type,procedure_id,ip_id,start_time,end_time,app_class_top,app_class,business,l4_protocol,busi_type,user_port,server_ip,server_prot,country_code,net_code,up_data,down_data,duration,duration_1,up_rate,down_rate,up_ip_pkgs,down_ip_pkgs,action_flag,complete_flag,busi_delay,ul_tcp_disordered_packets,dl_tcp_disordered_packets,ul_tcp_retransmission_packets,dl_tcp_retransmission_packets,ul_ip_frag_packets,dl_ip_frag_packets,tcp_built_delay,tcp_confirm_delay,first_tcp_success_delay,first_answer_delay,window_size,mss_size,tcp_attempts_cnt,tcp_connection_status,session_end_flag,charge_id,terminal_factory,terminal_model,apply_classify,apply_name";
		// 将输入的数据按逗号分隔符的字符串拆分解析成map<字段名,数据值>
		Map<String, String> entities = transformLineToMap(GN_XDR_IP_FIELD.split(COMMA_SEPARATOR, -1), IPSourceValue);
		// M to R需要传输的字段
		String mapIpField = "ip_id,server_ip,apply_name,up_data,down_data,up_ip_pkgs,down_ip_pkgs,rat,lac,cid,charge_id,msisdn,terminal_model,apn,imei,start_time,end_time,imsi,l4_protocol,apply_classify,server_prot,duration,ul_tcp_disordered_packets,dl_tcp_disordered_packets,ul_tcp_retransmission_packets,dl_tcp_retransmission_packets,ul_ip_frag_packets,dl_ip_frag_packets";
		
		Text flag = new Text();
		Text joinKey = new Text();
		Text content = new Text();
		// 数据类型标识
    	flag.set(IP_FILE_FLAG);
    	// 关联条件
    	joinKey.set(entities.get(JOIN_KEY));
    	// 值
    	content.set(getSubmitField(entities,mapIpField.toUpperCase()));
		// 输出对象
		CombineEntity combineEntity = new CombineEntity();
		// 输出对象.数据类型标识
    	combineEntity.setFlag(flag);
    	// 输出对象.关联条件
    	combineEntity.setJoinKey(joinKey);
    	// 输出对象.值
    	combineEntity.setContent(content);

    	Record record = null;
		// 如果数据类型是IP类型
		if (IP_FILE_FLAG.equals(combineEntity.getFlag().toString())) {
			// 将数据转换成可识别的java bean对象
			record = ValuesToRecordBean(combineEntity);
		}		
		
		// 获得IP未关联的产物
		String lineValue = record.getStatisticsCol();
		// 将源数据的每行以","分隔成字段放入List中
		List<String> fieldList = splitStringByComma(lineValue);
		String mapOutValueStr = createMapOutValue(fieldList);
		System.out.println("[mapOutValueStr]"+mapOutValueStr);
		List<String> lineValueList = splitStringByComma(mapOutValueStr);
		//统计delayTime
		long delayTime = createDelayTime(lineValueList);
		System.out.println("[delayTime]"+delayTime);
	}
}

class Record {
	
	//标识记录归属业务类型
	private String flag;
	//输出字段
	private String ip_id;
	private String msisdn;
	private String terminal_model;
	private String apn;
	private String start_time;
	private String end_time;
	private String up_data;
	private String down_data;
	private String rat;
	private String app_class;
	private String lac;
	private String cid;
	private String charge_id;
	private String server_ip;
	private String imsi;
	private String l4_protocol;
	private String app_class_top;
	private String server_prot;
	private String duration;
	private String ul_tcp_disordered_packets;
	private String dl_tcp_disordered_packets;
	private String ul_tcp_retransmission_packets;
	private String dl_tcp_retransmission_packets;
	private String ul_ip_frag_packets;
	private String dl_ip_frag_packets;
	
	
	//http独有属性
	private String host;
	private String uri;
	private String user_agent;
	
	//rtsp独有属性
	private String rtp_server_ip;
	//email独有属性-收件人
	private String user_name;
	
	//需要imei关联终端配置表得出终端类型terminaltype
	private String imei;
	private String terminaltype;
	
	//计算流量均摊需要使用的字段
	private String up_ip_pkgs;
	private String down_ip_pkgs;
	
	//汇总计算使用的字段，精确到小时
	private String period_of_time;
	//汇总计算使用的字段，精确到日
	private String sum_date;
	
	
	private String web_classify;
	private String web_name;
	
	public Record() {
		super();
	}
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	//根据各个业务决定返回值
	public String getUrl() {
		
		if(StaticGnXdrTest.DNS_FILE_FLAG.equals(this.getFlag().toString())){
			if(null!=server_ip&&!"".equals(server_ip)){
				return server_ip+"(DNS)";
			}else{
				return server_ip;
			}
		}else if(StaticGnXdrTest.HTTP_FILE_FLAG.equals(this.getFlag().toString())){
			if(null!=uri&&!"".equals(uri)){
				return uri;
			}else{
				return server_ip;
			}
		}else if(StaticGnXdrTest.RTSP_FILE_FLAG.equals(this.getFlag().toString())){
			if(null!=rtp_server_ip&&!"".equals(rtp_server_ip)){
				return rtp_server_ip;
			}else{
				return server_ip;
			}
		}else if(StaticGnXdrTest.MMS_FILE_FLAG.equals(this.getFlag().toString())){
			return uri;
		}else if(StaticGnXdrTest.EMAIL_FILE_FLAG.equals(this.getFlag().toString())){
			return user_name;
		}else{
			return server_ip;
		}
	}

	public String getServer_ip() {
		return server_ip;
	}

	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getRtp_server_ip() {
		return rtp_server_ip;
	}

	public void setRtp_server_ip(String rtp_server_ip) {
		this.rtp_server_ip = rtp_server_ip;
	}

	public String getIp_id() {
		return ip_id;
	}
	public void setIp_id(String ip_id) {
		this.ip_id = ip_id;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getTerminal_model() {
		return terminal_model;
	}
	public void setTerminal_model(String terminal_model) {
		this.terminal_model = terminal_model;
	}
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getUp_data() {
		return up_data;
	}
	public void setUp_data(String up_data) {
		this.up_data = up_data;
	}
	public String getDown_data() {
		return down_data;
	}
	public void setDown_data(String down_data) {
		this.down_data = down_data;
	}
	public String getRat() {
		return "1".equals(rat)?"1":"0";
	}
	public void setRat(String rat) {
		this.rat = rat;
	}
	public String getApp_class() {
		return app_class;
	}
	public void setApp_class(String app_class) {
		this.app_class = app_class;
	}
	public String getLac() {
		return lac;
	}
	public void setLac(String lac) {
		this.lac = lac;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCharge_id() {
		return charge_id;
	}
	public void setCharge_id(String charge_id) {
		this.charge_id = charge_id;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getTerminaltype() {
		return terminaltype;
	}
	public void setTerminaltype(String terminaltype) {
		this.terminaltype = terminaltype;
	}
	public String getUp_ip_pkgs() {
		return up_ip_pkgs;
	}
	public void setUp_ip_pkgs(String up_ip_pkgs) {
		this.up_ip_pkgs = up_ip_pkgs;
	}
	public String getDown_ip_pkgs() {
		return down_ip_pkgs;
	}
	public void setDown_ip_pkgs(String down_ip_pkgs) {
		this.down_ip_pkgs = down_ip_pkgs;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getL4_protocol() {
		return l4_protocol;
	}

	public void setL4_protocol(String l4_protocol) {
		this.l4_protocol = l4_protocol;
	}

	public String getApp_class_top() {
		return app_class_top;
	}

	public void setApp_class_top(String app_class_top) {
		this.app_class_top = app_class_top;
	}

	public String getServer_prot() {
		return server_prot;
	}

	public void setServer_prot(String server_prot) {
		this.server_prot = server_prot;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getUl_tcp_disordered_packets() {
		return ul_tcp_disordered_packets;
	}

	public void setUl_tcp_disordered_packets(String ul_tcp_disordered_packets) {
		this.ul_tcp_disordered_packets = ul_tcp_disordered_packets;
	}

	public String getDl_tcp_disordered_packets() {
		return dl_tcp_disordered_packets;
	}

	public void setDl_tcp_disordered_packets(String dl_tcp_disordered_packets) {
		this.dl_tcp_disordered_packets = dl_tcp_disordered_packets;
	}

	public String getUl_tcp_retransmission_packets() {
		return ul_tcp_retransmission_packets;
	}

	public void setUl_tcp_retransmission_packets(
			String ul_tcp_retransmission_packets) {
		this.ul_tcp_retransmission_packets = ul_tcp_retransmission_packets;
	}

	public String getDl_tcp_retransmission_packets() {
		return dl_tcp_retransmission_packets;
	}

	public void setDl_tcp_retransmission_packets(
			String dl_tcp_retransmission_packets) {
		this.dl_tcp_retransmission_packets = dl_tcp_retransmission_packets;
	}

	public String getUl_ip_frag_packets() {
		return ul_ip_frag_packets;
	}

	public void setUl_ip_frag_packets(String ul_ip_frag_packets) {
		this.ul_ip_frag_packets = ul_ip_frag_packets;
	}

	public String getDl_ip_frag_packets() {
		return dl_ip_frag_packets;
	}

	public void setDl_ip_frag_packets(String dl_ip_frag_packets) {
		this.dl_ip_frag_packets = dl_ip_frag_packets;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPeriod_of_time() {
		return period_of_time;
	}

	public void setPeriod_of_time(String period_of_time) {
		this.period_of_time = period_of_time;
	}

	public String getSum_date() {
		return sum_date;
	}

	public void setSum_date(String sum_date) {
		this.sum_date = sum_date;
	}
	
	//华为应用识别
	public String  getWeb_classify(){
		return web_classify;
	}
	public void setWeb_classify(String web_classify) {
		this.web_classify = web_classify;	
	}
	public String  getWeb_name(){
		return web_name;
	}
	public void setWeb_name(String web_name) {
		this.web_name = web_name;	
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("flag:").append( null == this.flag?"":this.flag).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ip_id:").append( null == this.ip_id?"":this.ip_id).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("msisdn:").append( null == this.msisdn?"":this.msisdn).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("terminal_model:").append( null == this.terminal_model?"":this.terminal_model).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("apn:").append( null == this.apn?"":this.apn).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("start_time:").append( null == this.start_time?"":this.start_time).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("end_time:").append( null == this.end_time?"":this.end_time).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("up_data:").append( null == this.up_data?"":this.up_data).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("down_data:").append( null == this.down_data?"":this.down_data).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("rat:").append( null == this.rat?"":this.rat).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("app_class:").append( null == this.app_class?"":this.app_class).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("lac:").append( null == this.lac?"":this.lac).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("cid:").append( null == this.cid?"":this.cid).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("charge_id:").append( null == this.charge_id?"":this.charge_id).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("server_ip:").append( null == this.server_ip?"":this.server_ip).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("imsi:").append( null == this.imsi?"":this.imsi).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("l4_protocol:").append( null == this.l4_protocol?"":this.l4_protocol).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("app_class_top:").append( null == this.app_class_top?"":this.app_class_top).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("server_prot:").append( null == this.server_prot?"":this.server_prot).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("duration:").append( null == this.duration?"":this.duration).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ul_tcp_disordered_packets:").append( null == this.ul_tcp_disordered_packets?"":this.ul_tcp_disordered_packets).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("dl_tcp_disordered_packets:").append( null == this.dl_tcp_disordered_packets?"":this.dl_tcp_disordered_packets).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ul_tcp_retransmission_packets:").append( null == this.ul_tcp_retransmission_packets?"":this.ul_tcp_retransmission_packets).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("dl_tcp_retransmission_packets:").append( null == this.dl_tcp_retransmission_packets?"":this.dl_tcp_retransmission_packets).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ul_ip_frag_packets:").append( null == this.ul_ip_frag_packets?"":this.ul_ip_frag_packets).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("dl_ip_frag_packets:").append( null == this.dl_ip_frag_packets?"":this.dl_ip_frag_packets).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("host:").append( null == this.host?"":this.host).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("uri:").append( null == this.uri?"":this.uri).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("user_agent:").append( null == this.user_agent?"":this.user_agent).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("rtp_server_ip:").append( null == this.rtp_server_ip?"":this.rtp_server_ip).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("user_name:").append( null == this.user_name?"":this.user_name).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("imei:").append( null == this.imei?"":this.imei).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("terminaltype:").append( null == this.terminaltype?"":this.terminaltype).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("up_ip_pkgs:").append( null == this.up_ip_pkgs?"":this.up_ip_pkgs).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("down_ip_pkgs:").append( null == this.down_ip_pkgs?"":this.down_ip_pkgs);

		return sb.toString();
	}

	public String getStatisticsCol() {
		StringBuffer sb = new StringBuffer();
		sb.append( null == this.msisdn?"":this.getMsisdn()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.imsi?"":this.getImsi()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.imei?"":this.getImei()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.apn?"":this.getApn()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.l4_protocol?"":this.getL4_protocol()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.rat?"":this.getRat()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.lac?"":this.getLac()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.cid?"":this.getCid()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.app_class_top?"":this.getApp_class_top()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.app_class?"":this.getApp_class()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.web_classify?"":this.getWeb_classify()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.web_name?"":this.getWeb_name()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.host?"":this.getHost()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.server_ip?"":this.getServer_ip()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.server_prot?"":this.getServer_prot()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.user_agent?"":this.getUser_agent()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( this.getUrl()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.period_of_time?"":this.getPeriod_of_time()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.ip_id?"":this.getIp_id()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.duration?"":this.getDuration()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.start_time?"":this.getStart_time()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.end_time?"":this.getEnd_time()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.up_data?"":this.getUp_data()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.down_data?"":this.getDown_data()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.up_ip_pkgs?"":this.getUp_ip_pkgs()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.down_ip_pkgs?"":this.getDown_ip_pkgs()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.ul_tcp_disordered_packets?"":this.getUl_tcp_disordered_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.dl_tcp_disordered_packets?"":this.getDl_tcp_disordered_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.ul_tcp_retransmission_packets?"":this.getUl_tcp_retransmission_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.dl_tcp_retransmission_packets?"":this.getDl_tcp_retransmission_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.ul_ip_frag_packets?"":this.getUl_ip_frag_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.dl_ip_frag_packets?"":this.getDl_ip_frag_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append( null == this.sum_date?"":this.getSum_date());

		return sb.toString();
	}
	
	public String getStatisticsColTest() {
		StringBuffer sb = new StringBuffer();
		sb.append("msisdn:").append( null == this.msisdn?"":this.getMsisdn()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("imsi:").append( null == this.imsi?"":this.getImsi()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("imei:").append( null == this.imei?"":this.getImei()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("apn:").append( null == this.apn?"":this.getApn()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("l4_protocol:").append( null == this.l4_protocol?"":this.getL4_protocol()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("rat:").append( null == this.rat?"":this.getRat()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("lac:").append( null == this.lac?"":this.getLac()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("cid:").append( null == this.cid?"":this.getCid()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("app_class_top:").append( null == this.app_class_top?"":this.getApp_class_top()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("app_class:").append( null == this.app_class?"":this.getApp_class()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("web_classify:").append("").append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("web_name:").append("").append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("host:").append( null == this.host?"":this.getHost()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("server_ip:").append( null == this.server_ip?"":this.getServer_ip()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("server_prot:").append( null == this.server_prot?"":this.getServer_prot()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("user_agent:").append( null == this.user_agent?"":this.getUser_agent()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("url:").append(this.getUrl()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("period_of_time:").append(this.getPeriod_of_time()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ip_id:").append( null == this.ip_id?"":this.getIp_id()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("duration:").append( null == this.duration?"":this.getDuration()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("up_data:").append( null == this.up_data?"":this.getUp_data()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("down_data:").append( null == this.down_data?"":this.getDown_data()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("up_ip_pkgs:").append( null == this.up_ip_pkgs?"":this.getUp_ip_pkgs()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("down_ip_pkgs:").append( null == this.down_ip_pkgs?"":this.getDown_ip_pkgs()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("start_time:").append( null == this.start_time?"":this.getStart_time()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("end_time:").append( null == this.end_time?"":this.getEnd_time()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ul_tcp_disordered_packets:").append( null == this.ul_tcp_disordered_packets?"":this.getUl_tcp_disordered_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("dl_tcp_disordered_packets:").append( null == this.dl_tcp_disordered_packets?"":this.getDl_tcp_disordered_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ul_tcp_retransmission_packets:").append( null == this.ul_tcp_retransmission_packets?"":this.getUl_tcp_retransmission_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("dl_tcp_retransmission_packets:").append( null == this.dl_tcp_retransmission_packets?"":this.getDl_tcp_retransmission_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("ul_ip_frag_packets:").append( null == this.ul_ip_frag_packets?"":this.getUl_ip_frag_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("dl_ip_frag_packets:").append( null == this.dl_ip_frag_packets?"":this.getDl_ip_frag_packets()).append(StaticGnXdrTest.COMMA_SEPARATOR)
		.append("sum_date:").append(this.getSum_date());

		return sb.toString();
	}
}

class CombineEntity implements WritableComparable<CombineEntity> {
	private Text joinKey;	//关联key
	private Text flag;		//数据类型标识
	private Text content;	//值

	public CombineEntity() {
		this.joinKey = new Text();
		this.flag = new Text();
		this.content = new Text();
	}
	
	public Text getJoinKey() {
		return joinKey;
	}
	public void setJoinKey(Text joinKey) {
		this.joinKey = joinKey;
	}
	public Text getFlag() {
		return flag;
	}
	public void setFlag(Text flag) {
		this.flag = flag;
	}
	public Text getContent() {
		return content;
	}
	public void setContent(Text content) {
		this.content = content;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.joinKey.readFields(in);
		this.flag.readFields(in);
		this.content.readFields(in);
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		this.joinKey.write(out);
		this.flag.write(out);
		this.content.write(out);
	}

	@Override
	public int compareTo(CombineEntity o) {
		return this.joinKey.compareTo(o.joinKey);
	}
}