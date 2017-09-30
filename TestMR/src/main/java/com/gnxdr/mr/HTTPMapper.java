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

public class HTTPMapper extends Mapper<LongWritable, Text, Text, CombineEntity>{
	
	private Configuration conf;//����
	private String[] columnNames = null;//�����ļ�������
	private String mapHttpField = "";//����ļ�������
	private Map<String, String> entities = null;//����������
	private CombineEntity combineEntity = new CombineEntity();//��ž���ֵ�Ķ���
	private Text flag = new Text();//map��ʶ
	private Text joinKey = new Text();//�����ֶε�ֵ
	private Text content = new Text();//�����ֵ
	private Counter httpMapperErrorCounter;//���������,���ر���
	
	@SuppressWarnings("rawtypes")
	private MultipleOutputs mos;

	/**
	 * Called once at the beginning of the task. 
	 * @param context ��ʾ�����Ļ����ȣ������²���
	 * <br>Configuration conf,
     * <br>TaskAttemptID taskid,
     * <br>RecordReader<KEYIN,VALUEIN> reader,
     * <br>RecordWriter<KEYOUT,VALUEOUT> writer,
     * <br>OutputCommitter committer,
     * <br>StatusReporter reporter,
     * <br>InputSplit split
	 * */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		
		conf = context.getConfiguration();//��������ļ�����
		//counterֻ��һ��������,context��ȫ�ֱ���(�̰߳�ȫ),��������Ҫö�ٴ��
		httpMapperErrorCounter = context.getCounter(COUNTER_ENUM.httpMapperErrorCounterEnum);
		//Creates and initializes multiple outputs support, it should be instantiated in the Mapper/Reducer setup method.
		//���ļ����
		mos = new MultipleOutputs(context);
		//��ȡgpfs_config.xml�����ļ��е�Constants.GN_XDR_HTTP_FIELD����,��Constants.COMMA_SEPARATOR���зָ�
		columnNames = conf.get(Constants.GN_XDR_HTTP_FIELD).split(Constants.COMMA_SEPARATOR, -1);
		//��ȡgpfs_config.xml�����ļ��е� http����ֶ�,ת�ɴ�д
		mapHttpField = conf.get(Constants.MAP_HTTP_TRANSPORT).toUpperCase();
		//�������id
		TaskID taskId = context.getTaskAttemptID().getTaskID();
		int partition = taskId.getId();
		//ɾ��ָ��·�����ļ� �����¼�ļ����·��+��������ʱ��+/+���õ�(JOB_DATAN)+err_http_+����id
		MRUtils.delPath(FileSystem.get(conf), conf.get(Constants.ERROR_OUTPUT_PATH) + conf.get(Constants.TASK_HOUR) + Constants.SEPARATOR + conf.get(Constants.JOB_DATAN) + Constants.ERR_HTTP_FILE_FREFIX + partition);
	}
	
	/**
	 * Called once for each key/value pair in the input split.
	 * <br>Most applications should override this, but the default is the identity function. 
	 * @param value ����ľ���ֵ
	 * */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//value ����
		//�����ŷָ������ַ�����ֽ�����map
		entities = MRUtils.transformLineToMap(columnNames, value.toString());
		//HTTP�ļ��ֶ�δƥ���ϵļ�¼
		if(entities == null||entities.size() == 0){
		//	mos.write(Constants.ERR_OUT_PIPE_NAME, NullWritable.get(), value, conf.get(Constants.FULL_ERROR_OUTPUT_PATH) + Constants.ERR_HTTP_FILE_FREFIX);
			httpMapperErrorCounter.increment(1);//���������+1
			return;
		}
		//map��ʶ
    	flag.set(Constants.HTTP_FILE_FLAG);
    	//ȡ�������ֶε�ֵ
    	joinKey.set(entities.get(Constants.JOIN_KEY));
    	//�����Ҫ���ֶε�ֵ���ð�Ƕ���ƴ����һ��
    	content.set(MRUtils.getSubmitField(entities,mapHttpField));
    	//��ž���ֵ�Ķ���
    	combineEntity.setFlag(flag);
    	combineEntity.setJoinKey(joinKey);
    	combineEntity.setContent(content);
    	//�������ip_id����
    	context.write(combineEntity.getJoinKey(), combineEntity);
    	//mos.write(Constants.HTTP_OUT_PIPE_NAME, joinKey, content, conf.get(Constants.FILE_OUTPUT_PATH) + conf.get(Constants.TASK_HOUR) + Constants.SEPARATOR + "HTTP");
	}

	/**
	 * Expert users can override this method for more complete control over the execution of the Mapper. 
	 * */
	@Override
	@SuppressWarnings("rawtypes")
	protected void cleanup(org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//�رն��ļ����
		mos.close();
	}
}
