package com.gnxdr.mr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.gnxdr.bean.CombineEntity;
import com.gnxdr.bean.Record;
import com.gnxdr.constant.Constants;
import com.gnxdr.constant.Constants.COUNTER_ENUM;
import com.gnxdr.utils.MRUtils;

/**
 * @version V1.0.1 cqx modify by 68712-1 增加web_name解析
 * */
public class JoinReducer extends Reducer<Text, CombineEntity, Text, Text> {
	private List<Record> leftRecordBeans = new ArrayList<Record>();
	private List<Record> rightRecordBeans = new ArrayList<Record>();
	private Record record = null;

	private double upbytes;
	private double downbytes;
	private double uppkgs;
	private double downpkgs;

	private double sumUptype;
	private double sumDowntype;
	private double sumUppkgs;
	private double sumDownpkgs;

	private double upbytesDifference;
	private double downbytesDifference;
	private double uppkgsDifference;
	private double downpkgsDifference;

	private int statisCount;

	private Configuration conf = null;//配置
	private FSDataOutputStream stm = null;//输出流
	private BufferedWriter outputBW = null;//输出流
	private FileSystem fs = null;//gpfs文件系统
	private List<Record> normalHttpRecordList = new ArrayList<Record>();
	private Map<String, String> oracleMobileRecords = new HashMap<String, String>();

	private MultipleOutputs<Text, Text> rmos;
	private Counter mapTransportErrorCounter;
	private Counter mapIpTransportErrorCounter;
	private Counter numFormatErrorCounter;
	private Counter hbasefileSuccessCounter;
	private Counter errorCounter;
	private Counter joinedCounter;
	private Counter unjoinedCounter;
	private Counter unjoinedhttpCounter;

	private Counter counter1;
	private Counter counter2;
	private Counter counter3;
	
	private Map<String, String> appNameRecords = new HashMap<String, String>();//appName
	private Map<String, String> webNameRecords = new HashMap<String, String>();//webName V1.0.1

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		//多文件输出
		rmos = new MultipleOutputs<Text, Text>(context);
		//获得配置
		conf = context.getConfiguration();
		//计数器
		mapTransportErrorCounter = context
				.getCounter(COUNTER_ENUM.mapTransportErrorCounterEnum);
		mapIpTransportErrorCounter = context
				.getCounter(COUNTER_ENUM.mapIpTransportErrorCounter);
		numFormatErrorCounter = context
				.getCounter(COUNTER_ENUM.numFormatErrorCounterEnum);
		hbasefileSuccessCounter = context
				.getCounter(COUNTER_ENUM.hbasefileSuccessCounterEnum);
		errorCounter = context.getCounter(COUNTER_ENUM.errorCounterEnum);
		joinedCounter = context.getCounter(COUNTER_ENUM.joinedCounterEnum);
		unjoinedCounter = context.getCounter(COUNTER_ENUM.unjoinedCounterEnum);
		unjoinedhttpCounter = context
				.getCounter(COUNTER_ENUM.unjoinedhttpCounterEnum);

		counter1 = context.getCounter(COUNTER_ENUM.counter1Enum);
		counter2 = context.getCounter(COUNTER_ENUM.counter2Enum);
		counter3 = context.getCounter(COUNTER_ENUM.counter3Enum);

		//通过配置获得gpfs的文件系统
		fs = FileSystem.get(conf);
		//任务id
		int partition = context.getTaskAttemptID().getTaskID().getId();
		// 判断该次task是否是第一次，如果不是第一次，则先删除原先的文件再重新创建一个空的
		Path path = new Path(conf.get(Constants.HBASE_OUTPUT_FILE)
				+ conf.get(Constants.TASK_HOUR) + Constants.SEPARATOR
				+ conf.get(Constants.JOB_DATAN).toLowerCase() + "/hbase_"
				+ conf.get(Constants.JOB_DATAN) + "_" + partition);
		// 判断文件是否存在，如果存在则删除清空，如果不存在则新建
		if (!fs.exists(path)) {
			stm = fs.create(path);
		} else {
			fs.delete(path, true);
			stm = fs.create(path);
		}
		//utf-8输出流文件
		outputBW = new BufferedWriter(new OutputStreamWriter(stm, "utf-8"));
		
