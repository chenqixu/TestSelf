import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import bean.KeepNetLogOSSBean;
import bean.KeepNetLogOSSData;
import bean.KeepNetLogOSSHttpBean;
import bean.KeepNetLogOSSIpBean;
import bean.KeepNetLogOSSResultBean;
import bean.KeepNetLogOSSWlanBean;

public class HiveTest {
	
	//���Կ��� trueΪ����
	private boolean test_flag = true;
	
	public void setTest_flag(boolean test_flag) {
		this.test_flag = test_flag;
	}

	public HiveTest(){		
	}
		
    /**
     * �������ַ�����ָ�����ڸ�ʽ�����ַ���
     * */
	private String formatDateString(String date, String origFormat, String destFormat) throws Exception {
    	SimpleDateFormat sf1 = new SimpleDateFormat(origFormat);
    	Date d = null;
		try {
			d = sf1.parse(date);
		} catch (ParseException e) {
			System.out.println("KeepNetLogOSSClient formatDateString error:"+e.toString());
			e.printStackTrace();
			throw e;//�׳��쳣
		}
    	SimpleDateFormat sf2 = new SimpleDateFormat(destFormat);
    	return sf2.format(d);
    }
	
	/**
	 * �жϴ���Ŀ�ʼ�ͽ���ʱ���Ƿ����1��Сʱ,����ʹ��Сʱ����,������ʹ�������
	 * */
	private boolean defSdateAndEdate(String start_date, String end_date) throws Exception {
		boolean hours = false;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date beginDate = format.parse(start_date);
		java.util.Date endDate= format.parse(end_date);
		//long day=(endDate.getTime()-beginDate.getTime())/(60*1000);
		if(beginDate.getHours() == endDate.getHours()){//����ͬһʱ��
			hours = true;
		}
		return hours;
	}
	
