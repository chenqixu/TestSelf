package com.newland.bi.bigdata;


import java.util.regex.*;

public class regex1 {
	public static void main(String args[]) {

		/*String str = "for my\" money\" ";
		String regex = "\""; // 
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		boolean result = m.find();
		System.out.println(result);*/
		
		/*String regex="y\\\\";//试一试"y\\" 
		String str="i say\\i\\love\\java "; 
		System.out.println(str); 
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(str); 
		String s=m.replaceAll("⊙⊙"); // ("") 删除 
		System.out.println(s); */
		
//		String regex="[(1[0-9]{0,1})(,)]";
//		Pattern p=Pattern.compile(regex);
//		Matcher m=p.matcher("1,15");
//		boolean result = m.find();
//		System.out.println(result);
		
//		String regex = "[^a-zA-Z0-9]";
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher("123254fasdADFsf12312=-=");
//		boolean result = m.find();
//		System.out.println(result);

//		String regex = "[A-Za-z0-9\\-\\.]+.jar";
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher("commons-codec-1.9.jar");
//		boolean matched = m.matches();
//		System.out.println(matched);
//		boolean result = m.find();
//		System.out.println(result);

//		String regex = "lib";
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher("lib");
//		boolean matched = m.matches();
//		System.out.println(matched);
//		boolean result = m.find();
//		System.out.println(result);
		
		String regex = "Uar_103_01_IP_session_60_20170111_212300_20170111_212359.csv";
		String regex1 = "LTE_VOIP_002113305707_20170117080200.csv";
		String regex2 = "LTE.csv";
		Pattern p = Pattern.compile(regex2);
		Matcher m = p.matcher("L*");
		boolean matched = m.matches();
		System.out.println(matched);
		boolean result = m.find();
		System.out.println(result);
	}
}