		//通过配置获得oracle配置表的hdfs路径(关联oracle表在gpfs中的位置)
		//imei和手机关系配置
		String uri = conf.get(Constants.ORACLE_TABLEFILE_IN_HDFS);
		if (StringUtils.isNotBlank(uri)) {
			FSDataInputStream in = null;
			in = fs.open(new Path(uri));
			if (in == null) {
				System.out.println("终端配置文件不存在!");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String line = br.readLine();
			while (null != line) {
				String[] model_desc = line.split(",", 2);// model_desc[0]:01300100
				// model_desc[1]:01,Iphone
				// 4S
				oracleMobileRecords.put(model_desc[0], model_desc[1]);
				line = br.readLine();
			}
			br.close();
			in.close();
		}
		
		// 获取应用名称配置文件(app配置信息表在gpfs中的位置)
		//app配置
		String appNameTable = conf.get(Constants.APP_NAME_TABLE);
		if (StringUtils.isNotBlank(appNameTable)) {
			// 创建输入流，关联配置文件
			FSDataInputStream inAppname = null;
			inAppname = fs.open(new Path(appNameTable));
			if (inAppname == null) {
				System.out.println("app名称配置文件不存在!");
			}
			BufferedReader brAppname = new BufferedReader(
					new InputStreamReader(inAppname, "utf-8"));
			// 按行读取
			String lineServicename = brAppname.readLine();
			while (null != lineServicename) {
				// 根据","分割，根据应用大类App_Type和应用小类App_Sub_type，获得应用小类的名称（即特征库中的二级分类（应用））
				String[] servicenameFileArr = lineServicename.split(",", -1);
				appNameRecords.put(servicenameFileArr[0], servicenameFileArr[1]);
				lineServicename = brAppname.readLine();
			}
			brAppname.close();
			inAppname.close();
		}
		
		//web配置 V1.0.1
		
		
		
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		//多文件输出关闭
		rmos.close();
		//输出流关闭 buffer writer
		outputBW.close();
		//输出流 data stream
		stm.close();
		//gpfs文件系统关闭
		fs.close();
	}

