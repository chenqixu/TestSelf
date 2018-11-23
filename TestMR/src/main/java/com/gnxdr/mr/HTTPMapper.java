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
	
	private Configuration conf;//配置
	private String[] columnNames = null;//输入文件的列名
	private String mapHttpField = "";//输出文件的列名
	private Map<String, String> entities = null;//列名，内容
	private CombineEntity combineEntity = new CombineEntity();//存放具体值的对象
	private Text flag = new Text();//map标识
	private Text joinKey = new Text();//关联字段的值
	private Text content = new Text();//输出的值
	private Counter httpMapperErrorCounter;//错误计数器,本地变量
	
	@SuppressWarnings("rawtypes")
	private MultipleOutputs mos;

	/**
	 * Called once at the beginning of the task. 
	 * @param context 表示上下文环境等，有以下参数
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
		
		conf = context.getConfiguration();//获得配置文件内容
		//counter只是一个计数器,context是全局变量(线程安全),计数器需要枚举存放
		httpMapperErrorCounter = context.getCounter(COUNTER_ENUM.httpMapperErrorCounterEnum);
		//Creates and initializes multiple outputs support, it should be instantiated in the Mapper/Reducer setup method.
		//多文件输出
		mos = new MultipleOutputs(context);
		//读取gpfs_config.xml配置文件中的Constants.GN_XDR_HTTP_FIELD内容,用Constants.COMMA_SEPARATOR进行分割
		columnNames = conf.get(Constants.GN_XDR_HTTP_FIELD).split(Constants.COMMA_SEPARATOR, -1);
		//读取gpfs_config.xml配置文件中的 http输出字段,转成大写
		mapHttpField = conf.get(Constants.MAP_HTTP_TRANSPORT).toUpperCase();
		//获得任务id
		TaskID taskId = context.getTaskAttemptID().getTaskID();
		int partition = taskId.getId();
		//删除指定路径的文件 错误记录文件输出路径+任务运行时间+/+配置的(JOB_DATAN)+err_http_+任务id
		MRUtils.delPath(FileSystem.get(conf), conf.get(Constants.ERROR_OUTPUT_PATH) + conf.get(Constants.TASK_HOUR) + Constants.SEPARATOR + conf.get(Constants.JOB_DATAN) + Constants.ERR_HTTP_FILE_FREFIX + partition);
	}
	
	/**
	 * Called once for each key/value pair in the input split.
	 * <br>Most applications should override this, but the default is the identity function. 
	 * @param value 传入的具体值
	 * */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		//value 输入
		//将逗号分隔符的字符串拆分解析成map
		entities = MRUtils.transformLineToMap(columnNames, value.toString());
		//HTTP文件字段未匹配上的记录
		if(entities == null||entities.size() == 0){
		//	mos.write(Constants.ERR_OUT_PIPE_NAME, NullWritable.get(), value, conf.get(Constants.FULL_ERROR_OUTPUT_PATH) + Constants.ERR_HTTP_FILE_FREFIX);
			httpMapperErrorCounter.increment(1);//错误计数器+1
			return;
		}
		//map标识
    	flag.set(Constants.HTTP_FILE_FLAG);
    	//取出关联字段的值
    	joinKey.set(entities.get(Constants.JOIN_KEY));
    	//获得需要的字段的值，用半角逗号拼接在一起
    	content.set(MRUtils.getSubmitField(entities,mapHttpField));
    	//存放具体值的对象
    	combineEntity.setFlag(flag);
    	combineEntity.setJoinKey(joinKey);
    	combineEntity.setContent(content);
    	//输出，按ip_id分组
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
		//关闭多文件输出
		mos.close();
	}
}
