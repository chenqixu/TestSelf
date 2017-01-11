package com.newland.bi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.newland.bi.excel.ExcelSheetList;
import com.newland.bi.excel.ExcelUtils;

public class ExcelToTxt {
	public static String getFileName(String filename){
		return filename.substring(0,filename.indexOf("."));
	}
	
	public static void main(String[] args) {
		String path = "";
		String writefilepath = "";
		if(args!=null && args.length==1){
			path = args[0];
		}else{
			System.out.println("no args.Please check.");
			System.exit(-1);
		}
		File file = new File(path);
		String writefilename = getFileName(file.getName());
		writefilepath = file.getParent()+File.separator +writefilename+".tmp";
		BufferedWriter writer = null;
		StringBuffer sb = new StringBuffer();
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null && list.size()>=1) {
				File writeFile = new File(writefilepath);
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(writeFile), "UTF-8"));				
				// 读取并转换第一个sheet
				for (int i=0; i<1; i++) {
					// 循环sheet每一行
					for (int j=0; j<list.get(i).getSheetList().size(); j++) {
						for(int x=0;x<list.get(i).getSheetList().get(j).size();x++){
							// 替换内容中的换行符为空格
							sb.append(list.get(i).getSheetList().get(j).get(x).replaceAll("\n", " ")+"\t");
						}
						sb.deleteCharAt(sb.length()-1);
						sb.append("\n");
					}
				}
				writer.write(sb.toString());
				writer.flush();
				writer.close();
				File newfile = new File(writeFile.getParent()+File.separator +writefilename+".txt");
				if(newfile.exists()){
					newfile.delete();
					writeFile.renameTo(newfile);
				}else{
					writeFile.renameTo(newfile);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
