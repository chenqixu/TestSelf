package com.newland.bi.bigdata.excel;

import java.util.List;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.apache.commons.lang.StringUtils;

public class LoopholeRectificationClient {
	private static MyLogger logger = MyLoggerFactory.getLogger(LoopholeRectificationClient.class);
	private List<ExcelSheetList> list = null;
	private ExcelUtils eu = new ExcelUtils();
	
	public LoopholeRectificationClient(){
	}
	
	public void run(String path, String sheetName){
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if(StringUtils.isNotEmpty(sheetName)){
						if(!list.get(i).getSheetName().equals(sheetName))continue;
					}
					// 循环sheet每一行
					for (int j=0; j<list.get(i).getSheetList().size(); j++) {
						// 打印第二列
						String ip = list.get(i).getSheetList().get(j).get(1).replaceAll("\r|\n", "");
						if(ip.contains("10.48.236.208") || ip.contains("10.48.236.209"))
							logger.info(list.get(i).getSheetList().get(j).toString().replaceAll("\r|\n", ""));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		LoopholeRectificationClient lrc = new LoopholeRectificationClient();
		lrc.run("d:\\tmp\\大数据平台应用漏洞.xls", "应用漏洞");
	}
}
