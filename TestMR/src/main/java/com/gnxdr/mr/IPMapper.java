package com.gnxdr.mr;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.gnxdr.bean.CombineEntity;
import com.gnxdr.constant.Constants;
import com.gnxdr.constant.Constants.COUNTER_ENUM;
import com.gnxdr.utils.MRUtils;

public class IPMapper extends Mapper<LongWritable, Text, Text, CombineEntity>{
	
	private Configuration conf;//����
	private String[] columnNames = null;//�����ļ�������
	private String mapIpField = "";//����ļ�������
	private Map<String, String> entities = null;//����������
	private CombineEntity combineEntity = new CombineEntity();//��ž���ֵ�Ķ���
	private Text flag = new Text();//map��ʶ
	private Text joinKey = new Text();//�����ֶε�ֵ
	private Text content = new Text();//�����ֵ
	private Counter ipMapperErrorCounter;//���������,���ر���
	
	@SuppressWarnings("rawtypes")
	private MultipleOutputs mos;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		
		conf = context.getConfiguration();//��������ļ�����
		//counterֻ��һ��������,context��ȫ�ֱ���(�̰߳�ȫ),��������Ҫö�ٴ��
		ipMapperErrorCounter = context.getCounter(COUNTER_ENUM.ipMapperErrorCounterEnum);
		//Creates and initializes multiple outputs support, it should be instantiated in the Mapper/Reducer setup method.
		//���ļ����
		mos = new MultipleOutputs(context);
		//��ȡgpfs_config.xml�����ļ��е�Constants.GN_XDR_IP_FIELD����,��Constants.COMMA_SEPARATOR���зָ�
		columnNames = conf.get(Constants.GN_XDR_IP_FIELD).split(Constants.COMMA_SEPARATOR, -1);
		//��ȡgpfs_config.xml�����ļ��е� ip����ֶ�,ת�ɴ�д
		mapIpField = conf.get(Constants.MAP_IP_TRANSPORT).toUpperCase();
		//�������id
		TaskID taskId = context.getTaskAttemptID().getTaskID();
		int partition = taskId.getId();
		//ɾ��ָ��·�����ļ� �����¼�ļ����·��+��������ʱ��+/+���õ�(JOB_DATAN)+err_ip_+����id
		MRUtils.delPath(FileSystem.get(conf), conf.get(Constants.ERROR_OUTPUT_PATH) + conf.get(Constants.TASK_HOUR) + Constants.SEPARATOR + conf.get(Constants.JOB_DATAN) + Constants.ERR_IP_FILE_FREFIX + partition);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//value ����
		//�����ŷָ������ַ�����ֽ�����map
		entities = MRUtils.transformLineToMap(columnNames, value.toString());
		//IP�ļ��ֶ�δƥ���ϵļ�¼
		if(entities == null||entities.size() == 0){
		//	mos.write(Constants.ERR_OUT_PIPE_NAME, NullWritable.get(), value, conf.get(Constants.FULL_ERROR_OUTPUT_PATH) + Constants.ERR_IP_FILE_FREFIX);
			ipMapperErrorCounter.increment(1);//���������+1
			return;
		}
		
		String telnum = entities.get("msisdn".toUpperCase());
    	//�쳣�������
    	if(telnum.length() > 14){
    		//mos.write(Constants.ERR_OUT_PIPE_NAME, NullWritable.get(), value,conf.get(Constants.FULL_ERROR_OUTPUT_PATH) + Constants.ERR_IP_FILE_FREFIX);
    		return;
    	}
    	
    	if(telnum.length() == 13){
    		entities.put("msisdn".toUpperCase(),telnum.substring(2));
    	}
    	if(telnum.length() == 14){
    		entities.put("msisdn".toUpperCase(),telnum.substring(3));
    	}
		
		//map��ʶ
    	flag.set(Constants.IP_FILE_FLAG);
    	//ȡ�������ֶε�ֵ
    	joinKey.set(entities.get(Constants.JOIN_KEY));
    	//�����Ҫ���ֶε�ֵ���ð�Ƕ���ƴ����һ��
    	content.set(MRUtils.getSubmitField(entities,mapIpField));
    	//��ž���ֵ�Ķ���
    	combineEntity.setFlag(flag);
    	combineEntity.setJoinKey(joinKey);
    	combineEntity.setContent(content);
    	//�������ip_id����
    	context.write(combineEntity.getJoinKey(), combineEntity);
    	//mos.write(Constants.HTTP_OUT_PIPE_NAME, joinKey, content, conf.get(Constants.FILE_OUTPUT_PATH) + conf.get(Constants.TASK_HOUR) + Constants.SEPARATOR + "IP");
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected void cleanup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//�رն��ļ����
		mos.close();
	}
}