	/**
	 * ��ѯ
	 * */
	public KeepNetLogOSSData qryKeepNetLog(KeepNetLogOSSBean requestBean) throws Exception {
		KeepNetLogOSSData knlod = new KeepNetLogOSSData();//���ؽ��
		int dbType = 0;//�������� 1:hive 2:bishow
		StringBuffer sbSQL = new StringBuffer("");//��ѯsql
		List<KeepNetLogOSSClientThread> listThread = new ArrayList<KeepNetLogOSSClientThread>();//���ڴ���߳�
		List wlan_resultList = null;//wlan���
		List ip_resultList = null;//ip���
		List http_resultList = null;//http���
		List<List<String>> qryresultList = null;//�����Ĳ�ѯ���
		try{
			if(requestBean!=null){
				if(requestBean.getRattype().equals("99")){//WLAN �ƶ����ܲ�ѯ��������� oracle
					dbType = 2;
					
					//wlan
	        		sbSQL.delete(0, sbSQL.length());//���sql
					sbSQL.append(" select");
					sbSQL.append(" to_char(starttime_s,'yyyy-MM-dd HH24:mi:ss')");
					sbSQL.append(" ,to_char(starttime_e,'yyyy-MM-dd HH24:mi:ss')");
					sbSQL.append(" ,telnumber");
					sbSQL.append(" ,mac");
					sbSQL.append(" ,ac_home_county");
					sbSQL.append(" ,user_home_county");
					sbSQL.append(" ,acip");
					sbSQL.append(" ,nasid");
					sbSQL.append(" ,to_char(up,'FM9999999999999990.00')");
					sbSQL.append(" ,to_char(down,'FM9999999999999990.00')");
					sbSQL.append(" ,nvl(user_gw_ip,' ') ");
					sbSQL.append(" from keepnetlogoss_wlan_log wlan ");
					sbSQL.append(" where ");
					sbSQL.append(appendWhereSql(requestBean, "wlan"));
					sbSQL.append(" and rownum<10000 ");//���ֻ�����10000��
					sbSQL.append(" order by starttime_s ");
					/*wlan�߳�*/
					KeepNetLogOSSClientThread keepqueryThread1 = new KeepNetLogOSSClientThread(sbSQL.toString(), dbType, "wlan", requestBean.getTelnumber());
					keepqueryThread1.setTest_flag(this.test_flag);
					keepqueryThread1.start();
	                listThread.add(keepqueryThread1);
				}else{//2G/3G HIVE
					dbType = 1;
					
					//ip
	        		sbSQL.delete(0, sbSQL.length());//���sql
					sbSQL.append("select ");
					sbSQL.append(" ip.sid ");//���ں�http��ipsid����
					sbSQL.append(" ,ip.telnumber ");//�ֻ�����
					sbSQL.append(" ,'' ");//�û�NAT����IP��ַ
					sbSQL.append(" ,ip.userip ");//�û�˽��IP��ַ
					sbSQL.append(" ,ip.usrsport ");//NAT��Դ�˿�
					sbSQL.append(" ,ip.usrdip ");//Ŀ��IP
					sbSQL.append(" ,ip.usrdport ");//Ŀ�Ķ˿�
					sbSQL.append(" ,ip.starttime ");//����ʱ��
					sbSQL.append(" ,ip.lasttime ");//����ʱ��
					sbSQL.append(" ,ip.apn ");//APN
					sbSQL.append(" ,case when ip.rattype=1 then '3G' when ip.rattype=2 then '2G' else '' end ");//2g/3g
					sbSQL.append(" from ip_rc ip ");
					sbSQL.append(" where ");
					sbSQL.append(appendWhereSql(requestBean, "ip"));
					sbSQL.append(" order by ip.starttime desc ");
					//sbSQL.append(" limit 20000 ");//���ֻ�����10000��					
					/*ip�߳�*/
					KeepNetLogOSSClientThread keepqueryThread1 = new KeepNetLogOSSClientThread(sbSQL.toString(), dbType, "ip", requestBean.getTelnumber());
					keepqueryThread1.setTest_flag(this.test_flag);
					keepqueryThread1.start();
	                listThread.add(keepqueryThread1);
	                
	        		//http
	        		sbSQL.delete(0, sbSQL.length());//���sql
	        		sbSQL.append("select ");
	        		sbSQL.append(" http.ipsid ");//���ں�ip��sid����
	        		sbSQL.append(" ,http.url ");//����URL
	        		sbSQL.append(" ,http.starttime ");//����ʱ��
	        		sbSQL.append(" ,http.lasttime ");//����ʱ��
	        		sbSQL.append(" from http_rc http ");
	        		sbSQL.append(" where ");
	        		sbSQL.append(appendWhereSql(requestBean, "http"));
	        		sbSQL.append(" order by http.starttime desc ");
	        		//sbSQL.append(" limit 30000 ");//���ֻ�����10000��	                
	        		/*http�߳�*/
					KeepNetLogOSSClientThread keepqueryThread2 = new KeepNetLogOSSClientThread(sbSQL.toString(), dbType, "http", requestBean.getTelnumber());
					keepqueryThread2.setTest_flag(this.test_flag);
					keepqueryThread2.start();
	                listThread.add(keepqueryThread2);
				}
				
				/*�ж϶��߳��Ƿ�ִ�����*/
		        int isflag = listThread.size();
		        if(isflag==1){//bishow wlan
			        while(isflag>0){
				        Thread.sleep(1);//1��ˢ��һ�ζ��߳�
				        for(int i=0;i<listThread.size();i++){
				        	if(listThread.get(i).isFlag()){//�߳�ִ�����
				        		listThread.get(i).setFlag(false);//����
				        		//waln���
				        		wlan_resultList = listThread.get(i).getResultList();
				        		isflag--;//��������1,ֱ��Ϊ0,ѭ��ֹͣ
				        	}
				        }
			        }
			        //���˹���ip ��List<Bean>���List<List<String>>			        
			        qryresultList = RowMapper(wlan_resultList, requestBean);
		        }else{//hive ��Ҫ����ip,http
			        while(isflag>0){
				        Thread.sleep(1);//1��ˢ��һ�ζ��߳�
				        for(int i=0;i<listThread.size();i++){
				        	if(listThread.get(i).isFlag()){//�߳�ִ�����
				        		listThread.get(i).setFlag(false);//����
				        		if(listThread.get(i).getTag_name().equals("ip")){
				        			//ip���
				        			ip_resultList = listThread.get(i).getResultList();
				        		}else if(listThread.get(i).getTag_name().equals("http")){
				        			//http���
				        			http_resultList = listThread.get(i).getResultList();
				        		}
				        		isflag--;//��������1,ֱ��Ϊ0,ѭ��ֹͣ
				        	}
				        }
			        }
			        //��ѯ���,����ip���http���join,������http.url��ip.����ip
			        qryresultList = leftJoinIpHttp(ip_resultList, http_resultList, requestBean);
		        }
		        //��ѯ�߳̽���,���ý��
				knlod.setDetailList(qryresultList);
				knlod.setRespCode("0");
				knlod.setRespDesc("�ɹ�");
				knlod.setTotalCount(String.valueOf(qryresultList.size()));
				//����Excel
				if(qryresultList!=null &&qryresultList.size()>0){
					expExcel(dbType, requestBean.getFilename(), requestBean.getTelnumber(), qryresultList);
				}
			}else{
				knlod.setRespCode("-1");
				knlod.setRespDesc("��ѯʧ��:û�д������");
			}
		} catch (Exception e) {
			System.out.println("KeepNetLogOSSClient qryKeepNetLog error:"+e.toString());
			e.printStackTrace();
			knlod.setRespCode("-1");
			knlod.setRespDesc("��ѯʧ��:"+e.toString());
		}
		return knlod;
	}
	
