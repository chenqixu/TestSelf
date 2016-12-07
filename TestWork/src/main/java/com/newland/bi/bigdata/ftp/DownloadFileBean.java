package com.newland.bi.bigdata.ftp;

public class DownloadFileBean {
	private String filename = "";
	private String filepath = "";
	public DownloadFileBean(String _filename, String _filepath){
		this.filename = _filename;
		this.filepath = _filepath;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}	
}
