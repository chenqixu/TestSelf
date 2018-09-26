package com.mr.util;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.hs.JobHistory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * ���ڻ�ȡyarn�ϵ���־
 * */
public class LogUtil {
	protected org.apache.hadoop.yarn.client.cli.LogsCLI a;// pass
	protected org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer jhs;// pass
	protected String mapred_site_xmlpath = LogUtil.class.getClassLoader().getResource("conf/mapred-site.xml").getPath();
	protected Configuration conf = new Configuration();
	
	/**
	 * Constructor
	 * */
	public LogUtil() {
		conf.addResource(new Path(mapred_site_xmlpath));
		System.out.println(conf);
		System.out.println(conf.get("mapreduce.jobhistory.webapp.address"));
	}
	
	/**
	 * <pre>
	 * History Server REST API
	 * </pre>
	 * */
	public void RESTAPI() {
		Client client = Client.create();
		WebResource webResource = client.resource("http://example.com/base");
	}
	
	/**
	 * <pre>
	 * JobHistory
	 * 
	 * ��JobHistory��ȡ��job��������ɵ�job��������ʷjob
	 * JobHistory��ͨ��load hdfs�ϵ�history file��ȡjob��Ϣ
	 * PartialJob���޷���ȡcounters��configuration��task����ϸ��Ϣ
	 * </pre>
	 * */
	public void JobHistory() {
		JobHistory his = new JobHistory();
		his.init(conf);
		
		Map<JobId, Job> jobs = his.getAllJobs();//��ȡ��job��Ϣ�ǲ������ģ�PartialJob��
		System.out.println(jobs);
		
		JobID oldJobId = JobID.forName("job_1533174517432_0066");
		System.out.println(oldJobId.getId());
		JobId jobId = TypeConverter.toYarn(oldJobId);	
		Job fullJob = his.getJob(jobId);//��ȡ��job��Ϣ�������ģ�CompletedJob��
		System.out.println(fullJob);
	}
	
	/**
	 * <pre>
	 * JobClient
	 * 
	 * ��JobClient�л�ȡjob��Ҫ����resourcemanager�����historyserver����
	 * ��JobClient��ȡ��job����������applicationmaster��������resourcemanager�Ļ����е�
	 * ��JobClient��ȡ��job��������״̬��job
	 * </pre>
	 * */
	public void JobClient() {
		try {
			JobClient jobClient = new JobClient(new JobConf(conf));
			JobStatus[] jobs = jobClient.getAllJobs();//��ȡ�����е�job��job��Ϣ��������
			System.out.println(jobs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new LogUtil().JobHistory();
	}
}
