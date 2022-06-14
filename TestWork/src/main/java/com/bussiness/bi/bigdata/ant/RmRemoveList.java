package com.bussiness.bi.bigdata.ant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import com.bussiness.bi.bigdata.changecode.ChangeCode;

/**
 * 移除清单文件中已经移除的部分，以R开头
 * */
public class RmRemoveList extends ChangeCode {

	@Override
	public void change(){
		List<String> changelist = scan(this.getScan_path());
		BufferedReader reader = null;
		BufferedWriter writer = null;	
		try{
			//read and write
			for(int i=0;i<changelist.size();i++){
				File readFile = new File(changelist.get(i));
				File writeFile = new File(changelist.get(i)+"bak");
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(readFile), this.getRead_code()));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(writeFile), this.getWrite_code()));
				String _tmp = null;
				while((_tmp=reader.readLine())!=null){
					if(_tmp.indexOf("R ")==0){
					}else{
						writer.write(_tmp);
						writer.write("\r\n");
					}
				}
				reader.close();
				writer.flush();
				writer.close();
				if(readFile.exists()){
					boolean delete = readFile.delete();
					if(delete){
						writeFile.renameTo(readFile);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	
	public static void main(String[] args) {
		RmRemoveList cc = new RmRemoveList();
		cc.setScan_path("d:/Work/ETL/编译/List");
		cc.setScan_rule(".*\\.list");
		cc.setRead_code("GBK");
		cc.setWrite_code("GBK");
		cc.setLoop(false);// 不需要-r
		cc.change();
	}
}
