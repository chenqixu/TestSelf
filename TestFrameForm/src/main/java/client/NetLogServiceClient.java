package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import util.CallWebService;
import util.ResultXML;
import util.XMLData;

import bean.NetLogClientBean;
import bean.NgReqBean;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;

public class NetLogServiceClient {

	// 为空的整型接口定义
	public static final String NULL_VALUE = "-999999";
	
	private String seldata = "1";//查询数据库 1:greenplum; 2:hbase;
	private String sUrl = "";//服务地址 从数据库中获得
	private String ClientId = "";//鉴权ID 从数据库中获得
	private String PassWord = "";//鉴权密码 从数据库中获得
	private String targetnamespace = "";//服务命名空间地址
	private boolean flag = false;//是否查询配置
	private StringBuffer error = new StringBuffer();//错误描述
	
	/**
	 * 构造函数
	 * */
	public NetLogServiceClient() {
	}

	public void setSeldata(String seldata) {
		this.seldata = seldata;
		this.getConf();//获得配置
	}

	public String getSeldata() {
		return seldata;
	}
	
	/**
	 * 获得配置
	 * */
	public void getConf() {
		if(this.seldata.equals("1")){//greenplum
			this.sUrl = //"http://10.46.61.153:9015/nlisb_tool/ws/NgService";
				"http://10.1.0.168:5005/bi_svc/services/NgService";
			this.ClientId = "9991089";
			this.PassWord = "bXb8b9aZb0blb4ajbda2btb0akaPbFbkccaKb7aqaubhaGaFb3bdbMbRbzb1aTbJ";
			this.targetnamespace = "http://ng.busiapp.webservice.bi.newland.com/";
			this.flag = true;
		}else if(this.seldata.equals("2")){//hbase
			this.sUrl = //"http://10.46.219.60:8080/bi_bigdata_svc/services/NgService";
				"http://10.1.4.53:8008/bi_bigdata_svc/services/NgService";
			this.ClientId = "9990058";
			this.PassWord = "bSb3b4aUbvbgbzaeaYaxbobvafaKbAbfbXaFb2alapbcaBaAbyaYbHbMbubwaObE";
			this.targetnamespace = "http://service.webservice.bi.newland.com/";
			this.flag = true;
		}
	}
	
	/**
	 * 查询hbase数据
	 * */
	public synchronized NetLogClientBean queryHbaseData(NgReqBean requestBean,String start,String pagecount){
		NetLogClientBean ncb = new NetLogClientBean();
		List<String> dataResult = new ArrayList<String>();
		List<String> gatherResult = new ArrayList<String>();
		String heard = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\""+this.targetnamespace+"\">";
		CallWebService cws = new CallWebService();
		if(this.flag){//查询配置成功
			String strxml = cws.setSendXML(heard, this.ClientId ,this.PassWord, requestBean, start, pagecount);
			System.out.println("NetLogServiceClient queryHbaseData request url:"+this.sUrl);
			System.out.println("NetLogServiceClient queryHbaseData request xml:"+strxml);
			String resultxml = "";//结果xml
			try{
				resultxml = cws.doAction("POST", this.sUrl, strxml.getBytes());
				System.out.println("NetLogServiceClient queryHbaseData resultxml xml:"+resultxml);
				
				if(resultxml.length()>0){//查询有结果
					ResultXML rx = new ResultXML();
					StringBuffer xml = new StringBuffer();
					xml.append("<?xml version=\"1.0\"  encoding='UTF-8'?>");
					xml.append(resultxml);
					XMLData xd = new XMLData(xml.toString());
					rx.rtFlag = true;
					rx.bXmldata = true;
					rx.xmldata = xd;
					rx.setbFlag(false);
					
					rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("HeaderResp").setParentPointer();
					String RespResult = rx.node("RespResult").getValue();
					String RespDesc = rx.node("RespDesc").getValue();
					if(RespResult.equals("0")){//查询成功
						ncb.setRespCode(RespResult);//设置状态
						ncb.setRespDesc(RespDesc);
						
						rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").node("RespData").setParentPointer();
				        rx.setRowFlagInfo("NgRespBean");
				        rx.First();
				        if(rx.isEof()){//没有结果
				        	ncb.setRespDesc("查询成功,但没有结果。");
				        }else{//有结果
				        	int i_first = 0;//计数器
					        while (!rx.isEof()) {
					        	String str = rx.getColumnsValue("content");
					        	dataResult.add(str);//详细数据
					        	if(i_first==0){//汇总信息
					        		ResultXML rxxRow = rx.GetColumnsResultXML();
						        	rxxRow.setbFlag(false);
									rxxRow.setRowFlagInfo("GatherBean");
									rxxRow.Frist();
									  
									String cmwap_total_bytes = "0";//wap
									String cmnet_total_bytes = "0";//net
									String cmwap_total_times = "0";//wap time
									String cmnet_total_times = "0";//net time
									while(!rxxRow.isEof()){
										if(rxxRow.getColumnsValue("apn").equalsIgnoreCase("cmwap") || rxxRow.getColumnsValue("apn").equalsIgnoreCase("CMWAP")){
											cmwap_total_bytes = rxxRow.getColumnsValue("allbytes");
											cmwap_total_times = rxxRow.getColumnsValue("allDelaytime");
										}else if(rxxRow.getColumnsValue("apn").equalsIgnoreCase("cmnet") || rxxRow.getColumnsValue("apn").equalsIgnoreCase("CMNET")){
											cmnet_total_bytes = rxxRow.getColumnsValue("allbytes");
											cmnet_total_times = rxxRow.getColumnsValue("allDelaytime");
										}
										rxxRow.Next();//下一个
									}
									gatherResult.add("cmnet流量,"+String.valueOf(cmnet_total_bytes)+",cmnet时长,"+String.valueOf(cmnet_total_times));
									gatherResult.add("cmwap流量,"+String.valueOf(cmwap_total_bytes)+",cmwap时长,"+String.valueOf(cmwap_total_times));
					        	}
					        	rx.Next();
					        	i_first++;//计数器++
					        }
					        //输出totalCount
					        rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").setParentPointer();
					        ncb.setTotalCount(rx.node("totalCount").getValue());
				        }
				        ncb.setDetailList(dataResult);//设置清单结果
				        ncb.setGatherList(gatherResult);//设置汇总结果
					}else{//查询失败
						ncb.setRespCode("-1");
						ncb.setRespDesc("查询失败:"+RespDesc);
					}
				}else{//查询没结果
					ncb.setRespCode("-1");
					ncb.setRespDesc("接口没有返回数据");
				}
			}catch(Exception e){
				ncb.setRespCode("-1");
				ncb.setRespDesc("查询失败:"+e.toString());
			}
		}else{//查询配置失败
			ncb.setRespCode("-1");
			ncb.setRespDesc("查询配置bishow.cfg_gn_service失败,"+this.error.toString());
		}
		return ncb;
	}
	
