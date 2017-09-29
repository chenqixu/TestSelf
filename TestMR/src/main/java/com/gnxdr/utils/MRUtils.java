package com.gnxdr.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import com.gnxdr.bean.CombineEntity;
import com.gnxdr.bean.Record;
import com.gnxdr.constant.Constants;

public class MRUtils {


	private static StringBuilder sb = new StringBuilder();
	private static Map<String, String> map = new HashMap<String, String>();

	
	/**
	 * 删除指定路径
	 * @param fs
	 * @param path
	 * @throws IOException 
	 * @
	 */
	public static void delPath(FileSystem fs, String path) throws IOException {

		Path p = new Path(path);
		if (fs.exists(p)) {
			fs.delete(p, true);
			System.out.println("=====delPath 删除：" + p.toString() + "=====");
		} else {
			System.out.println("=====delPath 文件不存在：" + p.toString() + "=====");
		}

	}

	/**
	 * 根据正则表达式，删除匹配的文件路径
	 * @param fs
	 * @param path 文件所在的目录
	 * @param regex	正则表达式
	 * @throws IOException
	 */
	public static void delPathByRegex(FileSystem fs, String path, String regex)
			throws IOException {

		Path inputDir = new Path(path);
		if (fs.exists(inputDir)) {
			System.out.println("=>delPathByRegex input path:" + inputDir.toString());
			FileStatus[] files = fs.globStatus(new Path(path + Constants.SEPARATOR + "*"), new RegexIxcludePathFilter(regex));
			if (files.length > 0) {
				for (FileStatus file : files) {
					String fileName = file.getPath().getName();
					fs.delete(file.getPath(), false);
					System.out.println("delPathByRegex 删除：" + fileName);
				}
			} else {
				System.out.println("delPathByRegex:"+path+Constants.SEPARATOR+regex+" no matched files~");
			}
		}else {
			System.out.println("=====delPathByRegex 路径不存在：" + inputDir.toString() + "=====");
		}
	}

	/**
	 * 将逗号分隔符的字符串拆分解析成map
	 * @param columnNames	列名
	 * @param line	带'逗号分隔符的字符串
	 * @return
	 */
	public static Map<String, String> transformLineToMap(String[] columnNames,
			String line) {

		String[] columnValues = line.split(Constants.COMMA_SEPARATOR, -1);
		if (columnNames == null || columnValues == null
				|| columnNames.length != columnValues.length) {
			return null;
		}
		map.clear();
		for (int i = 0; i < columnNames.length; i++) {
			map.put(columnNames[i].toUpperCase(), columnValues[i] == null ? ""
					: columnValues[i]);
		}
		return map;

	}

	/**
	 * 获得需要的字段
	 * @param map	所有字段与对应的值
	 * @param submitField	需要提取的字段名
	 * @return
	 */
	public static String getSubmitField(Map<String, String> map,
			String submitField) {

		sb.setLength(0);
		String[] fileds = submitField.split(Constants.COMMA_SEPARATOR, -1);
		for (String temp : Arrays.asList(fileds)) {
			sb.append(map.get(temp)).append(Constants.COMMA_SEPARATOR);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);

	}