	/**
	 * ip left join http
	 * */
	public List leftJoinIpHttp(List deallist1, List deallist2) {
		List list = null;
		List<List<String>> newlist = new Vector<List<String>>();
    	for(int i=0;i<deallist1.size();i++){//��ipѭ��
    		list = null;
    		for(int j=0;j<deallist2.size();j++){//ѭ��http,���sid��ipsid��������,������url,stime,etime
    			if(((KeepNetLogOSSIpBean)deallist1.get(i)).getSid().equals(
    					((KeepNetLogOSSHttpBean)deallist2.get(j)).getIpsid())){
    				KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    				list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
    				newlist.add(list);
    			}
    		}
    		if(list!=null && list.size()>0){//������������,�����Ѿ�������
    		}else{//������������
    			KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    			list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
    			newlist.add(list);
    		}    			
    	}    	
		return newlist;
	}
		
	/**
	 * ��������bean��װ��ѯ����
	 * */
	public String appendWhereSql(KeepNetLogOSSBean requestBean, String mode) throws Exception {
		StringBuffer sql = new StringBuffer("");
		if(requestBean!=null){
			if(requestBean.getRattype().equals("99")){//WLAN �ƶ����ܲ�ѯ��������� oracle				
				sql.append(" to_char("+mode+".starttime_s,'yyyy-MM-dd')='"+requestBean.getQuerytime_s()+"' ");//����ʱ���ѡ
				//��ʼʱ�� ����ʱ��
				if(requestBean!=null&&requestBean.getStarttime_s()!=null){
					sql.append(" and "+mode+".starttime_s between ");
					sql.append(" to_date('"+requestBean.getStarttime_s()+"','yyyy-MM-dd HH24:mi:ss')");
					sql.append(" and to_date('"+requestBean.getStarttime_e()+"','yyyy-MM-dd HH24:mi:ss')");
				}
				//����
				if(requestBean!=null&&requestBean.getTelnumber()!=null&&requestBean.getTelnumber().trim().length()>0){
					sql.append(" and "+mode+".telnumber like '%"+requestBean.getTelnumber()+"%' ");
				}
			}else{//2G/3G HIVE
				boolean hours = defSdateAndEdate(requestBean.getStarttime_s(), requestBean.getStarttime_e());
				if(hours){//ʹ��ʱ�η��� pt_hour
					sql.append(" ("+mode+".pt_hour = "+formatDateString(requestBean.getStarttime_s(),"yyyy-MM-dd HH:mm:ss","yyyyMMddHH")+") ");//����ʱ���ѡ
				}else{//ʹ������� pt_date
					sql.append(" ("+mode+".pt_date = "+formatDateString(requestBean.getQuerytime_s(),"yyyy-MM-dd","yyyyMMdd")+") ");//����ʱ���ѡ
				}
				//����
				if(requestBean!=null&&requestBean.getTelnumber()!=null&&requestBean.getTelnumber().trim().length()>0){
					sql.append(" and "+mode+".telnumber like '%"+requestBean.getTelnumber()+"%' ");
				}
				//apn
				//if(requestBean!=null&&requestBean.getApn()!=null&&requestBean.getApn().trim().length()>0){
				//	sql.append(" and "+mode+".apn like '%"+requestBean.getApn()+"%' ");
				//}
				//rattype
				//if(requestBean!=null&&requestBean.getRattype()!=null&&requestBean.getRattype().trim().length()>0){
				//	sql.append(" and "+mode+".rattype = "+requestBean.getRattype()+" ");
				//}
				//��ʼʱ�� ����ʱ�� unix_timestamp('2014-01-09 00:54:51.657','yyyy-MM-dd HH:mm:ss') 
				//if(requestBean!=null&&requestBean.getStarttime_s()!=null&&requestBean.getStarttime_s().trim().length()>0){
				//	sql.append(" and unix_timestamp(substr(trim("+mode+".starttime),2,19),'yyyy-MM-dd HH:mm:ss') between ");
				//	sql.append(" unix_timestamp('"+requestBean.getStarttime_s()+"','yyyy-MM-dd HH:mm:ss')");
				//	sql.append(" and unix_timestamp('"+requestBean.getStarttime_e()+"','yyyy-MM-dd HH:mm:ss')");
				//}
				//nat��Դ�˿� ֻ��ip��Ч
				if(requestBean!=null&&requestBean.getUsrsport()!=null&&mode.equals("ip")&&requestBean.getUsrsport().trim().length()>0){
					sql.append(" and "+mode+".usrsport = "+requestBean.getUsrsport().trim()+" ");
				}
				//url ֻ��http��Ч
				//if(requestBean!=null&&!Function.equalsNull(requestBean.getUrl())){
				//	sql.append(" and "+mode+".url = "+requestBean.getUrl()+" ");
				//}
				//����ip��ַ ֻ��ip��Ч ��������
				//if(requestBean!=null&&!Function.equalsNull(requestBean.getGgsnip())){
				//	sql.append(" and "+mode+".ggsnip = "+requestBean.getGgsnip()+" ");
				//}
			}
		}
		return sql.toString();
	}
	
