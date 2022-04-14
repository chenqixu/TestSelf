package com.mr.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
//import org.apache.hadoop.mapreduce.v2.hs.JobHistory;

import java.io.IOException;
import java.util.Map;

/**
 * 用于获取yarn上的日志
 */
public class LogUtil {
    //	protected org.apache.hadoop.yarn.client.cli.LogsCLI a;// pass
//    protected org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer jhs;// pass
    protected String mapred_site_xmlpath = LogUtil.class.getClassLoader().getResource("confhw/mapred-site.xml").getPath();
    protected Configuration conf = new Configuration();

    /**
     * Constructor
     */
    public LogUtil() {
        conf.addResource(new Path(mapred_site_xmlpath));
        System.out.println(conf);
        System.out.println("======" + conf.get("mapreduce.jobhistory.webapp.address"));
    }

    public static void main(String[] args) {
//        new LogUtil().JobHistory();
    }

    /**
     * <pre>
     * History Server REST API
     * </pre>
     */
    public void RESTAPI() {
        Client client = Client.create();
        WebResource webResource = client.resource("http://example.com/base");
    }

    /**
     * <pre>
     * JobHistory
     *
     * 从JobHistory获取的job是所有完成的job，包括历史job
     * JobHistory是通过load hdfs上的history file获取job信息
     * PartialJob中无法获取counters、configuration、task等详细信息
     * </pre>
     */
//    public void JobHistory() {
//        JobHistory his = new JobHistory();
//        his.init(conf);
//        System.out.println("getHistoryUrl=" + his.getHistoryUrl());
//        Map<JobId, Job> jobs = his.getAllJobs();//获取的job信息是不完整的（PartialJob）
//        System.out.println(jobs);
//
//        JobID oldJobId = JobID.forName("job_1633092039821_2421");
//        System.out.println(oldJobId.getId());
//        JobId jobId = TypeConverter.toYarn(oldJobId);
//        Job fullJob = his.getJob(jobId);//获取的job信息是完整的（CompletedJob）
//        System.out.println(fullJob);
//    }

    /**
     * <pre>
     * JobClient
     *
     * 从JobClient中获取job需要连接resourcemanager服务和historyserver服务
     * 从JobClient获取的job是其所属的applicationmaster还存在于resourcemanager的缓存中的
     * 从JobClient获取的job包含所有状态的job
     * </pre>
     */
    public void JobClient() {
        try {
            JobClient jobClient = new JobClient(new JobConf(conf));
            JobStatus[] jobs = jobClient.getAllJobs();//获取缓存中的job，job信息是完整的
            System.out.println(jobs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