	@Override
	protected void reduce(Text key, Iterable<CombineEntity> values,
			Context context) throws IOException, InterruptedException {

		// try {
		// left ip
		// right http
		//左记录
		leftRecordBeans.clear();
		//右记录
		rightRecordBeans.clear();
		for (CombineEntity value : values) {
			//如果是IP记录，把记录转成Record并加入左记录
			if (Constants.IP_FILE_FLAG.equals(value.getFlag().toString())) {
				record = MRUtils.ValuesToRecordBean(value);
				if (record == null) {
					mapIpTransportErrorCounter.increment(1);// 本条IP记录异常，抛弃并且不做关联
					return;
				}
				leftRecordBeans.add(record);
			}
			//如果不是IP记录，把记录转成Record并加入右记录
			else {
				record = MRUtils.ValuesToRecordBean(value);
				if (record == null) {
					mapTransportErrorCounter.increment(1);// 本条HTTP记录异常，抛弃
					continue;
				}
				rightRecordBeans.add(record);
			}
		}

		//没看懂??
		if ("true".equals(conf.get(Constants.DEBUG))) {
			if (leftRecordBeans.size() == 0) {
				counter1.increment(1);
			} else if (leftRecordBeans.size() == 1) {
				counter2.increment(1);
			} else {
				counter3.increment(1);// ip_id有重复
			}
		}

		//循环IP记录
		for (Record leftRecord : leftRecordBeans) {

			upbytes = 0;
			downbytes = 0;
			uppkgs = 0;
			downpkgs = 0;
			sumUptype = 0;
			sumDowntype = 0;
			sumUppkgs = 0;
			sumDownpkgs = 0;
			upbytesDifference = 0;
			downbytesDifference = 0;
			uppkgsDifference = 0;
			downpkgsDifference = 0;
			statisCount = 0;
			normalHttpRecordList.clear();
			// 至少关联到一条http记录
			if (rightRecordBeans.size() > 0) {

				String uperrseq;
				String downerrseq;
				String updup;
				String downdup;
				String upfrag;
				String downfrag;
				for (Record rightRecord : rightRecordBeans) {
					try {
						upbytes = Double.valueOf(rightRecord.getUp_data());
						downbytes = Double.valueOf(rightRecord.getDown_data());
						uppkgs = Double.valueOf(rightRecord.getUp_ip_pkgs());
						downpkgs = Double
								.valueOf(rightRecord.getDown_ip_pkgs());
						sumUptype += upbytes;
						sumDowntype += downbytes;
						sumUppkgs += uppkgs;
						sumDownpkgs += downpkgs;
						statisCount++;
						normalHttpRecordList.add(rightRecord);
					} catch (NumberFormatException nfe) {
						numFormatErrorCounter.increment(1);
						continue;
					}

				}
				if (statisCount == 0) {
					statisCount = 1;
				}
				try {
					upbytesDifference = (Double
							.valueOf(leftRecord.getUp_data()) - sumUptype)
							/ statisCount;
					downbytesDifference = (Double.valueOf(leftRecord
							.getDown_data()) - sumDowntype)
							/ statisCount;
					uppkgsDifference = (Double.valueOf(leftRecord
							.getUp_ip_pkgs()) - sumUppkgs)
							/ statisCount;
					downpkgsDifference = (Double.valueOf(leftRecord
							.getDown_ip_pkgs()) - sumDownpkgs)
							/ statisCount;

					uperrseq = String.valueOf(Double.valueOf(leftRecord
							.getUl_tcp_disordered_packets())
							/ normalHttpRecordList.size());
					downerrseq = String.valueOf(Double.valueOf(leftRecord
							.getDl_tcp_disordered_packets())
							/ normalHttpRecordList.size());
					updup = String.valueOf(Double.valueOf(leftRecord
							.getUl_tcp_retransmission_packets())
							/ normalHttpRecordList.size());
					downdup = String.valueOf(Double.valueOf(leftRecord
							.getDl_tcp_retransmission_packets())
							/ normalHttpRecordList.size());
					upfrag = String.valueOf(Double.valueOf(leftRecord
							.getUl_ip_frag_packets())
							/ normalHttpRecordList.size());
					downfrag = String.valueOf(Double.valueOf(leftRecord
							.getDl_ip_frag_packets())
							/ normalHttpRecordList.size());

					leftRecord.setUl_tcp_disordered_packets(uperrseq);
					leftRecord.setDl_tcp_disordered_packets(downerrseq);
					leftRecord.setUl_tcp_retransmission_packets(updup);
					leftRecord.setDl_tcp_retransmission_packets(downdup);
					leftRecord.setUl_ip_frag_packets(upfrag);
					leftRecord.setDl_ip_frag_packets(downfrag);

				} catch (NumberFormatException nfe) {
					numFormatErrorCounter.increment(1);// 如果是ip数据异常，直接抛
					return;
				}
				for (Record normalHttpRecord : normalHttpRecordList) {
					try {
						// 如果总均摊流量为负的,则说明数据异常,保留http原来的流量值.均摊之后的值，保留小数点后4位
						if (upbytesDifference > 0) {
							normalHttpRecord.setUp_data(MRUtils
									.doubleFormat(Double
											.valueOf(normalHttpRecord
													.getUp_data())
											+ upbytesDifference));
						}
						if (downbytesDifference > 0) {
							normalHttpRecord.setDown_data(MRUtils
									.doubleFormat(Double
											.valueOf(normalHttpRecord
													.getDown_data())
											+ downbytesDifference));
						}
						if (uppkgsDifference > 0) {
							normalHttpRecord.setUp_ip_pkgs(MRUtils
									.doubleFormat(Double
											.valueOf(normalHttpRecord
													.getUp_ip_pkgs())
											+ uppkgsDifference));
						}
						if (downpkgsDifference > 0) {
							normalHttpRecord.setDown_ip_pkgs(MRUtils
									.doubleFormat(Double
											.valueOf(normalHttpRecord
													.getDown_ip_pkgs())
											+ downpkgsDifference));
						}

					} catch (NumberFormatException nfe) {
						numFormatErrorCounter.increment(1);// 如果是http记录异常，则跳过当前取下一条关联的http记录
						continue;
					}
				}
				if (normalHttpRecordList.size() > 0) {// 有至少一条正常的可以关联的http数据
					for (Record rightRecord : normalHttpRecordList) {
						Record unitrecord = MRUtils.unitAsOne(leftRecord,
								rightRecord, true);
						translateTerminalType(unitrecord);
						if (MRUtils.checkFitToHbase(leftRecord)) {// 该IP记录有key值，满足入Hbase条件
							if (recordToHbaseFile(context, unitrecord,appNameRecords)) {
								// 入hbasefile成功的同时，也写入清单文件
								setTime(unitrecord);
								joinedCounter.increment(1);

								 rmos
								 .write( conf.get(Constants.JOINED_OUTPUT_PREFIX)
										 +conf.get(Constants.TASK_HOUR)
										 + conf.get(Constants.JOB_DATAN),
								 NullWritable.get(),
								 unitrecord.getStatisticsCol());

							}
						}
					}
				} else {// 所有被关联上的http数据都有异常,则按照单条ip入库
					if (MRUtils.checkFitToHbase(leftRecord)) {// 该记录有key值，满足入Hbase条件
						translateTerminalType(leftRecord);
						if (recordToHbaseFile(context, leftRecord,appNameRecords)) {
							setTime(leftRecord);
							unjoinedhttpCounter.increment(1);

							rmos.write(conf.get(Constants.JOINED_OUTPUT_PREFIX)
									+conf.get(Constants.TASK_HOUR)
									+conf.get(Constants.JOB_DATAN),
									NullWritable.get(), leftRecord
											.getStatisticsCol());
						}
					}

				}

			} else {
				// 未关联到任何http记录
				if (MRUtils.checkFitToHbase(leftRecord)) {// 该记录有key值，满足入Hbase条件
					translateTerminalType(leftRecord);
					if (recordToHbaseFile(context, leftRecord,appNameRecords)) {
						setTime(leftRecord);
						unjoinedCounter.increment(1);
												
						rmos.write(conf.get(Constants.JOINED_OUTPUT_PREFIX)
								+conf.get(Constants.TASK_HOUR)
								+conf.get(Constants.JOB_DATAN), NullWritable
								.get(), leftRecord.getStatisticsCol());
					}
				}
			}
		}

		// }catch(InterruptedException e){
		// errorCounter.increment(1);
		// }catch (IOException e) {
		// errorCounter.increment(1);
		// }catch (Exception e) {
		// errorCounter.increment(1);
		// }
	}

