package com.bussiness.bi.bigdata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class RealTimeAnalysis {
	private static Map<String, Map<String, String>> dictmap = new HashMap<String, Map<String, String>>();
	private static String dictpath = "d:/home/dict/";
	private static final String dictstr = "dict";
	private static final String joinurl = "joinurl()";
	private static final String evl_split_str = "\\+,\\+";
	private static final String str_param1 = "S:";
	private static final String str_param2 = "str";	
	
	public static void init(String str){
		String tmp = "";
		String[] arrtmp = null;
		if(str!=null && str.trim().length()>0){
			tmp = str.replace("\\", "");
			tmp = tmp.replace(str_param1, str_param2);
			arrtmp = tmp.split(evl_split_str, -1);
			for(String s : arrtmp){
				if(s.indexOf("dict")>=0){
					String filename = getDictFirstArgs(s);
					String path = dictpath+filename;
//					System.out.println("path:"+path);
					Map<String, String> dictRecords = new HashMap<String, String>();
					FileInputStream fis = null;
					BufferedReader br = null;
					try {
						fis = new FileInputStream(path);
						br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
						// 按行读取
						String lineWebname = br.readLine();
						while (null != lineWebname) {
							String[] webnameFileArr = lineWebname.split(",", -1);
							dictRecords.put(webnameFileArr[0], webnameFileArr[1]);
							lineWebname = br.readLine();
						}
						br.close();
						fis.close();
						dictmap.put(filename, dictRecords);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(br!=null){
							try {
								br.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if(fis!=null){
							try {
								fis.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}		
	}
	
	/**
	 * 单独维表加载
	 * @param path 维表路径
	 * @return
	 * */
	public Map<String, String> dimLoad(String path){
		Map<String, String> dictRecords = new HashMap<String, String>();
		if(path!=null && path.trim().length()>0){
			FileInputStream fis = null;
			BufferedReader br = null;
			try {
				try {
					fis = new FileInputStream(path);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("读取"+path+"配置文件异常!"+e.toString());
					return null;
				}
				br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
				// 按行读取
				String lineWebname = br.readLine();
				while (null != lineWebname) {
					String[] webnameFileArr = lineWebname.split(",", -1);
					dictRecords.put(webnameFileArr[0], webnameFileArr[1]);
					lineWebname = br.readLine();
				}
				br.close();
				fis.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(br!=null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fis!=null){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return dictRecords;
	}	
	
	public String nullstr(){
		return "";
	}
	
	public String parseOxInt(String OxStr){		
		int value = 0;
		if(OxStr!=null && OxStr.trim().length()>0)
			value = Integer.parseInt(OxStr, 16);
		return String.valueOf(value);
	}
	
	public String intMoreThan(String[] args, int value, String defaultvalue){
		String str = "";
		for(String s : args){
			if(Integer.valueOf(s)>value){
				str = s;
				break;
			}
		}
		if(str.length()==0){
			str = defaultvalue;
		}
		return str;
	}
	
	public String[] getMoreArgs(String...args){
		return args;
	}
	
	public double floatdiv(String divisor, double dividend,int decimal){
		double value = 0;
		BigDecimal b= new BigDecimal(Double.valueOf(divisor)/dividend);
		value = b.setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue();
		return value;
	}
	
	public String timeStampFormat(String str, String formatType){
		String value = "";
		long time = Long.valueOf(str);
		SimpleDateFormat format = new SimpleDateFormat(formatType);
		Date date = new Date(time);
		value = format.format(date);
		return value;
	}
	
	public String substr(String str, int begin, int length, String default_str){
		String value = default_str;
		if(str!=null && str.length()>=(begin+length)){
			value = str.substring(begin, begin+length);
		}
		return value;
	}
	
	public String decode(String str, String...args){
		String value = "";
//		System.out.println("decode str:"+str);
		if(args!=null && args.length>0){
			if(args.length%2==0){				
			}else{
				value = args[args.length-1];
			}
			for(int i=0;i<args.length;i+=2){
				if(i%2==0){
					if(str.equals(args[i])){
						// 必须判断下标是否超出数组大小
						if((i+1)<args.length){
							value = args[i+1];
							break;
						}
					}
				}
			}
		}
		return value;
	}
	
	public String dict(String mapname, String key){
		String value = "";
		if(dictmap.get(mapname)!=null){
			value = dictmap.get(mapname).get(key);
		}
		return value;
	}
	
	public static String getDictFirstArgs(String str){
		int index = -1;
		int end = -1;
		String value = "";
		if(str!=null && str.length()>0){
			int dict_index = str.indexOf(dictstr);
			String _tmp = "";
			if(dict_index>=0){
				_tmp = str.substring(dict_index);
				index = _tmp.indexOf("(");
				end = _tmp.indexOf(",");
				if(index>-1 && end>-1){
					value = _tmp.substring(index+1, end).replace("\"", "");
				}
			}			
		}
		return value;
	}
	
	public String evaluate(String str, Map<String, String> args){
		StringBuffer sb = new StringBuffer("");
		String tmp = "";
		String[] arrtmp = null;
		RealTimeAnalysis t = new RealTimeAnalysis();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("t", t);
		Iterator<?> iter = args.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String)entry.getKey();
			Object val = entry.getValue();
			map.put(key, val);
		}
		map.put(",", ",");
		map.put("(", "(");
		map.put(")", ")");
		map.put(joinurl, joinurl);
//		System.out.println("split.map:"+map);
		if(str!=null && str.trim().length()>0){
			tmp = str.replace("\\", "");
			tmp = tmp.replace(str_param1, str_param2);
			arrtmp = tmp.split(evl_split_str, -1);
			for(String s : arrtmp){
//				System.out.println("s:"+s);	
				if(s.indexOf("floatdiv")>=0
						|| s.indexOf("timeStampFormat")>=0
						|| s.indexOf("substr")>=0
						|| s.indexOf("dict")>=0
						|| s.indexOf("decode")>=0
						|| s.indexOf("nullstr")>=0
						|| s.indexOf("parseOxInt")>=0
						|| s.indexOf("intMoreThan")>=0
						|| s.indexOf("getMoreArgs")>=0
						|| s.indexOf("concat")>=0){
					// 替换成t.
					s = s.replace("floatdiv(", "t.floatdiv(");
					s = s.replace("timeStampFormat(", "t.timeStampFormat(");
					s = s.replace("substr(", "t.substr(");
					s = s.replace("dict(", "t.dict(");
					s = s.replace("decode(", "t.decode(");
					s = s.replace("nullstr(", "t.nullstr(");
					s = s.replace("parseOxInt(", "t.parseOxInt(");
					s = s.replace("intMoreThan(", "t.intMoreThan(");
					s = s.replace("getMoreArgs(", "t.getMoreArgs(");
					sb.append(invokeMethod(s,map));
					sb.append(",");
//					System.out.println("s-m:"+s);	
				}else{
					sb.append(map.get(s));
					sb.append(",");
//					System.out.println("s:"+s);	
				}
			}
		}
		if(sb.length()>0)sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public Object invokeMethod(String jexlExp,Map<String,Object> map){
		JexlEngine jexl=new JexlEngine();
		Expression e = jexl.createExpression(jexlExp);
		JexlContext jc = new MapContext();
		for(String key:map.keySet()){
			jc.set(key, map.get(key));
		}
		if(null==e.evaluate(jc)){
			return "";
		}
		return e.evaluate(jc);
	}
	
	public static void main(String[] args) {
		List<String> typelist = new ArrayList<String>();
		typelist.add("lte_email");
		typelist.add("lte_ftp");
		typelist.add("lte_http");
		typelist.add("lte_im");
		typelist.add("lte_mms");
		typelist.add("lte_p2p");
		typelist.add("lte_rtsp");
		typelist.add("lte_voip");
		typelist.add("lte_dns");
		typelist.add("lte_gen");
		typelist.add("gn_dns");
		typelist.add("gn_email");
		typelist.add("gn_ftp");
		typelist.add("gn_general");
		typelist.add("gn_http");
		typelist.add("gn_im");
		typelist.add("gn_mms");
		typelist.add("gn_p2p");
		typelist.add("gn_rtsp");
		typelist.add("gn_voip");
		
		Map<String, String> value_map = new HashMap<String, String>();
		value_map.put("lte_email", "2|803|898|11|17050001ab63fa00|6|460025899260017|866693020969411|8618389234508|1|100.88.70.18|100.88.60.87|2152|2152|00005f7e|7c833e23|29965|074d1e03|CMNET|105|1452222561269|1452222561426||11|10074|||10.180.75.172||36843|0|123.125.50.48||143|119|361|2|1|0|0|0|0|71|89|0|0|0|0|81664|1394|0|0|1||65534|||0||0||1");
		value_map.put("lte_ftp", "2|557|898|11|1e040001c5e2a900|6|460006504762199|863473028738784|8613976138283|1|100.88.70.21|100.88.53.212|2152|2152|000056f1|46510bf7|30072|0e59a701|CMNET|104|1452077111962|1452077112198||18|502|||10.229.90.251||37858|0|119.188.141.207||55615|216|640|4|4|0|0|0|0|56|0|0|0|||65728|1400|1|0|1|||||0|township/500/notice.json|37858|55615|640||179");
		value_map.put("lte_http", "2|2266|898|11|1d040001a7a34000|6|460078894833319|867148027692580|8618889392867|1|100.88.70.18|100.88.3.206|2152|2152|0000a1b1|78a381d6||075df403|CMNET|103|1452222588787|1452222588933||1|9|5|0|10.180.118.56||49860|0|221.182.131.194||80|695|4338|8|8|0|1|0|0|1|79|0|0|20|146|65535|1394|1|0|1|3|6|200|146|146|184|mmsns.qpic.cn|mmsns.qpic.cn/mmsns/zibTZeDuMVJdYmchADMIcRIUmZWibNJjk65dyKHN7SHV86Ht2qs9frQo9Ex5P63I8tV2srI9iaF2IQ/150?tp=webp||Dalvik/1.6.0 (Linux; U; Android 4.4.4; OPPO R7 Build/KTU84P)|image/webp|weixin.qq.com/?version=637732936&uin=1792131782&nettype=13000&signal=13&scene=timeline||3710|0|||||3|0|146|0|1");
		value_map.put("lte_im", "2|347|898|11|0c090001b7e30d50|0||||1|100.88.68.31|100.88.22.53|2152|2152|000045b7|0c1cde6e||||108|1452222497953|1452222500153||1|9|5|2|10.68.191.28||54304|0|221.182.131.197||80|1434|17348|16|18|0|5|0|0|3|35|0|0|0||65535|1400|1|0|1||||");
		value_map.put("lte_mms", "2|1641|898|11|1f0e0001b0c01300|6|460078894272704|351981060833841|8618889107277|1|100.88.70.17|100.88.66.36|2152|2152|00002d47|50316497|30004|075fc101|CMWAP|102|1452223198467|1452223203656||14|4|||10.214.216.254||50728|0|10.0.0.172||80|246431|5774|182|106|0|0|3|0|1|39|0|0|16|5189|132096|1394|1|0|1|0|0|1|200|128||010811200398980001335|1452223196-1|+8618089769933/TYPE=PLMN|1||0||232051||mmsc.monternet.com|mmsc.monternet.com|");
		value_map.put("lte_p2p", "2|398|898|11|15060001a8645900|6|460026894916554|359314061651503|8618289906643|1|100.88.70.18|100.88.0.243|2152|2152|0001a701|7842c84b|29976|07579b02|CMNET|109|1452222570324|1452222570390||12|10014|||10.181.146.225||34160|1|139.204.44.131||37930|132|338|1|1|0|0|0|0|0|0|0|0|||0|0|||1|0||");
		value_map.put("lte_rtsp", "2|650|898|11|0c0b00035af49d50|0||||1|100.88.68.19|100.88.15.13|2152|2152|00004b2a|0fa080d9||||107|1452103024936|1452103382313||18|22503|||10.186.105.126||51297|0|223.198.152.238||85|605998|7760457|11505|15078|0|0|34|0|47|65|0|0|10||131840|1400|1|0|1||||||||0|0|0");
		value_map.put("lte_voip", "2|320|898|11|1c040001a509d000|6|460007517060429|868291029776925|8613707563008|1|100.88.70.17|100.88.46.33|2152|2152|00015931|50543f97|30007|07576901|CMNET|106|1452222562338|1452222562588||13|10774|||10.145.195.200||58151|0|117.144.234.10||14001|480|399|6|4|0|0|0|0|47|60|0|0|0||14656|1394|1|0|1|||||||");
		value_map.put("lte_dns", "2|334|898|11|1d090003382bcb00|6|460006403708363|866641026462238|8613876918140|1|100.88.70.16|100.88.40.220|2152|2152|000222c1|4e144564|30002|07572a01|CMNET|101|1452265797550|1452265797551||18|501|||10.143.34.196||1334|1|211.138.161.178||53|66|128|1|1|0|0|0|0|0|0|0|0||1|0|0|||1|m-adash.m.taobao.com|106.11.4.75|0|1|3|0|0");
		value_map.put("lte_gen", "2|250|898|11|1f0c00033e09d200|6|460078894713756|865141020917387|8618889509612|1|100.88.70.22|100.88.41.171|2152|2152|00021b81|8e1443eb|30116|075d7002|CMNET|100|1452266990873|1452266990932||18|535|||10.208.150.116||40096|0|115.238.54.243||8038|60|40|1|1|0|0|0|0|0|0|0|0|||0|0|1|1|1");
		value_map.put("gn_dns", "1|460023089292010|8658630231097101|8615008082005|10.82.76.0|58692|0|41731|223.103.23.2|223.103.23.28|221.177.112.224|221.177.112.228|1|cmnet|101|6237009239393575008-3-1452166500-610158284-610158574|1452166875716|1452166875721|18|26|1|52811|211.138.161.178|53|460|0|60|76|1|1|0|0|0|0|0|0|110.173.196.36|0|1|0|0|0|281091879|null|儋州洋浦夏兰小学|中国移动|中国|898|0898|898|0898|4|||HS5R2|||");
		value_map.put("gn_email", "1|460006174776068|8654540242223578|8613976076795|10.100.207.117|58641|0|37622|221.177.113.198|221.177.113.210|221.177.112.235|221.177.112.236|1|cmnet|105|6236948534480060513-3-1452152311-598150748-598152524|1452152740514|1452152749375|18|26|0|58595|202.100.211.105|110|460|0|696|1026|12|14|0|0|0|0|0|0|0|3|ll@jst.com.cn||0||||1|703614694|null|海口美国工业村门口灯杆|中国移动|中国|898|0898|898|0898|4|||HS12R2|小米|红米NOTE|手机类");
		value_map.put("gn_ftp", "1|460006464728267|8633310289235178|8613976751683|10.245.250.102|30086|0|41773|221.177.112.52|221.177.112.52|221.177.113.84|221.177.113.86|2|cmnet|104|6236950078695948354-2-1452153002-784058358-784058543|1452153097973|1452153101292|18|26|0|56831|221.182.133.48|21|460|0|180|191|4|3|0|0|0|0|0|0|421|||0|0||0|0|0|0|718329191|null|临高南宝郎基村3|中国移动|中国|898|0898|898|0898|4|||HS9B15|OPPO|TD-R823T|TD手机");
		value_map.put("gn_general", "1|460021208860917|8657370206607478|8615120887834|10.190.113.70|30140|0|49041|223.103.23.194|223.103.23.193|223.103.23.4|223.103.23.9|2|cmnet|101|6236951802801299650|1452153502718|1452153502770|18|26|1|23801|8.8.8.8|53|460|0|59|167|1|1|0|0|0|0|0|0|0|0|0|0|0|0|0|255|0|2166254265|null|儋州红岭农场7队1|中国移动|中国|898|0898|898|0898|4|||HS15W4|步步高|VIVO X5L|LTE手机");
		value_map.put("gn_http", "1|460027897732624|3520260718232406|8618789939477|10.195.67.242|30067|0|49411|221.177.113.107|221.177.113.107|223.103.23.4|223.103.23.5|2|cmnet|103|6236951120989561026-6-1452153127-612965977-612967621|1452153343960|1452153344379|1|9|0|55887|183.232.93.156|80|460|0|509|3445|3|4|0|0|0|0|0|0|6|200|3|24|24|420|mmsns.qpic.cn|http://mmsns.qpic.cn/mmsns/22ib19GvNMy1ibfcL1nxziamymvmiaEIbWpb8CODaIhyaMjl4FaTrB3g9NA6nJPMx4IiczliaXpuMIpGg/150?tp=webp&length=1334&width=750||WeChat/6.3.9.18 CFNetwork/711.4.6 Darwin/14.0.0|image/webp|http://weixin.qq.com/?version=369297683&uin=1624245583&nettype=0||2978|255|255|255|||1226733731|null|三亚太阳湾高级度假村柏悦酒店1|中国移动|中国|898|0898|898|0898|4|||HS13B5|||");
		value_map.put("gn_im", "1|460027897305337|3580940500832051|8618789858959|10.100.232.221|30161|0|31722|221.177.113.204|221.177.113.204|221.177.112.235|221.177.112.236|2|cmnet|108|6236950959130321025|1452153305405|1452153400070|18|26|0|33590|183.232.121.141|80|460|0|779|794|7|6|0|0|0|1|0|0||||1/0/0;2/0/0;3/0/0;4/0/0|1240546579|null|陵水祖关狗仔村2|中国移动|中国|898|0898|898|0898|4|||HS2B3|索尼|XPERIA Z1|手机类");
		value_map.put("gn_mms", "1|460025899373227|3571430487612416|8618389294873|10.250.118.61|58625|0|40603|221.177.113.196|221.177.113.210|223.103.23.4|223.103.23.8|1|cmwap|102|6236951673097420993-6-1452153426-156957668-156959636|1452153470020|1452153472500|18|26|0|58053|10.0.0.172|80|460|0|3832|178652|61|136|0|0|1|0|0|0|0|0|0|0|0|1|200|0|+8613876057360|010715561498980001503|RQBm51|8618389294873|1||0| 转发： |1897|0.0.0.0||http://211.138.175.231:181/RQBm51||18775140|null|海口海甸邦墩里43号||中国|||898|0898|2|||HS7R2|华为|H60-L01|LTE手机");
		value_map.put("gn_p2p", "1|460025899115511|8643750234041678|8618389578789|10.77.221.101|58677|0|54291|221.177.113.200|221.177.113.210|221.177.112.224|221.177.112.225|1|cmnet|109|6236951424367276129|1452153414601|1452153415202|12|1|0|53807|221.182.133.188|8080|460|0|732|424|5|5|0|0|0|0|0|0|0|0||701531551|null|三亚凤凰楼宾馆||中国|||898|0898|2|||HS8R3|小米|红米NOTE 1TD|TD手机");
		value_map.put("gn_rtsp", "1|460078894788936|8676660290235978|8618876786211|10.40.34.14|30012|0|1931|223.103.23.195|223.103.23.195|221.177.113.136|221.177.113.134|2|cmnet|107|6237007881637200034-5-1452166242-303427123-303432305|1452166559527|1452166560847|18|26|255|47323|59.50.168.162|554|460|0|966|2706|4|19|0|0|0|0|0|0|rtsp://59.50.168.162:554/mpeg4/ch39/sub/av_stream|NKPlayer-1.00.00.081112|0.0.0.0|0|0|0|0|0|0|58|412019759|null|儋州中和花兰村1|中国移动|中国|898|0898|898|0898|4|||HS5W4|||");
		value_map.put("gn_voip", "1|460028089395202|8643370200081978|8615808947971|10.47.139.127|58638|0|5093|223.103.23.169|223.103.23.176|221.177.113.73|221.177.113.79|1|cmnet|106|6237011233690939459|1452167340017|1452167352416|18|26|1|44213|114.215.139.130|5060|460|0|19172|35057|30|40|0|0|0|0|0|0|0|sip:15808947971@114.215.139.130|sip:018070865981@114.215.139.13|1|0|2|1|685847303|||中国移动|中国|898|0898|||2||||||");
		
		RealTimeAnalysis t = new RealTimeAnalysis();
		for(String type: typelist){
			// 实例化配置
			Configuration conf = new Configuration();
			conf.addResource(new Path("H:/Work/WorkSpace/MyEclipse10/self/edc-bigdata-pretreatmentHNData/src/main/resources/conf/config_pretreatmentHNData_"+type+".xml"));
//			System.out.println("[main]"+RealTimeAnalysis.class.getName());
//			System.out.println(division(2222.22, 1024, 2));
//			System.out.println(new Date().getTime());
//			System.out.println(timeFormat("1454229192990", "yyyyMMdd"));
//			System.out.println(timeFormat("1454229192990",substr("12yyyyMMdd",2,8)));
//			String values = "1|460023089292010|8658630231097101|8615008082005|10.82.76.0|58692|0|41731|223.103.23.2|223.103.23.28|221.177.112.224|221.177.112.228|1|cmnet|101|6237009239393575008-3-1452166500-610158284-610158574|1452166875716|1452166875721|18|26|1|52811|211.138.161.178|53|460|0|60|76|1|1|0|0|0|0|0|0|110.173.196.36|0|1|0|0|0|281091879|null|儋州洋浦夏兰小学|中国移动|中国|898|0898|898|0898|4|||HS5R2|||";
			
			String[] arr_values = value_map.get(type).split(conf.get("SOURCE_DATA_SEPARATOR"),-1);
			String result = "";
			Map<String, String> map = new HashMap<String, String>();
			for(int i=0;i<arr_values.length;i++){
				map.put("str"+i, arr_values[i]);
			}
//			System.out.println("input.map:"+map);
			String FIELDS_RULE = "";//sdict(\"TD_LAC\",S:16.concat(parseOxInt(S:17)))";
			FIELDS_RULE = conf.get("FIELDS_RULE");
			System.out.println(type+"[FIELDS_RULE]:"+FIELDS_RULE);
			init(FIELDS_RULE);
			result = t.evaluate(FIELDS_RULE, map);
			result = result.replace(joinurl, "识别WEB地址");
			System.out.println(type+"[result]:"+result);
		}
		
//		System.out.println(t.dimLoad(dictpath+"URL_CHK").get("10086.redirect.kuailedu.com"));
//		System.out.println(t.dict("DIM_TF_IMEI_TAC1", "86826102"));
//		System.out.println(t.dict("DIM_TF_IMEI_TAC2", "86826102"));
		
//		Map<String,Object> map=new HashMap<String,Object>();
//		map.put("t", t);
//		map.put("str1", "1454229192990");
//		map.put("str2", "yyyyMMdd");
//		map.put("str3", "");
//		map.put("str4", "86826102aa");
//		map.put("str10", "22222");
////		String expression="t.timeStampFormat(str1,\"yyyyMMdd\")";
////		String expression="t.floatdiv(str10,1024,2)";
////		String expression="t.substr(str10,2,3,str10)";
//		String expression="t.decode(str3, t.nullstr(), t.dict(\"DIM_TF_IMEI_TAC1\", t.substr(str4, 0, 8, str4)), str3)";
//		init(expression);
//		System.out.println(t.invokeMethod(expression,map));		
////		System.out.println(t.decode("1","1","2G","2","3G","4G"));
////		System.out.println(t.nullstr());
		
//		System.out.println(substr("abcdef",2,2));
//		Class<?> threadClazz;
//		try {
//			threadClazz = Class.forName("RealTimeAnalysis");
//			Method method = threadClazz.getMethod("division",
//					new Class[] {double.class, double.class, int.class});
//			System.out.println(method.invoke(null, new Object[] {2222.22, 1024, 2}));
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		}
	}
}
