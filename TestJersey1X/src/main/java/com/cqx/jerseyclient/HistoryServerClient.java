package com.cqx.jerseyclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cqx.bean.JobAttempts;
import com.cqx.bean.Job;
import com.cqx.bean.Jobs;
import com.cqx.bean.ResultBean;

public class HistoryServerClient {
	
	public static final String JOB = "job";
	public static final String logKeyWord = "syslog";
	public static final String errKeyWord = "JobHistoryEventHandler";
	@SuppressWarnings("restriction")
	public static final String lineSeparator = java.security.AccessController.doPrivileged(
			new sun.security.action.GetPropertyAction("line.separator"));
	public static final int TIMEOUTMILLIS = 5000;
	
	protected static final String BASE_URL = "http://10.1.8.75:19888";
	/**
	 * <pre>
	 * <b>URI</b>
	 * http://<history server http address:port>/ws/v1/history/mapreduce/jobs
	 * 
	 * The jobs resource provides a list of the MapReduce jobs that have finished.
	 * It does not currently return a full list of parameters
	 * 
	 * <b>HTTP Operations Supported</b>
	 * GET
	 * 
	 * <b>Query Parameters Supported</b>
	 * user - user name
	 * state - the job state
	 * queue - queue name
	 * limit - total number of app objects to be returned
	 * startedTimeBegin - jobs with start time beginning with this time, specified in ms since epoch
	 * startedTimeEnd - jobs with start time ending with this time, specified in ms since epoch
	 * finishedTimeBegin - jobs with finish time beginning with this time, specified in ms since epoch
	 * finishedTimeEnd - jobs with finish time ending with this time, specified in ms since epoch
	 * </pre>
	 * */
	protected static final String JOBS = BASE_URL+"/ws/v1/history/mapreduce/jobs";
	
	/**
	 * <pre>
	 * <b>URI</b>
	 * http://<history server http address:port>/ws/v1/history/mapreduce/jobs/{jobid}
	 * 
	 * A Job resource contains information about a particular job identified by jobid.
	 * 
	 * <b>HTTP Operations Supported</b>
	 * GET
	 * 
	 * <b>Query Parameters Supported</b>
	 * None
	 * </pre>
	 * */
	protected static final String JOBINFO = BASE_URL+"/ws/v1/history/mapreduce/jobs/{jobid}";
	
	/**
	 * <pre>
	 * <b>URI</b>
	 * http://<history server http address:port>/ws/v1/history/mapreduce/jobs/{jobid}/jobattempts
	 * 
	 * With the job attempts API, you can obtain a collection of resources that represent a job attempt. When you run a GET operation on this resource, you obtain a collection of Job Attempt Objects.
	 * 
	 * <b>HTTP Operations Supported</b>
	 * GET
	 * 
	 * <b>Query Parameters Supported</b>
	 * None
	 * </pre>
	 * */
	protected static final String JOBATTEMPTS = BASE_URL+"/ws/v1/history/mapreduce/jobs/{jobid}/jobattempts";
	
	public HistoryServerClient() {
	}
	
	public void jobs() {
		Jobs textEntity = ClientFactory.getInstance().call(JOBS,
				MediaType.APPLICATION_XML, Jobs.class);
		if(textEntity != null && textEntity.getJob() != null )
			for(Jobs.Job jb : textEntity.getJob()) {
				System.out.println(jb.getName().replaceAll("\r|\n", ""));
				System.out.println(jb.getId());
			}
	}
	
	public void jobinfo(String jobid) {
		if(jobIdCheck(jobid)) {
			String url = JOBINFO.replace("{jobid}", jobid);
			Job textEntity = ClientFactory.getInstance().call(url,
    				MediaType.APPLICATION_XML, Job.class);
			System.out.println("[state]"+textEntity.getState());
			System.out.println("[diagnostics]"+textEntity.getDiagnostics());
		}
	}
	
	public String jobattempts(String jobid) {
		String result = null;
		if(jobIdCheck(jobid)) {
			String url = JOBATTEMPTS.replace("{jobid}", jobid);
    		JobAttempts textEntity = ClientFactory.getInstance().call(url,
    				MediaType.APPLICATION_XML, JobAttempts.class);
    		for(JobAttempts.JobAttempt ja : textEntity.getJobAttempt()) {
    			result = ja.getLogsLink();
    		}
		}
		return result;
	}
	
	public void jobhistorylogs(String url) {
		if(StringUtils.isNoneBlank(url)){
			ResultBean<String> textEntity = ClientFactory.getInstance().callGetBean(url);
			System.out.println("[status]"+textEntity.getStatus());
			System.out.println("[textEntity]"+textEntity.getT());
		}
	}
	
	public String parserHtmlGetSyslogLink(String url) {
		String result = null;
		if(StringUtils.isNoneBlank(url)){
//			Document doc = null;
//			try {
//				doc = Jsoup.parse(new URL(url), TIMEOUTMILLIS);
//			} catch (MalformedURLException e1) {
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
			Document doc = Jsoup.parse(ClientFactory.getInstance().call(url), BASE_URL);
			Elements es = doc.select("a");
			for(Element e : es) {
				String e_url = e.attr("abs:href");
				if(e_url.contains(logKeyWord)) {
					result = e_url;
					break;
				}
			}
		}
		return result;
	}
	
	public String parserHtmlGetErrLog(String url) {
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNoneBlank(url)){
			Document doc = Jsoup.parse(ClientFactory.getInstance().call(url));
			Elements es = doc.select("pre");
			for(Element e : es) {
				sb.append(e.text());
			}
		}
		return sb.toString();
	}
	
	protected String getErrStringLog(String content) {
		String line;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		try {
			reader = new BufferedReader(new StringReader(content));
			while((line = reader.readLine()) != null){
				if(line.contains(errKeyWord)){
					sb.append(line)
						.append(lineSeparator);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return sb.toString();
	}
	
	protected boolean jobIdCheck(String jobid) {
		if(jobid == null)
			return false;
	    try {
	    	String[] parts = jobid.split("_");
	    	if(parts.length == 3) {
	    		if(parts[0].equals(JOB)) {
	    			return true;
	    		}
	    	}
	    } catch (Exception ex) {//fall below
	    }
	    return false;
	}
	
	public static void setHttpProxy(String host, String port) {
		System.setProperty("proxySet", "true");
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", port);
	}
	
	public static void main(String[] args) {
		setHttpProxy("10.1.4.185", "11111");
		HistoryServerClient hsc = new HistoryServerClient();
		
//		hsc.jobs();
//		String url = "http://10.1.8.75:19888/jobhistory/logs/node81:8041/container_1533174517432_0066_01_000001/job_1533174517432_0066/hive";
		String url = "http://10.47.248.20:8020/services/env/conn/detail/name?connName=bch_hive_nl_load";
		hsc.jobhistorylogs(url);
//		hsc.jobinfo("job_1533174517432_0066");
		
//		String LogsLink = hsc.jobattempts("job_1533174517432_0066");
//		String syslogLink = hsc.parserHtmlGetSyslogLink(LogsLink);
//		System.out.println("[syslogLink]"+syslogLink);
//		String errlog = hsc.parserHtmlGetErrLog(syslogLink);
//		System.out.println("[errlog]"+hsc.getErrStringLog(errlog));
	}
}
