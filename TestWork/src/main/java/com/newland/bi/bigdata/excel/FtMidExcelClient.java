package com.newland.bi.bigdata.excel;

import java.io.IOException;
import java.util.List;

import com.newland.bi.bigdata.bean.ExcelSheetList;

public class FtMidExcelClient {
	public static final String TABLE_NAME = "查询表名：";
	public static final String FIELD_NAME = "字段名";
	public static final String FIELD_TAG = "字段标识(hive)";
	public static final String FIELD_TYPE = "属性类型(hive)";
	public static final String PARTITION = "partition";
	public static final String NULL_STR = "";

	public static final String SEMICOLON = ";";
	public static final String COMMA = ",";
	public static final String SPACE = " ";
	public static final String EQUAL = "=";
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";
	public static final String CREATE_HIVE_TABLE = "create  table IF NOT EXISTS ";
	public static final String PARTITIONED = ") partitioned by (";
	public static final String NEWLINE = "\n";
	public static final String PARTITION_FILED_TYPE = "string";
	public static final String DROP_TABLE = "drop table ";
	
	/**
	 * 表名：搜索每行首列”查询表名：“
	 * 字段：搜索每行首列”字段名“，抽取”字段标识(hive)“，”属性类型(hive)“两列数据
	 * 分区：”属性类型(hive)“=partition
	 * */
	public static String getCreateTableStr(String path) {
		StringBuffer sb = new StringBuffer("");
		StringBuffer partitionsb = new StringBuffer("");
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if (list.get(i).getSheetList().size()>4 
							&& list.get(i).getSheetList().get(4).get(0).equals(TABLE_NAME)) {
						String _table_name = list.get(i).getSheetList().get(4).get(1);
						sb.append(CREATE_HIVE_TABLE+_table_name+LEFT_BRACKET);
						// 循环row
						int _start_row = -1;
						int _end_row = -1;
						int Field_position = -1;
						int Field_type_location = -1;
						for (int j=0; j<list.get(i).getSheetList().size(); j++) {
							if (list.get(i).getSheetList().get(j).size()>0 && list.get(i).getSheetList().get(j).get(0).equals(FIELD_NAME)) {
								_start_row = j+1;
								for (int n=0;n<list.get(i).getSheetList().get(j).size(); n++) {
									if (list.get(i).getSheetList().get(j).get(n).equals(FIELD_TAG)) {
										Field_position = n;
										Field_type_location = n+1;
										_end_row = -1;
										break;
									}
								}
							}
							if (list.get(i).getSheetList().get(j).size()>0 && list.get(i).getSheetList().get(j).get(0).equals(NULL_STR)) {
								_end_row = j-1;
							}
							if (list.get(i).getSheetList().get(j).size()>0 
									&& !list.get(i).getSheetList().get(j).get(0).equals(FIELD_NAME) 
									&& _start_row>0 
									&& _end_row<0
									&& !list.get(i).getSheetList().get(j).get(Field_position).equals("")) {								
								if (list.get(i).getSheetList().get(j).get(Field_type_location).equals(PARTITION)) {
									partitionsb.append(list.get(i).getSheetList().get(j).get(Field_position)+SPACE
											+PARTITION_FILED_TYPE+COMMA);
								} else {
									sb.append(list.get(i).getSheetList().get(j).get(Field_position)+SPACE
										+list.get(i).getSheetList().get(j).get(Field_type_location)+COMMA);
								}
							}
						}
						sb.deleteCharAt(sb.length()-1);
						partitionsb.deleteCharAt(partitionsb.length()-1);
						sb.append(PARTITIONED);
						sb.append(partitionsb);
						sb.append(RIGHT_BRACKET);
						sb.append(SEMICOLON);
						sb.append(NEWLINE);
						partitionsb.delete(0, partitionsb.length());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String getDropTableStr(String path) {
		StringBuffer sb = new StringBuffer("");
		List<ExcelSheetList> list = null;
		ExcelUtils eu = new ExcelUtils();
		try {
			list = eu.readExcel(path);
			if (list != null) {
				// 循环sheet
				for (int i=0; i<list.size(); i++) {
					if (list.get(i).getSheetList().size()>4 
							&& list.get(i).getSheetList().get(4).get(0).equals(TABLE_NAME)) {
						String _table_name = list.get(i).getSheetList().get(4).get(1);
						sb.append(DROP_TABLE+_table_name+SEMICOLON);
						sb.append(NEWLINE);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String path = "d:/Work/CVS/BI/SSC/NETLOGANALYSIS/Develop/Design/DatabaseDesign/NETLOGANALYSIS_中间表设计.xlsx";
		System.out.println(getDropTableStr(path));
		System.out.println(getCreateTableStr(path));
	}
}
