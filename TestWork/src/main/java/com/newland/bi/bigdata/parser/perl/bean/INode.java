package com.newland.bi.bigdata.parser.perl.bean;

import java.util.ArrayList;
import java.util.List;

public class INode {
	private Node content;
	private INode parent;
	private List<INode> child = null;
	public INode(){
		child = new ArrayList<INode>();
	}
	public Node getContent() {
		return content;
	}
	public void setContent(Node content) {
		this.content = content;
	}
	public INode getParent() {
		return parent;
	}
	public void setParent(INode parent) {
		this.parent = parent;
	}
	public List<INode> getChildList() {
		return child;
	}
	public INode getChildByIndex(int index) {
		return child.get(index);
	}
	public INode getChild() {
		if(child.size()>0)
			return child.get(0);
		else
			return null;
	}
	public void setChild(List<INode> child) {
		this.child = child;
	}
	public void addChild(INode child){
		this.child.add(child);
	}
	public boolean hasChild(){
		return child!=null;
	}
}
