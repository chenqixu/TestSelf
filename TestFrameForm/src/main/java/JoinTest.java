import java.util.List;
import java.util.Vector;

import bean.KeepNetLogOSSHttpBean;
import bean.KeepNetLogOSSIpBean;
import bean.KeepNetLogOSSResultBean;

public class JoinTest {
	static List deallist1 = null;
	static KeepNetLogOSSIpBean deallist11 = null;
	static List deallist2 = null;
	static KeepNetLogOSSHttpBean deallist22 = null;
	
	public static void main(String[] args) {
		deallist1 = new Vector();
		deallist2 = new Vector();

		deallist11 = new KeepNetLogOSSIpBean();
		deallist11.setSid("12");
		deallist11.setApn("cmnet");
		deallist1.add(deallist11);		

		deallist11 = new KeepNetLogOSSIpBean();
		deallist11.setSid("13");
		deallist11.setApn("cmwap");
		deallist1.add(deallist11);
		
		deallist22 = new KeepNetLogOSSHttpBean();
		deallist22.setIpsid("12");
		deallist22.setUrl("qidian");
		deallist2.add(deallist22);

		deallist22 = new KeepNetLogOSSHttpBean();
		deallist22.setIpsid("12");
		deallist22.setUrl("www");
		deallist2.add(deallist22);
		
		leftJoinIpHttp(deallist1, deallist2);
		
	}
	
	/**
	 * ip left join http
	 * */
	public static List leftJoinIpHttp(List deallist1, List deallist2) {
		List list = null;
		List newlist = new Vector();
		boolean setflag = false;		
    	for(int i=0;i<deallist1.size();i++){
    		list = null;
    		for(int j=0;j<deallist2.size();j++){
    			if(((KeepNetLogOSSIpBean)deallist1.get(i)).getSid().equals(
    					((KeepNetLogOSSHttpBean)deallist2.get(j)).getIpsid())){
    				//System.out.println(((KeepNetLogOSSIpBean)deallist1.get(i)).getSid());
    				KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    				list = result.joinIpAndHttp((KeepNetLogOSSIpBean)deallist1.get(i), (KeepNetLogOSSHttpBean)deallist2.get(j));
    				newlist.add(list);
    			}
    		}
    		if(list!=null && list.size()>0){
    		}else{
    			KeepNetLogOSSResultBean result = new KeepNetLogOSSResultBean();
    			list = result.joinIp((KeepNetLogOSSIpBean)deallist1.get(i));
    			newlist.add(list);
    		}    			
    	}
    	
    	for(int i=0;i<newlist.size();i++){
    		for(int j=0;j<((List)newlist.get(i)).size();j++){
    			String str = "";
    			if(((List)newlist.get(i)).get(j)!=null){
    				str = ((List)newlist.get(i)).get(j).toString();
    			}
    			System.out.print(" "+str);
    		}
    		System.out.println("");
    	}
		
		return list;
	}
}
