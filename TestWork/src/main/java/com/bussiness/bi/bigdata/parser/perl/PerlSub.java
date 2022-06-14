package com.bussiness.bi.bigdata.parser.perl;

public class PerlSub {
	public static final String KeyWord = "sub ";
	public static final String Blank = "";
	private StringBuffer content = new StringBuffer();
	private int left_brace_cnt;
	private int right_brace_cnt;

	public String getContent() {
		return content.toString()+"}";
	}

	public void appendContent(String content) {
		this.content.append(content);
	}
	
	public static String getSubName(String str){
		return str.replace(KeyWord, Blank).trim();
	}
	
	public void increaseLeftBrace(){
		left_brace_cnt++;
	}
	
	public void increateRigthBrace(){
		right_brace_cnt++;
	}
	
	public void increaseLeftBraceArr(Integer[] in){
		int size = in.length;
		while(size-->0){
			increaseLeftBrace();
		}
	}
	
	public void increateRigthBraceArr(Integer[] in){
		int size = in.length;
		while(size-->0){
			increateRigthBrace();
		}
	}
	
	public int getLeftBrace(){
		return left_brace_cnt;
	}
	
	public int getRigthBrace(){
		return right_brace_cnt;
	}
	
	public boolean isEnd(){
		if(left_brace_cnt>0 && right_brace_cnt>0)
			return left_brace_cnt==right_brace_cnt;
		else
			return false;
	}
}
