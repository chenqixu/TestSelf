package com.newland.bi.bigdata.mavendp;

public class MavendpComm {
	public static String programpath = System.getProperty("user.dir");
	public static final String readfile = "/src/main/resources/conf/maven_dependency_tree.txt";
	public static final String read_code = "UTF-8";
	public static final String writepom = "/src/main/resources/conf/maven_dependency_tree_pom.xml";
	public static final String pom = "/src/main/resources/conf/pom.xml";
	public static final String write_code = "UTF-8";
	public static final String tab = "\r\n";
	public static final String start_str = "[INFO] +- ";
	public static final String end_str = "[INFO] \\- ";
	public static final String other_str = "- ";
	public static final String scan_rule = "pom\\.xml";
}