	/**
	 * 导出结果到Excel
	 * */
	public synchronized InputStream expExcel(String sheetName, List<List<String>> GnDetailList){
		WritableWorkbook wmm = null;
		ByteArrayOutputStream os = null;
		try {
			//创建可写入的Excel工作薄
	        os = new ByteArrayOutputStream();
            //wmm = Workbook.createWorkbook(new File(filePath));//根据文件名创建
	        wmm = Workbook.createWorkbook(os);
        } catch (IOException e) {
            //log.debug("生成新的excel文件出现异常");
            e.printStackTrace();
            return null;
        }
        //创建工作表
        if (sheetName == null || sheetName.equals(""))
            sheetName = "sheet";
        WritableSheet ws = wmm.createSheet(sheetName, 0);
        String titleStr = "";
        //标题
        String[] columns = {"手机号码", "终端型号", "终端类型", "APN", "网络类型","开始时间", "结束时间", "上行流量(K)", "下行流量(K)", "访问地址","应用名称"};
        try {
            //标题格式
            WritableFont writableFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_bt = new WritableCellFormat(writableFont);
            writableCellFormat_bt.setAlignment(jxl.format.Alignment.CENTRE);//对齐
            writableCellFormat_bt.setVerticalAlignment(VerticalAlignment.CENTRE);//对齐
            writableCellFormat_bt.setBackground(Colour.LIGHT_GREEN);//背景色
            writableCellFormat_bt.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
            writableCellFormat_bt.setWrap(true);//自动换行
	        //普通文本
	        WritableFont writableFont3 = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
	                UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
	        WritableCellFormat writableCellFormat_nr = new WritableCellFormat(writableFont3);
	        writableCellFormat_nr.setAlignment(jxl.format.Alignment.LEFT);//左右对齐
	        writableCellFormat_nr.setVerticalAlignment(VerticalAlignment.CENTRE);//上下对齐
	        writableCellFormat_nr.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
            //数字格式
            WritableFont writableFont_num = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat2 = null;
            jxl.write.NumberFormat nf = new jxl.write.NumberFormat("#,##0");//数字格式
            writableCellFormat2 = new WritableCellFormat(writableFont_num, nf);
            writableCellFormat2.setAlignment(jxl.format.Alignment.CENTRE);//对齐
            writableCellFormat2.setVerticalAlignment(VerticalAlignment.CENTRE);//对齐
            writableCellFormat2.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
	        
        	//添加标题
        	for (int i = 0; i < columns.length; i++) {
            	titleStr = columns[i];
            	Label labels = new Label(i, 0, titleStr, writableCellFormat_bt);
            	ws.addCell(labels);
        	}
	        //添加内容
	        jxl.write.Label labelcontent;
            jxl.write.Number numcontent;
	        for (int i = 0; i < GnDetailList.size(); i++) {//第2行开始才是数据
	        	//System.out.println("i:"+i+"GnDetailList.get("+i+")"+GnDetailList.get(i));
	            for (int j = 0; j < 11; j++) {//列
	                // Label(列号,行号 ,内容 )
	                String tempStr = GnDetailList.get(i).get(j).toString();
	                if (tempStr != null && tempStr.equals(NULL_VALUE)) {
	                    tempStr = " ";
	                }
	                if ( j==0 || j==7 || j==8){//手机号码,流量
	                	numcontent = new jxl.write.Number(j, i+1, Long.valueOf(tempStr), writableCellFormat_nr);
	                	ws.addCell(numcontent);
	                }else{//其他
	                	labelcontent = new jxl.write.Label(j, i+1, tempStr, writableCellFormat_nr);
	            		ws.addCell(labelcontent);
	            	}
	            }
	        }
	        //设置行高
        	//ws.setRowView(0, 550);
            //设置列宽
            for(int i=0;i<ws.getColumns();i++){
            	ws.setColumnView(i, 14);
            }
	        //清空内存
            System.gc();
	        //保存
            wmm.write();
            ws = null;
            //关闭
            wmm.close();
            return new ByteArrayInputStream(os.toByteArray());
        } catch (RowsExceededException e) {
            e.printStackTrace();
            return null;
            //log.debug("数据行错误：" + e.getMessage());
        } catch (WriteException e1) {
            //log.debug("数据写入错误：" + e.getMessage());
            e1.printStackTrace();
            return null;
        } catch (Exception e2) {
            //log.debug("数据填充错误：" + e1.getMessage());
            e2.printStackTrace();
            return null;
        }
	}
}
