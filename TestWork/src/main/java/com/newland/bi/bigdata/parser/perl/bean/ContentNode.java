package com.newland.bi.bigdata.parser.perl.bean;

public class ContentNode implements Node {
	private StringBuffer content = new StringBuffer();
	
	public ContentNode(){}
	
	public ContentNode(String content){
		add(content);
	}

	@Override
	public String getContent() {
		return content.toString();
	}

	@Override
	public void add(String content) {
		this.content.append(content);
	}

	@Override
	public String toString(){
		return this.content.toString();
	}
}
