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
	
	//测试开关 true为测试
	private boolean test_flag = true;
	
	public void setTest_flag(boolean test_flag) {
		this.test_flag = test_flag;
	}

	public HiveTest(){		
	}
		
    /**
     * 将日期字符串按指定日期格式成新字符串
     * */
	private String formatDateString(String date, String origFormat, String destFormat) throws Exception {
    	SimpleDateFormat sf1 = new SimpleDateFormat(origFormat);
    	Date d = null;
		try {
			d = sf1.parse(date);
		} catch (ParseException e) {
			System.out.println("KeepNetLogOSSClient formatDateString error:"+e.toString());
			e.printStackTrace();
			throw e;//抛出异常
		}
    	SimpleDateFormat sf2 = new SimpleDateFormat(destFormat);
    	return sf2.format(d);
    }
	
	/**
	 * 判断传入的开始和结束时间是否相差1个小时,是则使用小时分区,不是则使用天分区
	 * */
	private boolean defSdateAndEdate(String start_date, String end_date) throws Exception {
		boolean hours = false;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date beginDate = format.parse(start_date);
		java.util.Date endDate= format.parse(end_date);
		//long day=(endDate.getTime()-beginDate.getTime())/(60*1000);
		if(beginDate.getHours() == endDate.getHours()){//都在同一时段
			hours = true;
		}
		return hours;
	}
	
	/**
	 * 查询
	 * */
	public KeepNetLogOSSData qryKeepNetLog(KeepNetLogOSSBean requestBean) throws Exception {
		KeepNetLogOSSData knlod = new KeepNetLogOSSData();//返回结果
		int dbType = 0;//数据类型 1:hive 2:bishow
		StringBuffer sbSQL = new StringBuffer("");//查询sql
		List<KeepNetLogOSSClientThread> listThread = new ArrayList<KeepNetLogOSSClientThread>();//用于存放线程
		List wlan_resultList = null;//wlan结果
		List ip_resultList = null;//ip结果
		List http_resultList = null;//http结果
		List<List<String>> qryresultList = null;//处理后的查询结果
		try{
			if(requestBean!=null){
				if(requestBean.getRattype().equals("99")){//WLAN 移动网管查询导入的数据 oracle
					dbType = 2;
					
					//wlan
	        		sbSQL.delete(0, sbSQL.length());//清空sql
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
					sbSQL.append(" and rownum<10000 ");//最多只允许查10000条
					sbSQL.append(" order by starttime_s ");
					/*wlan线程*/
					KeepNetLogOSSClientThread keepqueryThread1 = new KeepNetLogOSSClientThread(sbSQL.toString(), dbType, "wlan", requestBean.getTelnumber());
					keepqueryThread1.setTest_flag(this.test_flag);
					keepqueryThread1.start();
	                listThread.add(keepqueryThread1);
				}else{//2G/3G HIVE
					dbType = 1;
					
					//ip
	        		sbSQL.delete(0, sbSQL.length());//清空sql
					sbSQL.append("select ");
					sbSQL.append(" ip.sid ");//用于和http的ipsid关联
					sbSQL.append(" ,ip.telnumber ");//手机号码
					sbSQL.append(" ,'' ");//用户NAT后公网IP地址
					sbSQL.append(" ,ip.userip ");//用户私网IP地址
					sbSQL.append(" ,ip.usrsport ");//NAT后源端口
					sbSQL.append(" ,ip.usrdip ");//目的IP
					sbSQL.append(" ,ip.usrdport ");//目的端口
					sbSQL.append(" ,ip.starttime ");//访问时间
					sbSQL.append(" ,ip.lasttime ");//下线时间
					sbSQL.append(" ,ip.apn ");//APN
					sbSQL.append(" ,case when ip.rattype=1 then '3G' when ip.rattype=2 then '2G' else '' end ");//2g/3g
					sbSQL.append(" from ip_rc ip ");
					sbSQL.append(" where ");
					sbSQL.append(appendWhereSql(requestBean, "ip"));
					sbSQL.append(" order by ip.starttime desc ");
					//sbSQL.append(" limit 20000 ");//最多只允许查10000条					
					/*ip线程*/
					KeepNetLogOSSClientThread keepqueryThread1 = new KeepNetLogOSSClientThread(sbSQL.toString(), dbType, "ip", requestBean.getTelnumber());
					keepqueryThread1.setTest_flag(this.test_flag);
					keepqueryThread1.start();
	                listThread.add(keepqueryThread1);
	                
	        		//http
	        		sbSQL.delete(0, sbSQL.length());//清空sql
	        		sbSQL.append("select ");
	        		sbSQL.append(" http.ipsid ");//用于和ip的sid关联
	        		sbSQL.append(" ,http.url ");//访问URL
	        		sbSQL.append(" ,http.starttime ");//上线时间
	        		sbSQL.append(" ,http.lasttime ");//下线时间
	        		sbSQL.append(" from http_rc http ");
	        		sbSQL.append(" where ");
	        		sbSQL.append(appendWhereSql(requestBean, "http"));
	        		sbSQL.append(" order by http.starttime desc ");
	        		//sbSQL.append(" limit 30000 ");//最多只允许查10000条	                
	        		/*http线程*/
					KeepNetLogOSSClientThread keepqueryThread2 = new KeepNetLogOSSClientThread(sbSQL.toString(), dbType, "http", requestBean.getTelnumber());
					keepqueryThread2.setTest_flag(this.test_flag);
					keepqueryThread2.start();
	                listThread.add(keepqueryThread2);
				}
				
				/*判断多线程是否执行完成*/
		        int isflag = listThread.size();
		        if(isflag==1){//bishow wlan
			        while(isflag>0){
				        Thread.sleep(1);//1秒刷新一次多线程
				        for(int i=0;i<listThread.size();i++){
				        	if(listThread.get(i).isFlag()){//线程执行完成
				        		listThread.get(i).setFlag(false);//回设
				        		//waln结果
				        		wlan_resultList = listThread.get(i).getResultList();
				        		isflag--;//计数器减1,直到为0,循环停止
				        	}
				        }
			        }
			        //过滤公网ip 把List<Bean>变成List<List<String>>			        
			        qryresultList = RowMapper(wlan_resultList, requestBean);
		        }else{//hive 需要处理ip,http
			        while(isflag>0){
				        Thread.sleep(1);//1秒刷新一次多线程
				        for(int i=0;i<listThread.size();i++){
				        	if(listThread.get(i).isFlag()){//线程执行完成
				        		listThread.get(i).setFlag(false);//回设
				        		if(listThread.get(i).getTag_name().equals("ip")){
				        			//ip结果
				        			ip_resultList = listThread.get(i).getResultList();
				        		}else if(listThread.get(i).getTag_name().equals("http")){
				        			//http结果
				        			http_resultList = listThread.get(i).getResultList();
				        		}
				        		isflag--;//计数器减1,直到为0,循环停止
				        	}
				        }
			        }
			        //查询完成,处理ip表和http表的join,并过滤http.url和ip.公网ip
			        qryresultList = leftJoinIpHttp(ip_resultList, http_resultList, requestBean);
		        }
		        //查询线程结束,设置结果
				knlod.setDetailList(qryresultList);
				knlod.setRespCode("0");
				knlod.setRespDesc("成功");
				knlod.setTotalCount(String.valueOf(qryresultList.size()));
				//导出Excel
				if(qryresultList!=null &&qryresultList.size()>0){
					expExcel(dbType, requestBean.getFilename(), requestBean.getTelnumber(), qryresultList);
				}
			}else{
				knlod.setRespCode("-1");
				knlod.setRespDesc("查询失败:没有传入参数");
			}
		} catch (Exception e) {
			System.out.println("KeepNetLogOSSClient qryKeepNetLog error:"+e.toString());
			e.printStackTrace();
			knlod.setRespCode("-1");
			knlod.setRespDesc("查询失败:"+e.toString());
		}
		return knlod;
	}
	
	/**
	 * ip left join http
	 * */
	public List leftJoinIpHttp(List deallist1, List deallist2) {
		List list = null;
		List<List<String>> newlist = new Vector<List<String>>();
    	for(int i=0;i<deallist1.size();i++){//按ip循环
    		list = null;
    		for(int j=0;j<deallist2.size();j++){//循环http,如果sid和ipsid关联的上,就设置url,stime,etime
    			if(((KeepNetLogOSSIpBean)deallist1.get(i)).getSid().equals(
    					((KeepNetLogOSSHttpBean)deallist2.get(j)).getIpsid())){
    				KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    				list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
    				newlist.add(list);
    			}
    		}
    		if(list!=null && list.size()>0){//条件关联的上,上面已经处理了
    		}else{//条件关联不上
    			KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    			list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
    			newlist.add(list);
    		}    			
    	}    	
		return newlist;
	}
		
	/**
	 * 根据请求bean组装查询条件
	 * */
	public String appendWhereSql(KeepNetLogOSSBean requestBean, String mode) throws Exception {
		StringBuffer sql = new StringBuffer("");
		if(requestBean!=null){
			if(requestBean.getRattype().equals("99")){//WLAN 移动网管查询导入的数据 oracle				
				sql.append(" to_char("+mode+".starttime_s,'yyyy-MM-dd')='"+requestBean.getQuerytime_s()+"' ");//访问时间必选
				//开始时间 结束时间
				if(requestBean!=null&&requestBean.getStarttime_s()!=null){
					sql.append(" and "+mode+".starttime_s between ");
					sql.append(" to_date('"+requestBean.getStarttime_s()+"','yyyy-MM-dd HH24:mi:ss')");
					sql.append(" and to_date('"+requestBean.getStarttime_e()+"','yyyy-MM-dd HH24:mi:ss')");
				}
				//号码
				if(requestBean!=null&&requestBean.getTelnumber()!=null&&requestBean.getTelnumber().trim().length()>0){
					sql.append(" and "+mode+".telnumber like '%"+requestBean.getTelnumber()+"%' ");
				}
			}else{//2G/3G HIVE
				boolean hours = defSdateAndEdate(requestBean.getStarttime_s(), requestBean.getStarttime_e());
				if(hours){//使用时段分区 pt_hour
					sql.append(" ("+mode+".pt_hour = "+formatDateString(requestBean.getStarttime_s(),"yyyy-MM-dd HH:mm:ss","yyyyMMddHH")+") ");//访问时间必选
				}else{//使用天分区 pt_date
					sql.append(" ("+mode+".pt_date = "+formatDateString(requestBean.getQuerytime_s(),"yyyy-MM-dd","yyyyMMdd")+") ");//访问时间必选
				}
				//号码
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
				//开始时间 结束时间 unix_timestamp('2014-01-09 00:54:51.657','yyyy-MM-dd HH:mm:ss') 
				//if(requestBean!=null&&requestBean.getStarttime_s()!=null&&requestBean.getStarttime_s().trim().length()>0){
				//	sql.append(" and unix_timestamp(substr(trim("+mode+".starttime),2,19),'yyyy-MM-dd HH:mm:ss') between ");
				//	sql.append(" unix_timestamp('"+requestBean.getStarttime_s()+"','yyyy-MM-dd HH:mm:ss')");
				//	sql.append(" and unix_timestamp('"+requestBean.getStarttime_e()+"','yyyy-MM-dd HH:mm:ss')");
				//}
				//nat后源端口 只有ip有效
				if(requestBean!=null&&requestBean.getUsrsport()!=null&&mode.equals("ip")&&requestBean.getUsrsport().trim().length()>0){
					sql.append(" and "+mode+".usrsport = "+requestBean.getUsrsport().trim()+" ");
				}
				//url 只有http有效
				//if(requestBean!=null&&!Function.equalsNull(requestBean.getUrl())){
				//	sql.append(" and "+mode+".url = "+requestBean.getUrl()+" ");
				//}
				//公网ip地址 只有ip有效 都走配置
				//if(requestBean!=null&&!Function.equalsNull(requestBean.getGgsnip())){
				//	sql.append(" and "+mode+".ggsnip = "+requestBean.getGgsnip()+" ");
				//}
			}
		}
		return sql.toString();
	}
	
	/**
	 * ip left join http
	 * 并过滤http.url和ip.公网ip
	 * 先用sip关联,处理有关联的数据,然后处理没关联的数据
	 * 
	 * */
	public List leftJoinIpHttp(List deallist1, List deallist2, KeepNetLogOSSBean requestBean) {
		List list = null;
		List<List<String>> newlist = new Vector<List<String>>();
		String ggsnip = "";
		String url = "";
		boolean is_ggsnip = false;
		boolean is_url = false;
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式
		//有传条件 公网ip
		if(requestBean!=null && requestBean.getGgsnip()!=null&&requestBean.getGgsnip().trim().length()>0){
			is_ggsnip = true;
			ggsnip = requestBean.getGgsnip().trim();
		}
		//有传条件 url
		if(requestBean!=null && requestBean.getUrl()!=null&&requestBean.getUrl().trim().length()>0){
			is_url = true;
			url = requestBean.getUrl().trim();
		}
		//计数器 统计执行效率
		int ijj = 0;
		System.out.println("deallist1.size():"+deallist1.size());
		System.out.println("deallist2.size():"+deallist2.size());
		System.out.println("leftJoinIpHttp执行开始时间:"+sim.format(new Date()));
    	for(int i=deallist1.size()-1;i>=0;i--){//按ip循环,http倒叙这里额倒叙
    		list = null;
    		//需要倒叙,因为匹配的要移除
    		for(int j=deallist2.size()-1;j>=0;j--){//循环http,如果sid和ipsid关联的上,就设置url,stime,etime
    			ijj ++;
    			if(((KeepNetLogOSSIpBean)deallist1.get(i)).getSid().equals(
    					((KeepNetLogOSSHttpBean)deallist2.get(j)).getIpsid())){
    				//约束有url的情况 并且有http
    				if(((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().length()>0
    						&&((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().indexOf("http")>=0){
	    				KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
	    				//公网ip条件 & url条件
	    				if(is_ggsnip && is_url){
	    		    		int index_is_ggsnip = -1;
	    		    		int index_is_url = -1;
	    		    		index_is_ggsnip = ((KeepNetLogOSSIpBean)deallist1.get(i)).getGgsnip().indexOf(ggsnip);
	    		    		index_is_url = ((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().indexOf(url);
	    					if(index_is_ggsnip>=0 && index_is_url>=0){
	    						list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	    	    				newlist.add(list);
	    					}
	    				}else if(is_url){//只有url条件
	    		    		int index_is_url = -1;
	    		    		index_is_url = ((KeepNetLogOSSHttpBean)deallist2.get(j)).getUrl().indexOf(url);
	    					if(index_is_url>=0){
	    						list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	    	    				newlist.add(list);
	    					}
	    				}else if(is_ggsnip){//只有公网ip条件
	    		    		int index_is_ggsnip = -1;
	    		    		index_is_ggsnip = ((KeepNetLogOSSIpBean)deallist1.get(i)).getGgsnip().indexOf(ggsnip);
	    					if(index_is_ggsnip>=0){
	    						list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	    	    				newlist.add(list);
		    				}
	    				}else{//都 没有
	    					list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
	        				newlist.add(list);
	    				}
	    				//处理完要及时移除 但这里就要倒叙
	    				deallist2.remove(j);
    				}
    			}
    		}
    		if(list!=null && list.size()>0){//条件关联的上,上面已经处理了
    		}else{//条件关联不上
    			/*KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    			//如果有传url条件 以下都不用处理
    			if(is_url){    				
    			}else{//没传url条件的处理
	    			//有传条件 公网ip
	    			if(is_ggsnip){
    		    		int index_is_ggsnip = -1;
    		    		index_is_ggsnip = ((KeepNetLogOSSIpBean)deallist1.get(i)).getGgsnip().indexOf(ggsnip);
    		    		if(index_is_ggsnip>=0){
	    					list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
	    	    			newlist.add(list);
	    				}
	    			}else{//没传条件 公网ip
	    				list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
	        			newlist.add(list);
	    			}
    			}*/
    		}    			
    	}
    	System.out.println("ijj:"+ijj);
		System.out.println("leftJoinIpHttp执行结束时间:"+sim.format(new Date()));
		return newlist;
	}
	
	/**
	 * 对结果中的公网ip地址过滤<wlan>
	 * */
	public List RowMapper(List result, KeepNetLogOSSBean requestBean){
		List list = new Vector();
		//有传条件
		if(requestBean!=null && requestBean.getGgsnip()!=null&&requestBean.getGgsnip().trim().length()>0){
	    	for(int i=0;i<result.size();i++){
	    		KeepNetLogOSSWlanBean bean = (KeepNetLogOSSWlanBean)result.get(i);
	    		//过滤wlan的公网ip
	    		int index = -1;
	    		index = bean.getUser_gw_ip().indexOf(requestBean.getGgsnip().trim());
	    		if(index>=0){
	    			list.add(bean.changeList());
	    		}
	    	}
		}else{//没传条件
			for(int i=0;i<result.size();i++){
	    		KeepNetLogOSSWlanBean bean = (KeepNetLogOSSWlanBean)result.get(i);	    		
	    		list.add(bean.changeList());
	    	}
		}
		return list;
	}
    
	/**
	 * 导出结果到Excel
	 * dbType 1:hive库  2:bishow信息库
	 * */
	public synchronized void expExcel(int dbType, String filePath, String sheetName, List<List<String>> GnDetailList) throws Exception {
		System.out.println("KeepNetLogOSSClient expExcel filePath:"+filePath);
		WritableWorkbook wmm = null;
		//ByteArrayOutputStream os = null;
		try {
			//创建可写入的Excel工作薄
	        //os = new ByteArrayOutputStream();
            wmm = Workbook.createWorkbook(new File(filePath));//根据文件名创建
	        //wmm = Workbook.createWorkbook(os);
        } catch (IOException e) {
			System.out.println("KeepNetLogOSSClient expExcel 生成新的excel文件出现异常:"+e.toString());
            e.printStackTrace();
			throw e;//抛出异常
            //return null;
        }
        //创建工作表
        if (sheetName == null || sheetName.equals(""))
            sheetName = "sheet";
        WritableSheet ws = wmm.createSheet(sheetName, 0);
        String titleStr = "";
        //标题
        String[] columns = null;//{"手机号码", "用户NAT后公网IP地址", "用户私网IP地址", "NAT后源端口", "目的IP","目的端口", "访问URL", "访问时间", "上线时间", "下线时间","APN","网络类型"};
        if(dbType==1){
        	columns = new String[]{"手机号码", "用户NAT后公网IP地址", "用户私网IP地址", "NAT后源端口", "目的IP", "目的端口", "访问URL", "访问时间", "开始时间", "结束时间", "APN", "网络类型"};        	
        }else if(dbType==2){
        	columns = new String[]{"上网开始时间", "上网结束时间", "用户名", "用户mac地址", "AC归属省", "用户归属省", "ACIP", "NASID", "流入字节(单位:m)", "流出字节(单位:m)", "用户公网IP地址"};
        }
        try {
            //标题格式
            WritableFont writableFont_bt = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_bt = new WritableCellFormat(writableFont_bt);
            writableCellFormat_bt.setAlignment(jxl.format.Alignment.CENTRE);//对齐
            writableCellFormat_bt.setVerticalAlignment(VerticalAlignment.CENTRE);//对齐
            writableCellFormat_bt.setBackground(Colour.LIGHT_GREEN);//背景色
            writableCellFormat_bt.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
            writableCellFormat_bt.setWrap(true);//自动换行
	        //普通文本
	        WritableFont writableFont_pt = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
	                UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
	        WritableCellFormat writableCellFormat_pt = new WritableCellFormat(writableFont_pt);
	        writableCellFormat_pt.setAlignment(jxl.format.Alignment.CENTRE);//左右对齐
	        writableCellFormat_pt.setVerticalAlignment(VerticalAlignment.CENTRE);//上下对齐
	        writableCellFormat_pt.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
            //数字格式(有小数)
            WritableFont writableFont_num = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_nr = null;
            jxl.write.NumberFormat nf = new jxl.write.NumberFormat("###0.0##");//数字格式
            writableCellFormat_nr = new WritableCellFormat(writableFont_num, nf);
            writableCellFormat_nr.setAlignment(jxl.format.Alignment.CENTRE);//对齐
            writableCellFormat_nr.setVerticalAlignment(VerticalAlignment.CENTRE);//对齐
            writableCellFormat_nr.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
            //数字格式(手机号码)
            WritableFont writableFont_num1 = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat writableCellFormat_nr1 = null;
            jxl.write.NumberFormat nf1 = new jxl.write.NumberFormat("##0");//数字格式
            writableCellFormat_nr1 = new WritableCellFormat(writableFont_num1, nf1);
            writableCellFormat_nr1.setAlignment(jxl.format.Alignment.CENTRE);//对齐
            writableCellFormat_nr1.setVerticalAlignment(VerticalAlignment.CENTRE);//对齐
            writableCellFormat_nr1.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);//边框
	        
        	//添加标题
        	for (int i = 0; i < columns.length; i++) {
            	titleStr = columns[i];
            	Label labels = new Label(i, 0, titleStr, writableCellFormat_bt);
            	ws.addCell(labels);
        	}
	        //添加内容
	        jxl.write.Label labelcontent;
	        for (int i = 0; i < GnDetailList.size(); i++) {//第2行开始才是数据
	            for (int j = 0; j < columns.length; j++) {//列
	                // Label(列号,行号 ,内容)
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
	        //设置行高
        	//ws.setRowView(0, 550);
            //设置列宽
            for(int i=0;i<ws.getColumns();i++){
            	ws.setColumnView(i, 14);
            }
	        //清空内存
            //System.gc();
	        //保存
            wmm.write();
            ws = null;
            //关闭
            wmm.close();
            //return new ByteArrayInputStream(os.toByteArray());
        } catch (RowsExceededException e) {
			System.out.println("KeepNetLogOSSClient expExcel 数据行错误:"+e.toString());
            e.printStackTrace();
			throw e;//抛出异常
            //return null;
        } catch (WriteException e1) {
			System.out.println("KeepNetLogOSSClient expExcel 数据写入错误:"+e1.toString());
            e1.printStackTrace();
			throw e1;//抛出异常
            //return null;
        } catch (Exception e2) {
			System.out.println("KeepNetLogOSSClient expExcel 数据填充错误:"+e2.toString());
            e2.printStackTrace();
			throw e2;//抛出异常
            //return null;
        } finally {
        	//清空内存
            System.gc();
        	ws = null;
        	wmm =null;
        }
	}

	
	public static void main(String[] args) throws Exception {
		HiveTest test = new HiveTest();
		KeepNetLogOSSBean requestBean = new KeepNetLogOSSBean();
		KeepNetLogOSSData resultData = null;
		String RespCode = "";//结果代码
		String RespDesc = "";//结果描述
		List<List<String>> responseList = null;//详细结果
		String totalCount = "0";//总记录数

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
	    if(test_flag.equals("true")){//测试
	    	test.setTest_flag(true);
	    }else{//生产
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
