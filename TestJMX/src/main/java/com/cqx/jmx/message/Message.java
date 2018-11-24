package com.cqx.jmx.message;

public class Message {
	private String title, body, by;
	public Message() {
		title="none";
		body="none";
		by="none";
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public void echo() {
        System.out.println("<"+title+">");
        System.out.println(body);
        System.out.println("by " + by);
    }
}