	public boolean recordToHbaseFile(Context context, Record unitRecord, Map<String,String> appNameRecords) {
		StringBuffer rowkey = new StringBuffer();
		String tmpStartTime;
		try {
			tmpStartTime = MRUtils.getDateTimeKey(unitRecord.getStart_time());
		} catch (Exception e1) {
			e1.printStackTrace();
			errorCounter.increment(1);
			return false;
		}
		rowkey = rowkey.append(
				MRUtils.generateModKey(Long.valueOf(unitRecord.getMsisdn())))
				.append(Constants.COMMA_SEPARATOR).append(
						unitRecord.getMsisdn()).append(
						Constants.COMMA_SEPARATOR).append(tmpStartTime).append(
						Constants.COMMA_SEPARATOR).append(unitRecord.getApn())
				.append(Constants.COMMA_SEPARATOR)
				.append(unitRecord.getIp_id());

		StringBuffer sb = new StringBuffer();

		if (StringUtils.isNotBlank(unitRecord.getMsisdn())) {
			sb.append(unitRecord.getMsisdn());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getTerminal_model())) {
			sb.append(unitRecord.getTerminal_model());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getApn())) {
			sb.append(unitRecord.getApn());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getTerminaltype())) {
			sb.append(unitRecord.getTerminaltype());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getStart_time())) {
			try {
				sb.append(MRUtils.getDateTimeValue(unitRecord.getStart_time()));
			} catch (Exception e) {
				e.printStackTrace();
				errorCounter.increment(1);
				return false;
			}
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getEnd_time())) {
			try {
				sb.append(MRUtils.getDateTimeValue(unitRecord.getEnd_time()));
			} catch (Exception e) {
				e.printStackTrace();
				errorCounter.increment(1);
				return false;
			}
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getUp_data())) {
			sb.append(unitRecord.getUp_data());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getDown_data())) {
			sb.append(unitRecord.getDown_data());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getUrl())) {
			sb.append(unitRecord.getUrl());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getRat())) {
			sb.append(unitRecord.getRat());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getApp_class())) {
			sb.append(appNameRecords.get(unitRecord.getApp_class()));
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getLac())) {
			sb.append(unitRecord.getLac());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getCid())) {
			sb.append(unitRecord.getCid());
		}
		sb.append(",");
		if (StringUtils.isNotBlank(unitRecord.getCharge_id())) {
			sb.append(unitRecord.getCharge_id());
		}

		try {
			outputBW.write(rowkey.toString() + "\t" + sb.toString() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			errorCounter.increment(1);
			return false;
		}
		hbasefileSuccessCounter.increment(1);
		return true;

	}

	private void translateTerminalType(Record record) {
		String imei = record.getImei();
		if (StringUtils.isBlank(imei)) {
			return;
		}
		// 截取前八位和配置表的IMEI关联
		if (StringUtils.isBlank(imei) || imei.length() < 8) {
			return;
		}
		String mobileTypeAndDesc = oracleMobileRecords
				.get(imei.substring(0, 8));
		if (StringUtils.isNotBlank(mobileTypeAndDesc)) {
			String[] mobile = mobileTypeAndDesc.split(",", -1);
			if (mobileTypeAndDesc.length() < 2) {
				return;
			}
			record.setTerminal_model(mobile[1]);
			record.setTerminaltype(mobile[0]);
		}
	}

	private void setTime(Record record) {
		try {
			record.setPeriod_of_time(MRUtils.getPeriodOfTimeFormat(record
					.getStart_time()));
			record
					.setSum_date(MRUtils.getSumDateFormat(record
							.getStart_time()));
		} catch (Exception e) {
			e.printStackTrace();
			errorCounter.increment(1);
		}
	}
}
