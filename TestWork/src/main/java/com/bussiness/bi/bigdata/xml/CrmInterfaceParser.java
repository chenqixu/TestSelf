package com.bussiness.bi.bigdata.xml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;

/**
 * CRM XML接口文件解析
 * */
public class CrmInterfaceParser {
	public void parserxml(String confxml, String valuexml){
		StringBuffer result = new StringBuffer();
		FileReader fr = null;
		BufferedReader br = null;		
		try{
			fr = new FileReader(confxml);
			br = new BufferedReader(fr);
			StringBuffer xml_c = new StringBuffer();
			String _tmp = "";
			while((_tmp=br.readLine())!=null){
				xml_c.append(_tmp);
			}
			br.close();
			fr.close();
//			System.out.println("[xml_c]"+xml_c.toString());

			fr = new FileReader(valuexml);
			br = new BufferedReader(fr);
			StringBuffer xml_v = new StringBuffer();
			while((_tmp=br.readLine())!=null){
				xml_v.append(_tmp);
			}
			br.close();
			fr.close();
//			System.out.println("[xml_v]"+xml_v.toString());

			ResultXML rx = new ResultXML();
			XMLData xd = new XMLData(xml_c.toString());
			rx.rtFlag = true;
			rx.bXmldata = true;
			rx.xmldata = xd;
			rx.setbFlag(false);
			rx.setRowFlagInfo("property");
			rx.First();
			while(!rx.isEof()){
				String node = rx.getColumnsValue("node");
				String type = rx.getColumnsValue("type");
				String value = rx.getColumnsValue("value");
				System.out.println("[node]"+node+"[type]"+type+"[value]"+value);
				String s = this.getValueByNode(xml_v.toString(), node, type, value);
				System.out.println("[parser]"+s);
				result.append(s);
				rx.Next();
			}
			System.out.println("result:"+result.deleteCharAt(result.length()-1).toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fr!=null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @param xml
	 * @param node 以.分割
	 * @param value 以,分割
	 * */
	public String getValueByNode(String xml, String node, String type, String value){
//		System.out.println("getValueByNode["+node+"][xml]"+xml);
		StringBuffer sb = new StringBuffer();
		ResultXML rx = new ResultXML();
		XMLData xd = new XMLData(xml);
		rx.rtFlag = true;
		rx.bXmldata = true;
		rx.xmldata = xd;
		rx.setbFlag(false);
		
		String[] nodes = node.split("\\.");
		String[] types = type.split("\\.");
		if(nodes.length==0){
			// 没有node
		}else if(nodes.length==1){
			// 判断type是xml还是node，xml可以直接读取，node还需要切换父节点
			if(types[0].equals("node")){
				rx.resetParent().node(nodes[0]).setParentPointer();
			}
			// 获取值，并返回
			sb.append(getValueByNode(rx , value));	
		}else if(nodes.length>1){
			// 获取第二节点的XML，然后从node中截取掉第一节点，继续递归
			String secondnode_xml = "";
			// 如果第二个节点有值
			if(rx.isExistNode(nodes[1])){
				// 第二节点值
				secondnode_xml = rx.node(nodes[1]).getValue();
				// 根据type判断第二节点是XML还是node
				if(types[1].equals("xml")){
					// 是一个标准XML，传入第二节点值
					// 节点递减
					String _tempnode = this.nodeDecrease(nodes);
					// 类型递减
					String _temptype = this.nodeDecrease(types);
					// 递归，并返回
					sb.append(this.getValueByNode(secondnode_xml, _tempnode , _temptype, value));
				}else if(types[1].equals("node")){
					// 是一个子节点，需要传入原有的XML报文
					// 节点递减
					String _tempnode = this.nodeDecrease(nodes);
					// 类型递减
					String _temptype = this.nodeDecrease(types);
					// 递归，并返回
					sb.append(this.getValueByNode(xml, _tempnode , _temptype, value));
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 节点递减
	 * */
	public String nodeDecrease(String[] strs){
		StringBuffer _temp = new StringBuffer();
		for(int i=1;i<strs.length;i++){
			_temp.append(strs[i]+".");
		}
		if(_temp.length()>0){
			_temp.deleteCharAt(_temp.length()-1);
		}
		return _temp.toString();
	}
	
	/**
	 * 在XML中通过node获取value
	 * */
	public String getValueByNode(ResultXML rx, String node){
		StringBuffer sb = new StringBuffer();
		String[] nodes = node.split(",");
		for(int i=0;i<nodes.length;i++){
			String _tmpvalue = "";
			if(rx.isExistNode(nodes[i])){
				_tmpvalue = rx.node(nodes[i]).getValue();
			}
			sb.append(_tmpvalue+Constants.COLUMN_SPLIT);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
//		new CrmInterfaceParser().parserxml("H:/Work/Git/TestSelf/TestWork/src/main/resources/conf/read2.xml"
//				,"d:/Work/ETL/xml解析/71202-关于CRM接口表intf_biz.interboss_operation抽取及CLOB内容解析的处理/报文.xml");
		new CrmInterfaceParser().parserxml("H:/Work/Git/TestSelf/TestWork/src/main/resources/conf/read3.xml"
				,"j:/Work/海南/海南MRO/xml接口相关资料/test.xml");
//		String xml = "<?xml version='1.0' encoding='UTF-8'?><prodOrderReq><IDType>01</IDType><IDItemRange>13960435498</IDItemRange><OprNumb>UMMPBIP3A21120150901192148264186</OprNumb><BizType>69</BizType><IdentCode>59100020150901191510396945590645</IdentCode><ProdunctInfo><ProductType>01</ProductType><ProductId>1000210077</ProductId><OprCode>01</OprCode><EffectiveType>01</EffectiveType></ProdunctInfo></prodOrderReq>";
//		String xml = "<?xml version='1.0' encoding='UTF-8'?><InterBOSS><Version>0100</Version><TestFlag>0</TestFlag><BIPType><BIPCode>BIP3A211</BIPCode><ActivityCode>T3000214</ActivityCode><ActionCode>1</ActionCode></BIPType><RoutingInfo><OrigDomain>UMMP</OrigDomain><RouteType>01</RouteType><Routing><HomeDomain>BOSS</HomeDomain><RouteValue>13960435498</RouteValue></Routing></RoutingInfo><TransInfo><SessionID>6900P2B77420150901192149517395</SessionID><TransIDO>6900P2B77420150901192149517395</TransIDO><TransIDOTime>20150901192148</TransIDOTime><TransIDH>308988998424</TransIDH><TransIDHTime>20150901192320</TransIDHTime></TransInfo><SNReserve><TransIDC>9980111120150901190658904280153</TransIDC><ConvID>157a4ba7-bc7b-4e29-8c8d-3c718f8b36b3</ConvID><CutOffDay>20150901</CutOffDay><OSNTime>20150901190658</OSNTime><OSNDUNS>9980</OSNDUNS><HSNDUNS>5910</HSNDUNS><MsgSender>0216</MsgSender><MsgReceiver>5911</MsgReceiver><Priority>3</Priority><ServiceLevel>1</ServiceLevel></SNReserve><SvcCont><![CDATA[<?xml version='1.0' encoding='UTF-8'?><prodOrderReq><IDType>01</IDType><IDItemRange>13960435498</IDItemRange><OprNumb>UMMPBIP3A21120150901192148264186</OprNumb><BizType>69</BizType><IdentCode>59100020150901191510396945590645</IdentCode><ProdunctInfo><ProductType>01</ProductType><ProductId>1000210077</ProductId><OprCode>01</OprCode><EffectiveType>01</EffectiveType></ProdunctInfo></prodOrderReq>]]></SvcCont></InterBOSS>";
//		ResultXML rx = new ResultXML();
//		XMLData xd = new XMLData(xml);
//		rx.rtFlag = true;
//		rx.bXmldata = true;
//		rx.xmldata = xd;
//		rx.setbFlag(false);
//		System.out.println(rx.node("TestFlag").getValue());
	}
}
