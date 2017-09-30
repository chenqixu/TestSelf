package com.mr;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.mr.comm.GetMovementConstants;
import com.mr.comm.MoveRecord;
import com.mr.util.TempKey;

public class GetMovementTrackDataReducer extends
		Reducer<TempKey, Text, Text, Text> {

	// ����ʵ��
	private Configuration conf = null;
	// ���� MultipleOutputs �ı���
	private MultipleOutputs<Text, NullWritable> multipleOutputs;
	// ����ļ���ǰ׺
	private static String outputName = null;
	// ����ʱ��
	private static String sumDate = null;
	// �������value��list
	private List<String> inputValueList = new ArrayList<String>();
	// �������value��ת�����list
	private List<MoveRecord> recordList = new ArrayList<MoveRecord>();
	// ��ŵ���Դ���ݼ�¼������
	private String[] sourceDataValueArr = null;
	// ͳһʱ���ʽΪMC��ʱ��ĸ�ʽ
	private SimpleDateFormat sdf_MC;
	// ���ʱ��ĸ�ʽ
	private SimpleDateFormat sdf_output;

	@SuppressWarnings("unchecked")
	protected void setup(Context context) throws IOException,
			InterruptedException {
		multipleOutputs = new MultipleOutputs(context);
		super.setup(context);
		// ��ȡConfiguration����
		if (conf == null)
			conf = context.getConfiguration();
		// ����ļ���ǰ׺
		outputName = conf.get(GetMovementConstants.OUTPUT_NAME);
		// ����ʱ��
		sumDate = conf.get(GetMovementConstants.SOURCE_DATA_DATE);
		// MC��ʱ��ĸ�ʽ
		sdf_MC = new SimpleDateFormat(GetMovementConstants.MC_TIME_FORMAT);
		// ���ʱ��ĸ�ʽ
		sdf_output = new SimpleDateFormat(
				GetMovementConstants.OUTPUT_TIME_FORMAT);
	}

	protected void reduce(TempKey key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		// vlr_msisdn_number, 0
		// vlr_imei, 1
		// vlr_event_type, 2
		// vlr_lac, 3
		// vlr_cell, 4
		// vlr_report_time, 5
		// seu_type 6
		// dataType 7
		try {
			// ���ڴ��ǰһ����¼����Ϣ
			String old_lac = null;
			String old_cell = null;
			long old_time = 0;

			// ��ŵ�ǰ��¼����Ϣ
			String new_lac = null;
			String new_cell = null;
			long new_time = 0;
			// ʱ��ת��ʱ�õ�����ʱ����
			Date newTimeDate = new Date();
			Date oldTimeDate = new Date();

			// ��������list���
			recordList.clear();
			// �����ݷ���list��ȥ
			for (Text text : values) {
				inputValueList.add(text.toString());
			}

			for (int i = 0; i < inputValueList.size(); i++) {
				MoveRecord record = new MoveRecord();
				if (i == 0) {
					// �ָ��������ݵ�����
					sourceDataValueArr = inputValueList.get(i).split(
							GetMovementConstants.COMMA_SPLIT_STR, -1);
					// ����record
					record.setMsisdn(sourceDataValueArr[0]);
					record.setVlr_imei(sourceDataValueArr[1]);
					record.setVlr_event_type(sourceDataValueArr[2]);
					new_lac = sourceDataValueArr[3];
					record.setVlr_lac(new_lac);
					// ��һ����¼��lac��Ϊ��һ����¼old_lac�ĳ�ʼֵ
					old_lac = new_lac;
					new_cell = sourceDataValueArr[4];
					record.setVlr_cell(new_cell);
					// ��һ����¼��cell��Ϊ��һ����¼old_cell�ĳ�ʼֵ
					old_cell = new_cell;
					// ʱ���ʽת��
					newTimeDate = sdf_MC.parse(sourceDataValueArr[5]);
					record.setVlr_report_time(sdf_output.format(newTimeDate));
					// ��һ����¼��time��Ϊold_time�ĳ�ʼֵ
					new_time = newTimeDate.getTime();
					old_time = new_time;
					record.setSeu_type(sourceDataValueArr[6]);
					record.setDataType(sourceDataValueArr[7]);
					// ȡһ�������ʱ������һ����¼��old_time
					Date firstTimeDate = sdf_MC.parse(sumDate + "000000");
					record.setOld_time(sdf_output.format(firstTimeDate));
					record.setOld_lac(old_lac);
					record.setOld_cell(old_cell);
					// ʱ��������3Сʱ��־λ1������0
					long firstTime = sdf_MC.parse(sumDate + "000000").getTime();
					if (new_time - firstTime <= 10800000) {
						record.setIs_exception("0");
					} else {
						record.setIs_exception("1");
					}
					// ����list
					recordList.add(record);
				} else {
					// �ָ��������ݵ�����
					sourceDataValueArr = inputValueList.get(i).split(
							GetMovementConstants.COMMA_SPLIT_STR, -1);

					new_lac = sourceDataValueArr[3];
					new_cell = sourceDataValueArr[4];
					new_time = sdf_MC.parse(sourceDataValueArr[5]).getTime();

					long timeDifference = new_time - old_time;
					// ʱ��������3Сʱ��־λ1������0
					if (new_cell.equals(old_cell) && new_lac.equals(old_lac)
							&& timeDifference <= 10800000) {
						old_time = new_time;
						continue;
					}

					// ����record
					record.setMsisdn(sourceDataValueArr[0]);
					record.setVlr_imei(sourceDataValueArr[1]);
					record.setVlr_event_type(sourceDataValueArr[2]);
					record.setVlr_lac(new_lac);
					record.setVlr_cell(new_cell);
					// ʱ���ʽת��
					newTimeDate = sdf_MC.parse(sourceDataValueArr[5]);
					record.setVlr_report_time(sdf_output.format(newTimeDate));
					record.setSeu_type(sourceDataValueArr[6]);
					record.setDataType(sourceDataValueArr[7]);
					oldTimeDate.setTime(old_time);
					record.setOld_time(sdf_output.format(oldTimeDate));
					record.setOld_lac(old_lac);
					record.setOld_cell(old_cell);
					// ����ʱ����ж��쳣��־λȡֵ
					if (i < inputValueList.size() - 1) {
						if (timeDifference <= 10800000) {
							record.setIs_exception("0");
						} else {
							record.setIs_exception("1");
						}
					} else {
						// ���һ����¼�뵱��235959�Ƚ�
						long lasttTime = sdf_MC.parse(sumDate + "235959")
								.getTime();
						if (lasttTime - new_time <= 10800000) {
							record.setIs_exception("0");
						} else {
							record.setIs_exception("1");
						}
					}
					// ����list
					recordList.add(record);
					// ���±���
					old_time = new_time;
					old_lac = new_lac;
					old_cell = new_cell;
				}
			}
			//����������ݵ�list
			inputValueList.clear();
			//����MoveRecord����
			MoveRecord record = new MoveRecord();
			
			for (int j = 0; j < recordList.size(); j++) {
				//�������������StringBuffer
				StringBuffer outputSB = new StringBuffer();
				//ȡ����¼
				record = recordList.get(j);
//				msisdn,
				outputSB.append(record.getMsisdn()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_imei,
				outputSB.append(record.getVlr_imei()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_event_type,
				outputSB.append(record.getVlr_event_type()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_lac,
				outputSB.append(record.getVlr_lac()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				vlr_cell,
				outputSB.append(record.getVlr_cell()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				old_lac,
				outputSB.append(record.getOld_lac()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				old_cell,
				outputSB.append(record.getOld_cell()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				in_time,
				if(j==0 || record.getIs_exception().equals("1")){
					outputSB.append(record.getOld_time()).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					outputSB.append(record.getVlr_report_time()).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}
//				out_time,
				if(j < recordList.size()-1){
					MoveRecord nextRecord = recordList.get(j+1);
					if(nextRecord.getIs_exception().equals("1")){
						outputSB.append(nextRecord.getOld_time()).append(
								GetMovementConstants.COMMA_SPLIT_STR);
					}else{
						outputSB.append(nextRecord.getVlr_report_time()).append(
								GetMovementConstants.COMMA_SPLIT_STR);
					}
				}else{
					Date lastTimeDate = sdf_MC.parse(sumDate + "235959");
					outputSB.append(sdf_output.format(lastTimeDate)).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}
//				seu_type,
				outputSB.append(record.getSeu_type()).append(
						GetMovementConstants.COMMA_SPLIT_STR);
//				is_exception
				if(j < recordList.size()-1){
					outputSB.append(record.getIs_exception()).append(
							GetMovementConstants.COMMA_SPLIT_STR);
				}else{
					long lasttTime = sdf_MC.parse(sumDate + "235959")
					.getTime();
					long lastReportTime = sdf_MC.parse(record.getVlr_report_time())
					.getTime();
					if (lasttTime -  lastReportTime<= 10800000) {
						outputSB.append("0").append(
								GetMovementConstants.COMMA_SPLIT_STR);
					} else {
						outputSB.append("1").append(
								GetMovementConstants.COMMA_SPLIT_STR);
					}
				}
//				dataType
				outputSB.append(record.getDataType());
				
				//�������
				Text outputText = new Text();
				outputText.set(outputSB.toString());
				multipleOutputs.write(outputName, outputText, NullWritable.get());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		multipleOutputs.close();
		super.cleanup(context);
	}

}
