import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import bean.NetLogClientBean;
import bean.NgReqBean;
import client.NetLogServiceClient;

public class NgClientTest {
	public static void main(String[] args) {
		NetLogServiceClient client = new NetLogServiceClient();
		NgReqBean requestBean = new NgReqBean();
		NetLogClientBean ncb = new NetLogClientBean();
		
		String telnumber = "13600884160";
		String starttime_s = "20131230000000";
		String starttime_e = "20131231000000";
		String apn = "";
		String servicename = "";
		String home_area_code = "591";
		String reqsource = "";
		
		String seldata = "2";//默认查询hbase
		if(home_area_code.equals("591")){
			seldata = "1";
		}else{
			seldata = "2";
		}
		client.setSeldata(seldata);//设置查询数据库
		
		requestBean.setTelnumber(Long.parseLong(telnumber));
		requestBean.setStarttime_s(starttime_s);
		requestBean.setStarttime_e(starttime_e);
		requestBean.setApn(apn);
		requestBean.setServicename(servicename);
		requestBean.setReqSource(reqsource);		
		
		//查询
		ncb = client.queryHbaseData(requestBean,"","");//起始,分页为空为全部查询
		List<List<String>> responseList = ncb.getDealDetailList();
		FileOutputStream fops = null;
		InputStream os = null;
		try{
			fops = new FileOutputStream("D:/home/a.xls");//使用输出流
			os = client.expExcel("aa", responseList);
			byte[] b = new byte[100];
	        int len;
	        while ((len = os.read(b)) > 0) {
	        	fops.write(b, 0, len);
	        }
	        fops.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fops!=null){
				try {
					fops.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*String a = "13600884160,G700-T00,CMNET,,2013-12-30 21:00:31.683,2013-12-30 21:00:33.183,483,1274,223.82.247.72,1,";
		String b = "13600884160,G700-T00,CMNET,未知,2013-12-30 21:00:00,2013-12-30 21:00:00,483,1274,223.82.247.72,3G,a";
		if(a.endsWith(",")){
			System.out.println("1");
		}
		String ngdataArray1[] = a.split(",");
		String ngdataArray2[] = b.split(",");
		System.out.println(ngdataArray1.length);
		System.out.println(ngdataArray2.length);*/
	}
}
