import java.util.regex.Pattern;

import util.ResultXML;
import util.XMLData;

public class XmlTest {
	public static void main(String[] args) {
//		String xmlstr = "<?xml version='1.0' encoding='gbk'?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns1:qryNetLogListResponse xmlns:ns1=\"http://service.webservice.bi.newland.com/\">	<message><HeaderResp><RespResult>0</RespResult><RespTime>20140225163308051</RespTime><RespCode>0</RespCode><RespDesc>成功</RespDesc></HeaderResp>	<BodyResp><RespData><NgRespBean><content>14759737018,上海贝尔 ASB TR9501,CMNET,,2014-02-23 12:30:33.071,2014-02-23 12:30:33.071,40.0,0.0,111.13.100.91,3G,网站</content><GatherBean><apn>CMWAP</apn><allbytes>0.000</allbytes><allDelaytime>0</allDelaytime><service_name></service_name></GatherBean><GatherBean><apn>CMNET</apn><allbytes>91640.000</allbytes><allDelaytime>721.089</allDelaytime><service_name></service_name></GatherBean></NgRespBean><NgRespBean><content>14759737018,上海贝尔 ASB TR9501,CMNET,,2014-02-23 12:30:33.091,2014-02-23 12:30:33.211,100.0,0.0,224.0.0.252,3G,</content></NgRespBean><NgRespBean><content>14759737018,上海贝尔 ASB TR9501,CMNET,,2014-02-23 12:30:33.231,2014-02-23 12:30:33.331,104.0,0.0,224.0.0.252,3G,</content></NgRespBean><NgRespBean><content>14759737018,上海贝尔 ASB TR9501,CMNET,,2014-02-23 12:30:33.291,2014-02-23 12:30:33.401,122.0,0.0,224.0.0.252,3G,</content></NgRespBean><NgRespBean><content>14759737018,上海贝尔 ASB TR9501,CMNET,,2014-02-23 12:30:33.571,2014-02-23 12:30:37.565,234.0,0.0,218.207.212.51,3G,DNS</content></NgRespBean><NgRespBean><content>14759737018,上海贝尔 ASB TR9501,CMNET,,2014-02-23 12:30:33.611,2014-02-23 12:30:33.711,122.0,0.0,224.0.0.252,3G,</content></NgRespBean><NgRespBean><content>14759737018,TR9501,CMNET,,2014-02-23 12:30:33.471,2014-02-23 12:30:33.474,0.0,0.0,h.conf.f.360.cn,3G,DNS</content></NgRespBean><NgRespBean><content>14759737018,TR9501,CMNET,,2014-02-23 12:30:33.571,2014-02-23 12:30:37.565,0.0,0.0,a???^??H0?ij??.???u?????&amp;+??&quot;??,3G,DNS</content></NgRespBean></RespData><totalCount>120</totalCount><firstRowKey></firstRowKey><lastRowKey></lastRowKey></BodyResp></message></ns1:qryNetLogListResponse></soap:Body></soap:Envelope>";
//		
//		java.util.regex.Pattern INVALID_XML_CHARS = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\uD800\uDC00-\uDBFF\uDFFF]");
//		xmlstr = INVALID_XML_CHARS.matcher(xmlstr).replaceAll(" ");
//		System.out.println(xmlstr);
//		ResultXML rx = new ResultXML();
//		XMLData xd = new XMLData(xmlstr);
//		rx.rtFlag = true;
//		rx.bXmldata = true;
//		rx.xmldata = xd;
//		rx.setbFlag(false);
		System.out.println(java.net.URLDecoder.decode("&phpp=ANDROID_360&phpl=ZH_CN&pvc=1.4.1&pvb=2014-01-13%2015%3A23%3A47"));
		System.out.println(java.net.URLDecoder.decode("&phpp=ANDROID_DUOKU&phpl=ZH_CN&pvc=1.4.0&pvb=2013-12-02%2011%3A19%3A12"));
		System.out.println(java.net.URLDecoder.decode("&phpp=ANDROID&phpl=ZH_CN&pvc=1.4.1&pvb=2014-01-10%2017%3A39%3A16"));
	}
}
