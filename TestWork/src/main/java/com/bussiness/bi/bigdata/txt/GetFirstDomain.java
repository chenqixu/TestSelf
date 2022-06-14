package com.bussiness.bi.bigdata.txt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetFirstDomain {
	private static final String POINT = ".";
	private static final String SPLIT_POINT = "\\.";
	private static Map<String, String> TopMap = new HashMap<String, String>();
	private static Map<String, String> CountryMap = new HashMap<String, String>();
	static {
		// 国际顶级域名
		TopMap.put("ac","");
		TopMap.put("com","");
		TopMap.put("edu","");
		TopMap.put("gov","");
		TopMap.put("mil","");
		TopMap.put("arpa","");
		TopMap.put("net","");
		TopMap.put("org","");
		TopMap.put("biz","");
		TopMap.put("info","");
		TopMap.put("pro","");
		TopMap.put("name","");
		TopMap.put("coop","");
		TopMap.put("aero","");
		TopMap.put("museum","");
		TopMap.put("mobi","");
		TopMap.put("asia","");
		TopMap.put("tel","");
		TopMap.put("int","");
		TopMap.put("cc","");
		TopMap.put("tv","");
		TopMap.put("us","");
		TopMap.put("travel","");
		TopMap.put("xxx","");
		TopMap.put("idv","");
		// 国家顶级域名
		CountryMap.put("ad","");
		CountryMap.put("ae","");
		CountryMap.put("af","");
		CountryMap.put("ag","");
		CountryMap.put("ai","");
		CountryMap.put("al","");
		CountryMap.put("am","");
		CountryMap.put("an","");
		CountryMap.put("ao","");
		CountryMap.put("aq","");
		CountryMap.put("ar","");
		CountryMap.put("as","");
		CountryMap.put("at","");
		CountryMap.put("au","");
		CountryMap.put("aw","");
		CountryMap.put("az","");
		CountryMap.put("ba","");
		CountryMap.put("bb","");
		CountryMap.put("bd","");
		CountryMap.put("be","");
		CountryMap.put("bf","");
		CountryMap.put("bg","");
		CountryMap.put("bh","");
		CountryMap.put("bi","");
		CountryMap.put("bj","");
		CountryMap.put("bm","");
		CountryMap.put("bn","");
		CountryMap.put("bo","");
		CountryMap.put("br","");
		CountryMap.put("bs","");
		CountryMap.put("bt","");
		CountryMap.put("bv","");
		CountryMap.put("bw","");
		CountryMap.put("by","");
		CountryMap.put("bz","");
		CountryMap.put("ca","");
		CountryMap.put("cc","");
		CountryMap.put("cf","");
		CountryMap.put("cg","");
		CountryMap.put("ch","");
		CountryMap.put("ci","");
		CountryMap.put("ck","");
		CountryMap.put("cl","");
		CountryMap.put("cm","");
		CountryMap.put("cn","");
		CountryMap.put("co","");
		CountryMap.put("cq","");
		CountryMap.put("cr","");
		CountryMap.put("cu","");
		CountryMap.put("cv","");
		CountryMap.put("cx","");
		CountryMap.put("cy","");
		CountryMap.put("cz","");
		CountryMap.put("de","");
		CountryMap.put("dj","");
		CountryMap.put("dk","");
		CountryMap.put("dm","");
		CountryMap.put("do","");
		CountryMap.put("dz","");
		CountryMap.put("ec","");
		CountryMap.put("ee","");
		CountryMap.put("eg","");
		CountryMap.put("eh","");
		CountryMap.put("es","");
		CountryMap.put("et","");
		CountryMap.put("ev","");
		CountryMap.put("fi","");
		CountryMap.put("fj","");
		CountryMap.put("fk","");
		CountryMap.put("fm","");
		CountryMap.put("fo","");
		CountryMap.put("fr","");
		CountryMap.put("ga","");
		CountryMap.put("gb","");
		CountryMap.put("gd","");
		CountryMap.put("ge","");
		CountryMap.put("gf","");
		CountryMap.put("gh","");
		CountryMap.put("gi","");
		CountryMap.put("gl","");
		CountryMap.put("gm","");
		CountryMap.put("gn","");
		CountryMap.put("gp","");
		CountryMap.put("gr","");
		CountryMap.put("gt","");
		CountryMap.put("gu","");
		CountryMap.put("gw","");
		CountryMap.put("gy","");
		CountryMap.put("hk","");
		CountryMap.put("hm","");
		CountryMap.put("hn","");
		CountryMap.put("hr","");
		CountryMap.put("ht","");
		CountryMap.put("hu","");
		CountryMap.put("id","");
		CountryMap.put("ie","");
		CountryMap.put("il","");
		CountryMap.put("in","");
		CountryMap.put("io","");
		CountryMap.put("iq","");
		CountryMap.put("ir","");
		CountryMap.put("is","");
		CountryMap.put("it","");
		CountryMap.put("jm","");
		CountryMap.put("jo","");
		CountryMap.put("jp","");
		CountryMap.put("ke","");
		CountryMap.put("kg","");
		CountryMap.put("kh","");
		CountryMap.put("ki","");
		CountryMap.put("km","");
		CountryMap.put("kn","");
		CountryMap.put("kp","");
		CountryMap.put("kr","");
		CountryMap.put("kw","");
		CountryMap.put("ky","");
		CountryMap.put("kz","");
		CountryMap.put("la","");
		CountryMap.put("lb","");
		CountryMap.put("lc","");
		CountryMap.put("li","");
		CountryMap.put("lk","");
		CountryMap.put("lr","");
		CountryMap.put("ls","");
		CountryMap.put("lt","");
		CountryMap.put("lu","");
		CountryMap.put("lv","");
		CountryMap.put("ly","");
		CountryMap.put("ma","");
		CountryMap.put("mc","");
		CountryMap.put("md","");
		CountryMap.put("mg","");
		CountryMap.put("mh","");
		CountryMap.put("ml","");
		CountryMap.put("mm","");
		CountryMap.put("mn","");
		CountryMap.put("mo","");
		CountryMap.put("mp","");
		CountryMap.put("mq","");
		CountryMap.put("mr","");
		CountryMap.put("ms","");
		CountryMap.put("mt","");
		CountryMap.put("mv","");
		CountryMap.put("mw","");
		CountryMap.put("mx","");
		CountryMap.put("my","");
		CountryMap.put("mz","");
		CountryMap.put("na","");
		CountryMap.put("nc","");
		CountryMap.put("ne","");
		CountryMap.put("nf","");
		CountryMap.put("ng","");
		CountryMap.put("ni","");
		CountryMap.put("nl","");
		CountryMap.put("no","");
		CountryMap.put("np","");
		CountryMap.put("nr","");
		CountryMap.put("nt","");
		CountryMap.put("nu","");
		CountryMap.put("nz","");
		CountryMap.put("om","");
		CountryMap.put("pa","");
		CountryMap.put("pe","");
		CountryMap.put("pf","");
		CountryMap.put("pg","");
		CountryMap.put("ph","");
		CountryMap.put("pk","");
		CountryMap.put("pl","");
		CountryMap.put("pm","");
		CountryMap.put("pn","");
		CountryMap.put("pr","");
		CountryMap.put("pt","");
		CountryMap.put("pw","");
		CountryMap.put("py","");
		CountryMap.put("qa","");
		CountryMap.put("re","");
		CountryMap.put("ro","");
		CountryMap.put("ru","");
		CountryMap.put("rw","");
		CountryMap.put("sa","");
		CountryMap.put("sb","");
		CountryMap.put("sc","");
		CountryMap.put("sd","");
		CountryMap.put("se","");
		CountryMap.put("sg","");
		CountryMap.put("sh","");
		CountryMap.put("si","");
		CountryMap.put("sj","");
		CountryMap.put("sk","");
		CountryMap.put("sl","");
		CountryMap.put("sm","");
		CountryMap.put("sn","");
		CountryMap.put("so","");
		CountryMap.put("sr","");
		CountryMap.put("st","");
		CountryMap.put("su","");
		CountryMap.put("sy","");
		CountryMap.put("sz","");
		CountryMap.put("tc","");
		CountryMap.put("td","");
		CountryMap.put("tf","");
		CountryMap.put("tg","");
		CountryMap.put("th","");
		CountryMap.put("tj","");
		CountryMap.put("tk","");
		CountryMap.put("tl","");
		CountryMap.put("tm","");
		CountryMap.put("tn","");
		CountryMap.put("to","");
		CountryMap.put("tp","");
		CountryMap.put("tr","");
		CountryMap.put("tt","");
		CountryMap.put("tv","");
		CountryMap.put("tw","");
		CountryMap.put("tz","");
		CountryMap.put("ua","");
		CountryMap.put("ug","");
		CountryMap.put("uk","");
		CountryMap.put("us","");
		CountryMap.put("uy","");
		CountryMap.put("va","");
		CountryMap.put("vc","");
		CountryMap.put("ve","");
	}
	
	public String evaluate(String url, String host) {
		String result = host; // 匹配不上的情况，等于原始值
		try {
			// 如果没有http前缀，hive的自带函数是找不到host的，这里要手工识别
			if (url!=null && url.trim().length()>0 && (host==null || host.trim().equalsIgnoreCase("NULL"))) {
				// 匹配host
				Pattern p = Pattern.compile("(?<=//|)([\\w\\-\u4E00-\u9FA5]+\\.)+[\\w\u4E00-\u9FA5]+");
				Matcher m = p.matcher(url);
				// 有host
				if (m.find()) {
					// 获取host
					host = m.group();
				}
			}
			if (host!=null && host.trim().length()>0){
				// 开始匹配一级域名
				String[] arr = host.split(SPLIT_POINT);
				StringBuffer _tmp = new StringBuffer("");
				if (arr.length>1) { // 数组长度大于1，必须大于等于2
					// 先取HOST的最后一段，判断是国际顶级域名（如.com,.net,.org等）还是国家顶级域名（如.CN，.US等）
					if (TopMap.get(arr[arr.length-1]) !=  null) { // 最后一段是国际顶级域名
						// 如果数组长度等于2，host就是一级域名
						if (arr.length > 2) {		
							// 倒数第二段到末尾开始的部分，就是一级域名
							for (int i = arr.length-2; i < arr.length; i++) {
								_tmp.append(arr[i] + POINT);
							}
							if (_tmp.length()>0) {
								// 去掉末尾的小数点
								_tmp.deleteCharAt(_tmp.length()-1);
								result = _tmp.toString();
							}
						}
					} else if (CountryMap.get(arr[arr.length-1]) !=  null) { // 最后一段是国家顶级域名
						// 判断倒数第二段是否是组织域名（如.com,.net,.org,.edu等）
						if (TopMap.get(arr[arr.length-2]) !=  null) {
							if (arr.length>2) { // 要取值倒数第三段，所以数组长度必须大于2
								// 取倒数第三段到末尾的部分未一级域名
								for (int i = arr.length-3; i < arr.length; i++) {
									_tmp.append(arr[i] + POINT);
								}
								if (_tmp.length()>0) {
									// 去掉末尾的小数点
									_tmp.deleteCharAt(_tmp.length()-1);
									result = _tmp.toString();
								}
							}
						} else {
							// 如果不是组织域名，那么从倒数第二段开始到末尾的部分，就是一级域名
							for (int i = arr.length-2; i < arr.length; i++) {
								_tmp.append(arr[i] + POINT);
							}
							if (_tmp.length()>0) {
								// 去掉末尾的小数点
								_tmp.deleteCharAt(_tmp.length()-1);
								result = _tmp.toString();
							}
						}
					} else { // 最后一段都不是							
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;		
	}
}
