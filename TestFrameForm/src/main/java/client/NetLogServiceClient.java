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

	// Ϊ�յ����ͽӿڶ���
	public static final String NULL_VALUE = "-999999";
	
	private String seldata = "1";//��ѯ���ݿ� 1:greenplum; 2:hbase;
	private String sUrl = "";//�����ַ �����ݿ��л��
	private String ClientId = "";//��ȨID �����ݿ��л��
	private String PassWord = "";//��Ȩ���� �����ݿ��л��
	private String targetnamespace = "";//���������ռ��ַ
	private boolean flag = false;//�Ƿ��ѯ����
	private StringBuffer error = new StringBuffer();//��������
	
	/**
	 * ���캯��
	 * */
	public NetLogServiceClient() {
	}

	public void setSeldata(String seldata) {
		this.seldata = seldata;
		this.getConf();//�������
	}

	public String getSeldata() {
		return seldata;
	}
	
	/**
	 * �������
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
	 * ��ѯhbase����
	 * */
	public synchronized NetLogClientBean queryHbaseData(NgReqBean requestBean,String start,String pagecount){
		NetLogClientBean ncb = new NetLogClientBean();
		List<String> dataResult = new ArrayList<String>();
		List<String> gatherResult = new ArrayList<String>();
		String heard = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\""+this.targetnamespace+"\">";
		CallWebService cws = new CallWebService();
		if(this.flag){//��ѯ���óɹ�
			String strxml = cws.setSendXML(heard, this.ClientId ,this.PassWord, requestBean, start, pagecount);
			System.out.println("NetLogServiceClient queryHbaseData request url:"+this.sUrl);
			System.out.println("NetLogServiceClient queryHbaseData request xml:"+strxml);
			String resultxml = "";//���xml
			try{
				resultxml = cws.doAction("POST", this.sUrl, strxml.getBytes());
				System.out.println("NetLogServiceClient queryHbaseData resultxml xml:"+resultxml);
				
				if(resultxml.length()>0){//��ѯ�н��
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
					if(RespResult.equals("0")){//��ѯ�ɹ�
						ncb.setRespCode(RespResult);//����״̬
						ncb.setRespDesc(RespDesc);
						
						rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").node("RespData").setParentPointer();
				        rx.setRowFlagInfo("NgRespBean");
				        rx.First();
				        if(rx.isEof()){//û�н��
				        	ncb.setRespDesc("��ѯ�ɹ�,��û�н����");
				        }else{//�н��
				        	int i_first = 0;//������
					        while (!rx.isEof()) {
					        	String str = rx.getColumnsValue("content");
					        	dataResult.add(str);//��ϸ����
					        	if(i_first==0){//������Ϣ
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
										rxxRow.Next();//��һ��
									}
									gatherResult.add("cmnet����,"+String.valueOf(cmnet_total_bytes)+",cmnetʱ��,"+String.valueOf(cmnet_total_times));
									gatherResult.add("cmwap����,"+String.valueOf(cmwap_total_bytes)+",cmwapʱ��,"+String.valueOf(cmwap_total_times));
					        	}
					        	rx.Next();
					        	i_first++;//������++
					        }
					        //���totalCount
					        rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").setParentPointer();
					        ncb.setTotalCount(rx.node("totalCount").getValue());
				        }
				        ncb.setDetailList(dataResult);//�����嵥���
				        ncb.setGatherList(gatherResult);//���û��ܽ��
					}else{//��ѯʧ��
						ncb.setRespCode("-1");
						ncb.setRespDesc("��ѯʧ��:"+RespDesc);
					}
				}else{//��ѯû���
					ncb.setRespCode("-1");
					ncb.setRespDesc("�ӿ�û�з�������");
				}
			}catch(Exception e){
				ncb.setRespCode("-1");
				ncb.setRespDesc("��ѯʧ��:"+e.toString());
			}
		}else{//��ѯ����ʧ��
			ncb.setRespCode("-1");
			ncb.setRespDesc("��ѯ����bishow.cfg_gn_serviceʧ��,"+this.error.toString());
		}
		return ncb;
	}
	
	/**
	 * ���������Excel
	 * */
	public synchronized InputStream expExcel(String sheetName, List<List<String>> GnDetailList){
		WritableWorkbook wmm = null;
		ByteArrayOutputStream os = null;
		try {
			//������д���Excel������
	        os = new ByteArrayOutputStream();
            //wmm = Workbook.createWorkbook(new File(filePath));//�����ļ�������
	        wmm = Workbook.createWorkbook(os);
        } catch (IOException e) {
            //log.debug("�����µ�excel�ļ������쳣");
            e.printStackTrace();
            return null;
        }
        //����������
        if (sheetName == null || sheetName.equals(""))
            sheetName = "sheet";
        WritableSheet ws = wmm.createSheet(sheetName, 0);
        String titleStr = "";
        //����
        String[] columns = {"�ֻ�����", "�ն��ͺ�", "�ն�����", "APN", "��������","��ʼʱ��", "����ʱ��", "��������(K)", "��������(K)", "���ʵ�ַ","Ӧ������"};
        try {
            //�����ʽ
            WritableFont writableFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_bt = new WritableCellFormat(writableFont);
            writableCellFormat_bt.setAlignment(jxl.format.Alignment.CENTRE);//����
            writableCellFormat_bt.setVerticalAlignment(VerticalAlignment.CENTRE);//����
            writableCellFormat_bt.setBackground(Colour.LIGHT_GREEN);//����ɫ
            writableCellFormat_bt.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
            writableCellFormat_bt.setWrap(true);//�Զ�����
	        //��ͨ�ı�
	        WritableFont writableFont3 = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
	                UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
	        WritableCellFormat writableCellFormat_nr = new WritableCellFormat(writableFont3);
	        writableCellFormat_nr.setAlignment(jxl.format.Alignment.LEFT);//���Ҷ���
	        writableCellFormat_nr.setVerticalAlignment(VerticalAlignment.CENTRE);//���¶���
	        writableCellFormat_nr.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
            //���ָ�ʽ
            WritableFont writableFont_num = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat2 = null;
            jxl.write.NumberFormat nf = new jxl.write.NumberFormat("#,##0");//���ָ�ʽ
            writableCellFormat2 = new WritableCellFormat(writableFont_num, nf);
            writableCellFormat2.setAlignment(jxl.format.Alignment.CENTRE);//����
            writableCellFormat2.setVerticalAlignment(VerticalAlignment.CENTRE);//����
            writableCellFormat2.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
	        
        	//��ӱ���
        	for (int i = 0; i < columns.length; i++) {
            	titleStr = columns[i];
            	Label labels = new Label(i, 0, titleStr, writableCellFormat_bt);
            	ws.addCell(labels);
        	}
	        //�������
	        jxl.write.Label labelcontent;
            jxl.write.Number numcontent;
	        for (int i = 0; i < GnDetailList.size(); i++) {//��2�п�ʼ��������
	        	//System.out.println("i:"+i+"GnDetailList.get("+i+")"+GnDetailList.get(i));
	            for (int j = 0; j < 11; j++) {//��
	                // Label(�к�,�к� ,���� )
	                String tempStr = GnDetailList.get(i).get(j).toString();
	                if (tempStr != null && tempStr.equals(NULL_VALUE)) {
	                    tempStr = " ";
	                }
	                if ( j==0 || j==7 || j==8){//�ֻ�����,����
	                	numcontent = new jxl.write.Number(j, i+1, Long.valueOf(tempStr), writableCellFormat_nr);
	                	ws.addCell(numcontent);
	                }else{//����
	                	labelcontent = new jxl.write.Label(j, i+1, tempStr, writableCellFormat_nr);
	            		ws.addCell(labelcontent);
	            	}
	            }
	        }
	        //�����и�
        	//ws.setRowView(0, 550);
            //�����п�
            for(int i=0;i<ws.getColumns();i++){
            	ws.setColumnView(i, 14);
            }
	        //����ڴ�
            System.gc();
	        //����
            wmm.write();
            ws = null;
            //�ر�
            wmm.close();
            return new ByteArrayInputStream(os.toByteArray());
        } catch (RowsExceededException e) {
            e.printStackTrace();
            return null;
            //log.debug("�����д���" + e.getMessage());
        } catch (WriteException e1) {
            //log.debug("����д�����" + e.getMessage());
            e1.printStackTrace();
            return null;
        } catch (Exception e2) {
            //log.debug("����������" + e1.getMessage());
            e2.printStackTrace();
            return null;
        }
	}
}