	/**
	 * ip left join http
	 * ������http.url��ip.����ip
	 * ����sip����,�����й���������,Ȼ����û����������
	 * 
	 * */
	public List leftJoinIpHttp(List deallist1, List deallist2, KeepNetLogOSSBean requestBean) {
		List list = null;
		List<List<String>> newlist = new Vector<List<String>>();
		String ggsnip = "";
		String url = "";
		boolean is_ggsnip = false;
		boolean is_url = false;
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ʱ���ʽ
		//�д����� ����ip
		if(requestBean!=null && requestBean.getGgsnip()!=null&&requestBean.getGgsnip().trim().length()>0){
			is_ggsnip = true;
			ggsnip = requestBean.getGgsnip().trim();
		}
		//�д����� url
		if(requestBean!=null && requestBean.getUrl()!=null&&requestBean.getUrl().trim().length()>0){
			is_url = true;
			url = requestBean.getUrl().trim();
		}
		//������ ͳ��ִ��Ч��
		int ijj = 0;
		System.out.println("deallist1.size():"+deallist1.size());
		System.out.println("deallist2.size():"+deallist2.size());
		System.out.println("leftJoinIpHttpִ�п�ʼʱ��:"+sim.format(new Date()));
    	for(int i=deallist1.size()-1;i>=0;i--){//��ipѭ��,http����������
    		list = null;
    		//��Ҫ����,��Ϊƥ���Ҫ�Ƴ�
    		for(int j=deallist2.size()-1;j>=0;j--){//ѭ��http,���sid��ipsid��������,������url,stime,etime
    			ijj ++;
    			if(((KeepNetLogOSSIpBean)deallist1.get(i)).getSid().equals(
    					((KeepNetLogOSSHttpBean)deallist2.get(j)).getIpsid())){
    				//Լ����url����� ������http
    				if(((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().length()>0
    						&&((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().indexOf("http")>=0){
	    				KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
	    				//����ip���� & url����
	    				if(is_ggsnip && is_url){
	    		    		int index_is_ggsnip = -1;
	    		    		int index_is_url = -1;
	    		    		index_is_ggsnip = ((KeepNetLogOSSIpBean)deallist1.get(i)).getGgsnip().indexOf(ggsnip);
	    		    		index_is_url = ((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().indexOf(url);
	    					if(index_is_ggsnip>=0 && index_is_url>=0){
	    						list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	    	    				newlist.add(list);
	    					}
	    				}else if(is_url){//ֻ��url����
	    		    		int index_is_url = -1;
	    		    		index_is_url = ((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().indexOf(url);
	    					if(index_is_url>=0){
	    						list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	    	    				newlist.add(list);
	    					}
	    				}else if(is_ggsnip){//ֻ�й���ip����
	    		    		int index_is_ggsnip = -1;
	    		    		index_is_ggsnip = ((KeepNetLogOSSIpBean)deallist1.get(i)).getGgsnip().indexOf(ggsnip);
	    					if(index_is_ggsnip>=0){
	    						list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	    	    				newlist.add(list);
		    				}
	    				}else{//�� û��
	    					list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	        				newlist.add(list);
	    				}
	    				//������Ҫ��ʱ�Ƴ� �������Ҫ����
	    				deallist2.remove(j);
    				}
    			}
    		}
    		if(list!=null && list.size()>0){//������������,�����Ѿ�������
    		}else{//������������
    			/*KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    			//����д�url���� ���¶����ô���
    			if(is_url){    				
    			}else{//û��url�����Ĵ���
	    			//�д����� ����ip
	    			if(is_ggsnip){
    		    		int index_is_ggsnip = -1;
    		    		index_is_ggsnip = ((KeepNetLogOSSIpBean)deallist1.get(i)).getGgsnip().indexOf(ggsnip);
    		    		if(index_is_ggsnip>=0){
	    					list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
	    	    			newlist.add(list);
	    				}
	    			}else{//û������ ����ip
	    				list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
	        			newlist.add(list);
	    			}
    			}*/
    		}    			
    	}
    	System.out.println("ijj:"+ijj);
		System.out.println("leftJoinIpHttpִ�н���ʱ��:"+sim.format(new Date()));
		return newlist;
	}
	
	/**
	 * �Խ���еĹ���ip��ַ����<wlan>
	 * */
	public List RowMapper(List result, KeepNetLogOSSBean requestBean){
		List list = new Vector();
		//�д�����
		if(requestBean!=null && requestBean.getGgsnip()!=null&&requestBean.getGgsnip().trim().length()>0){
	    	for(int i=0;i<result.size();i++){
	    		KeepNetLogOSSWlanBean bean = (KeepNetLogOSSWlanBean)result.get(i);
	    		//����wlan�Ĺ���ip
	    		int index = -1;
	    		index = bean.getUser_gw_ip().indexOf(requestBean.getGgsnip().trim());
	    		if(index>=0){
	    			list.add(bean.changeList());
	    		}
	    	}
		}else{//û������
			for(int i=0;i<result.size();i++){
	    		KeepNetLogOSSWlanBean bean = (KeepNetLogOSSWlanBean)result.get(i);	    		
	    		list.add(bean.changeList());
	    	}
		}
		return list;
	}
    
	/**
	 * ���������Excel
	 * dbType 1:hive��  2:bishow��Ϣ��
	 * */
	public synchronized void expExcel(int dbType, String filePath, String sheetName, List<List<String>> GnDetailList) throws Exception {
		System.out.println("KeepNetLogOSSClient expExcel filePath:"+filePath);
		WritableWorkbook wmm = null;
		//ByteArrayOutputStream os = null;
		try {
			//������д���Excel������
	        //os = new ByteArrayOutputStream();
            wmm = Workbook.createWorkbook(new File(filePath));//�����ļ�������
	        //wmm = Workbook.createWorkbook(os);
        } catch (IOException e) {
			System.out.println("KeepNetLogOSSClient expExcel �����µ�excel�ļ������쳣:"+e.toString());
            e.printStackTrace();
			throw e;//�׳��쳣
            //return null;
        }
        //����������
        if (sheetName == null || sheetName.equals(""))
            sheetName = "sheet";
        WritableSheet ws = wmm.createSheet(sheetName, 0);
        String titleStr = "";
        //����
        String[] columns = null;//{"�ֻ�����", "�û�NAT����IP��ַ", "�û�˽��IP��ַ", "NAT��Դ�˿�", "Ŀ��IP","Ŀ�Ķ˿�", "����URL", "����ʱ��", "����ʱ��", "����ʱ��","APN","��������"};
        if(dbType==1){
        	columns = new String[]{"�ֻ�����", "�û�NAT����IP��ַ", "�û�˽��IP��ַ", "NAT��Դ�˿�", "Ŀ��IP", "Ŀ�Ķ˿�", "����URL", "����ʱ��", "��ʼʱ��", "����ʱ��", "APN", "��������"};        	
        }else if(dbType==2){
        	columns = new String[]{"������ʼʱ��", "��������ʱ��", "�û���", "�û�mac��ַ", "AC����ʡ", "�û�����ʡ", "ACIP", "NASID", "�����ֽ�(��λ:m)", "�����ֽ�(��λ:m)", "�û�����IP��ַ"};
        }
        try {
            //�����ʽ
            WritableFont writableFont_bt = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_bt = new WritableCellFormat(writableFont_bt);
            writableCellFormat_bt.setAlignment(jxl.format.Alignment.CENTRE);//����
            writableCellFormat_bt.setVerticalAlignment(VerticalAlignment.CENTRE);//����
            writableCellFormat_bt.setBackground(Colour.LIGHT_GREEN);//����ɫ
            writableCellFormat_bt.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
            writableCellFormat_bt.setWrap(true);//�Զ�����
	        //��ͨ�ı�
	        WritableFont writableFont_pt = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
	                UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
	        WritableCellFormat writableCellFormat_pt = new WritableCellFormat(writableFont_pt);
	        writableCellFormat_pt.setAlignment(jxl.format.Alignment.CENTRE);//���Ҷ���
	        writableCellFormat_pt.setVerticalAlignment(VerticalAlignment.CENTRE);//���¶���
	        writableCellFormat_pt.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
            //���ָ�ʽ(��С��)
            WritableFont writableFont_num = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_nr = null;
            jxl.write.NumberFormat nf = new jxl.write.NumberFormat("###0.0##");//���ָ�ʽ
            writableCellFormat_nr = new WritableCellFormat(writableFont_num, nf);
            writableCellFormat_nr.setAlignment(jxl.format.Alignment.CENTRE);//����
            writableCellFormat_nr.setVerticalAlignment(VerticalAlignment.CENTRE);//����
            writableCellFormat_nr.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
            //���ָ�ʽ(�ֻ�����)
            WritableFont writableFont_num1 = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_nr1 = null;
            jxl.write.NumberFormat nf1 = new jxl.write.NumberFormat("##0");//���ָ�ʽ
            writableCellFormat_nr1 = new WritableCellFormat(writableFont_num1, nf1);
            writableCellFormat_nr1.setAlignment(jxl.format.Alignment.CENTRE);//����
            writableCellFormat_nr1.setVerticalAlignment(VerticalAlignment.CENTRE);//����
            writableCellFormat_nr1.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//�߿�
	        
        	//��ӱ���
        	for (int i = 0; i < columns.length; i++) {
            	titleStr = columns[i];
            	Label labels = new Label(i, 0, titleStr, writableCellFormat_bt);
            	ws.addCell(labels);
        	}
	        //�������
	        jxl.write.Label labelcontent;
	        for (int i = 0; i < GnDetailList.size(); i++) {//��2�п�ʼ��������
	            for (int j = 0; j < columns.length; j++) {//��
	                // Label(�к�,�к� ,����)
	            	String tempStr = "";
	            	if(GnDetailList.get(i).get(j)!=null){
	            		tempStr = GnDetailList.get(i).get(j).toString();
	            	}
	                if (tempStr != null && tempStr.length()==0) {
	                    tempStr = " ";
	                }
                	labelcontent = new jxl.write.Label(j, i+1, tempStr, writableCellFormat_pt);
            		ws.addCell(labelcontent);
	            }
	           
	        }
	        //�����и�
        	//ws.setRowView(0, 550);
            //�����п�
            for(int i=0;i<ws.getColumns();i++){
            	ws.setColumnView(i, 14);
            }
	        //����ڴ�
            //System.gc();
	        //����
            wmm.write();
            ws = null;
            //�ر�
            wmm.close();
            //return new ByteArrayInputStream(os.toByteArray());
        } catch (RowsExceededException e) {
			System.out.println("KeepNetLogOSSClient expExcel �����д���:"+e.toString());
            e.printStackTrace();
			throw e;//�׳��쳣
            //return null;
        } catch (WriteException e1) {
			System.out.println("KeepNetLogOSSClient expExcel ����д�����:"+e1.toString());
            e1.printStackTrace();
			throw e1;//�׳��쳣
            //return null;
        } catch (Exception e2) {
			System.out.println("KeepNetLogOSSClient expExcel ����������:"+e2.toString());
            e2.printStackTrace();
			throw e2;//�׳��쳣
            //return null;
        } finally {
        	//����ڴ�
            System.gc();
        	ws = null;
        	wmm =null;
        }
	}

	
	public static void main(String[] args) throws Exception {
		HiveTest test = new HiveTest();
		KeepNetLogOSSBean requestBean = new KeepNetLogOSSBean();
		KeepNetLogOSSData resultData = null;
		String RespCode = "";//�������
		String RespDesc = "";//�������
		List<List<String>> responseList = null;//��ϸ���
		String totalCount = "0";//�ܼ�¼��

		String telnumber = "";
		String starttime_s = "";
		String starttime_e = "";
		String querytime_s = "";
		String rattype = "";
	    String filename = "";
	    String test_flag = "";
		//for(int i=0;i<args.length;i++){
			//System.out.println(args[i]);
		//}
		if(args.length==6){
			telnumber = args[0];
			starttime_s = args[1];
			starttime_s = test.formatDateString(starttime_s, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
			starttime_e = args[2];
			starttime_e = test.formatDateString(starttime_e, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
			querytime_s = args[3];
			rattype = args[4];
			test_flag = args[5];
			filename = "D:\\CSG\\"+telnumber+"_"+test.formatDateString(starttime_s,"yyyy-MM-dd HH:mm:ss","yyyyMMddHH")+".xls";
		}else{
			telnumber = "13960069981";
			starttime_s = "2014-01-16 00:00:00";
			starttime_e = "2014-01-16 00:59:00";
			querytime_s = "2014-01-16";
			//rattype = "2";
			test_flag = "true";
			filename = "D:\\home\\hadoop\\"+telnumber+"_"+test.formatDateString(starttime_s,"yyyy-MM-dd HH:mm:ss","yyyyMMddHH")+".xls";
		}
	    System.out.println("telnumber:"+telnumber);
	    System.out.println("starttime_s:"+starttime_s);
	    System.out.println("starttime_e:"+starttime_e);
	    System.out.println("querytime_s:"+querytime_s);
	    System.out.println("rattype:"+rattype);
	    System.out.println("test_flag:"+test_flag);
	    if(test_flag.equals("true")){//����
	    	test.setTest_flag(true);
	    }else{//����
	    	test.setTest_flag(false);
	    }
		
		/*requestBean.setTelnumber("13706942060");
		requestBean.setStarttime_s("2014-01-21 11:30:00");
		requestBean.setStarttime_e("2014-01-21 11:45:00");
		requestBean.setQuerytime_s("2014-01-21");
		requestBean.setRattype("2");		
		requestBean.setFilename(filename);*/
	    
		requestBean.setFilename(filename);
		requestBean.setTelnumber(telnumber);
		requestBean.setStarttime_s(starttime_s);
		requestBean.setStarttime_e(starttime_e);
		requestBean.setQuerytime_s(querytime_s);
		requestBean.setRattype(rattype);
		
		try {
			resultData = test.qryKeepNetLog(requestBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseList = resultData.getDetailList();
		totalCount = resultData.getTotalCount();
		RespCode = resultData.getRespCode();
		RespDesc = resultData.getRespDesc();
		System.out.println(totalCount);
		System.out.println(RespCode);
		System.out.println(RespDesc);
		
    	/*for(int i=0;i<responseList.size();i++){
    		for(int j=0;j<((List)responseList.get(i)).size();j++){
    			String str = "";
    			if(((List)responseList.get(i)).get(j)!=null){
    				str = ((List)responseList.get(i)).get(j).toString();
    			}
    			System.out.print(" "+str);
    		}
    		System.out.println("");
    	}*/
	}
}