	/**
	 * 将CombineEntity转换成Record
	 * @param entity
	 * @return Record
	 */
	public static Record ValuesToRecordBean(CombineEntity entity) {
		Record bean = new Record();
		if (null == entity.getContent()
				|| "".equals(entity.getContent().toString())) {
			return null;
		}
		List<String> valueList = Arrays.asList(entity.getContent().toString()
				.split(Constants.COMMA_SEPARATOR, -1));
		Iterator<String> iterator = valueList.iterator();
		if (entity.getFlag().toString().equals(Constants.HTTP_FILE_FLAG)) {
			if (valueList.size() < 10) {
				return null;
			}
			bean.setFlag(Constants.HTTP_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setHost(iterator.next());
			bean.setUri(iterator.next());
			bean.setApp_class(iterator.next());
			bean.setApp_class_top(iterator.next());
			bean.setWeb_classify(iterator.next());
			bean.setWeb_name(iterator.next());
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
			bean.setUser_agent(iterator.next());

		} else if (entity.getFlag().toString().equals(Constants.RTSP_FILE_FLAG)) {
			if (valueList.size() < 9) {
				return null;
			}
			bean.setFlag(Constants.RTSP_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setRtp_server_ip(iterator.next());
			bean.setApp_class(iterator.next());
			bean.setApp_class_top(iterator.next());
			bean.setWeb_classify(iterator.next());
			bean.setWeb_name(iterator.next());
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());

		} else if (entity.getFlag().toString()
				.equals(Constants.EMAIL_FILE_FLAG)) {
			if (valueList.size() < 9) {
				return null;
			}
			bean.setFlag(Constants.EMAIL_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setApp_class("");
			bean.setApp_class_top("");
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
			bean.setUser_name(iterator.next());

		} else if (entity.getFlag().toString().equals(Constants.IP_FILE_FLAG)) {
			if (valueList.size() < 28) {
				return null;
			}
			bean.setFlag(Constants.IP_FILE_FLAG);
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setApp_class(iterator.next());
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
			bean.setRat(iterator.next());
			bean.setLac(iterator.next());
			bean.setCid(iterator.next());
			bean.setCharge_id(iterator.next());
			bean.setMsisdn(iterator.next());
			bean.setTerminal_model(iterator.next());
			bean.setApn(iterator.next());
			bean.setImei(iterator.next());
			bean.setStart_time(iterator.next());
			bean.setEnd_time(iterator.next());
			bean.setImsi(iterator.next());
			bean.setL4_protocol(iterator.next());
			bean.setApp_class_top(iterator.next());
			bean.setServer_prot(iterator.next());
			bean.setDuration(iterator.next());
			bean.setUl_tcp_disordered_packets(iterator.next());
			bean.setDl_tcp_disordered_packets(iterator.next());
			bean.setUl_tcp_retransmission_packets(iterator.next());
			bean.setDl_tcp_retransmission_packets(iterator.next());
			bean.setUl_ip_frag_packets(iterator.next());
			bean.setDl_ip_frag_packets(iterator.next());

		} else {
			if (valueList.size() < 8) {
				return null;
			}
			bean.setFlag(entity.getFlag().toString());
			bean.setIp_id(iterator.next());
			bean.setServer_ip(iterator.next());
			bean.setApp_class("");
			bean.setApp_class_top("");
			bean.setUp_data(iterator.next());
			bean.setDown_data(iterator.next());
			bean.setUp_ip_pkgs(iterator.next());
			bean.setDown_ip_pkgs(iterator.next());
		}
		return bean;
	}

	private static long tempLong;
	private static Random random = new Random();

	/**
	 * 根据手机号取模
	 * @param telnumber
	 * @return
	 */
	public static String generateModKey(long telnumber) {

		random.setSeed(telnumber);
		tempLong = Math.abs(random.nextLong() % 1024);
		if (tempLong < 10) {
			return "000" + tempLong;
		} else if (tempLong < 100) {
			return "00" + tempLong;
		} else if (tempLong < 1000) {
			return "0" + tempLong;
		} else if (tempLong < 1024) {
			return "" + tempLong;
		}
		return null;
	}

	private static SimpleDateFormat newDataFormat1 = new SimpleDateFormat(
			Constants.ROWKEY_STARTTIME_FORMAT);
	private static SimpleDateFormat newDataFormat2 = new SimpleDateFormat(
			Constants.ROWVALUE_STARTTIME_FORMAT);
	private static SimpleDateFormat newDataFormat3 = new SimpleDateFormat(
			Constants.PERIOD_OF_TIME_FORMAT);
	private static SimpleDateFormat newDataFormat4 = new SimpleDateFormat(
			Constants.SUM_DATE_FORMAT);

	public static String getDateTimeKey(String dateTime) throws Exception {

		return newDataFormat1.format(new Date(Long.valueOf(dateTime)));
	}

	public static String getDateTimeValue(String dateTime) throws Exception {

		return newDataFormat2.format(new Date(Long.valueOf(dateTime)));
	}

	public static String getPeriodOfTimeFormat(String dateTime)
			throws Exception {

		return newDataFormat3.format(new Date(Long.valueOf(dateTime)));
	}

	public static String getSumDateFormat(String dateTime) throws Exception {

		return newDataFormat4.format(new Date(Long.valueOf(dateTime)));
	}

	/**
	 * 检查记录是否符合入库hbase的条件（key是否完整）
	 * @param record
	 * @return
	 */
	public static boolean checkFitToHbase(Record record) {

		if (StringUtils.isBlank(record.getIp_id())
				|| StringUtils.isBlank(record.getIp_id().trim())) {
			return false;
		}
		if (StringUtils.isBlank(record.getApn())
				|| StringUtils.isBlank(record.getApn().trim())) {
			return false;
		}
		if (StringUtils.isBlank(record.getMsisdn())
				|| StringUtils.isBlank(record.getMsisdn().trim())) {
			return false;
		}
		if (StringUtils.isBlank(record.getStart_time())
				|| StringUtils.isBlank(record.getStart_time().trim())) {
			return false;
		}
		return true;
	}

	public static String numberTemp;

	/**
	 * 保留4位 小数
	 * @param doublenum
	 * @return
	 */
	public static String doubleFormat(Double doublenum) {

		BigDecimal bg = new BigDecimal(doublenum);
		numberTemp = bg.setScale(4, BigDecimal.ROUND_HALF_UP).toString();
		if (StringUtils.isBlank(numberTemp)) {
			return null;
		} else {
			return numberTemp;
		}
	}

	/**
	 * 将关联上的记录合并成一个bean
	 * @param leftRecord
	 * @param rightRecord
	 * @param isRelated
	 * @return
	 */
	public static Record unitAsOne(Record leftRecord, Record rightRecord,
			boolean isRelated) {

		Record unitOne = leftRecord;

		if (Constants.HTTP_FILE_FLAG.equals(rightRecord.getFlag())) {
			unitOne.setUri(rightRecord.getUri());
			unitOne.setHost(rightRecord.getHost());
			unitOne.setUser_agent(rightRecord.getUser_agent());
			unitOne.setWeb_classify(rightRecord.getWeb_classify());
			unitOne.setWeb_name(rightRecord.getWeb_name());
			
		} else if (Constants.RTSP_FILE_FLAG.equals(rightRecord.getFlag())) {
			unitOne.setRtp_server_ip(rightRecord.getRtp_server_ip());
			unitOne.setWeb_classify(rightRecord.getWeb_classify());
			unitOne.setWeb_name(rightRecord.getWeb_name());

		} else if (Constants.MMS_FILE_FLAG.equals(rightRecord.getFlag())) {
			unitOne.setUri(rightRecord.getUri());

		} else if (Constants.EMAIL_FILE_FLAG.equals(rightRecord.getFlag())) {
			unitOne.setUser_name(rightRecord.getUser_name());

		}

		if (!StringUtils.isBlank(rightRecord.getServer_ip())
				&& !StringUtils.isBlank(rightRecord.getServer_ip().trim())) {
			unitOne.setServer_ip(rightRecord.getServer_ip());
		}

		if (StringUtils.isBlank(leftRecord.getApp_class()) && !StringUtils.isBlank(rightRecord.getApp_class())) {
			unitOne.setApp_class(rightRecord.getApp_class());
		}

		if (StringUtils.isBlank(leftRecord.getApp_class_top()) && !StringUtils.isBlank(rightRecord.getApp_class_top())) {
			unitOne.setApp_class_top(rightRecord.getApp_class_top());
		}

		// ip至少有关联上一条http记录，流量均摊的处理
		if (isRelated) {
			if (!StringUtils.isBlank(rightRecord.getUp_data())
					&& !StringUtils.isBlank(rightRecord.getUp_data().trim())) {
				unitOne.setUp_data(rightRecord.getUp_data());
			}
			if (!StringUtils.isBlank(rightRecord.getDown_data())
					&& !StringUtils.isBlank(rightRecord.getDown_data().trim())) {
				unitOne.setDown_data(rightRecord.getDown_data());
			}
			if (!StringUtils.isBlank(rightRecord.getUp_ip_pkgs())
					&& !StringUtils.isBlank(rightRecord.getUp_ip_pkgs().trim())) {
				unitOne.setUp_ip_pkgs(rightRecord.getUp_ip_pkgs());
			}
			if (!StringUtils.isBlank(rightRecord.getDown_ip_pkgs())
					&& !StringUtils.isBlank(rightRecord.getDown_ip_pkgs()
							.trim())) {
				unitOne.setDown_ip_pkgs(rightRecord.getDown_ip_pkgs());
			}

		}
		leftRecord.setFlag(rightRecord.getFlag());// 表示本条IP记录是被哪个HTTP业务关联的
		return unitOne;
	}

	/**
	 * 正则表达式匹配输入文件
	 * @param job
	 * @param fs
	 * @param path
	 * @param require
	 * @param regex
	 * @param map
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void addInputPath(Job job, FileSystem fs, String path,
			boolean require, String regex, Class map)
			throws FileNotFoundException, IOException {

		Path inputDir = new Path(path);
		if (fs.exists(inputDir)) {
			System.out.println("=>input path:" + inputDir.toString());
//			FileStatus[] files = fs.globStatus(new Path(path
//					+ Constants.SEPARATOR + "*"), new RegexIxcludePathFilter(
//					regex));
			 FileStatus[] files = fs.listStatus(inputDir);
			for (FileStatus file : files) {
				// if (validate(file, regex, require)) {
				System.out.println("input file:" + file.getPath().getName());
				MultipleInputs.addInputPath(job, file.getPath(),
						TextInputFormat.class, map);
				// }

			}
		} else {
			System.out.println("The Path:" + inputDir + " is not exists!");
		}
	}

	/**
	 * 正则表达式过滤器类
	 */
	private static class RegexIxcludePathFilter implements PathFilter {
		private final String regex;

		public RegexIxcludePathFilter(String regex) {
			this.regex = regex;
		}

		public boolean accept(Path path) {
			return path.toString().matches(regex);
		}
	}

	/**
	 * 筛选输入文件(此方法暂时无用)
	 */
	public static boolean validate(FileStatus status, String regex,
			boolean require) {
		if (status.isDir()) {
			return false;
		}
		// 文件正则校验
		if (require) {
			return status.getPath().getName().matches(regex);
		}
		return true;
	}
}
