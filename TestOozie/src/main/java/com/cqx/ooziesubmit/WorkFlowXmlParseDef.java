package com.cqx.ooziesubmit;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.ErrorCode;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.service.HadoopAccessorException;
import org.apache.oozie.service.HadoopAccessorService;
import org.apache.oozie.service.Service;
import org.apache.oozie.service.Services;
import org.apache.oozie.service.WorkflowAppService;
import org.apache.oozie.util.IOUtils;
import org.apache.oozie.workflow.WorkflowException;

/**
 * 用于验证workflow.xml文件的定义是否正确
 * */
public class WorkFlowXmlParseDef {
	
	public static void main(String[] args) {
		String appPath = "/home/edc_base/edc-app/xml/102607117304@2018080101000007/workflow.xml";
		String user = "edc_base";
		HadoopAccessorService has = Services.get().get(HadoopAccessorService.class);
        URI uri = new Path(appPath).toUri();
        System.out.println("##uri.getAuthority##"+uri.getAuthority());
        Configuration jobConf = has.createJobConf(uri.getAuthority());
//        Configuration configDefault = null;
        jobConf.set(OozieClient.APP_PATH, "/home/edc_base/edc-app/xml");
        jobConf.set(OozieClient.USER_NAME, user);
        System.out.println("##fsConf##"+jobConf);
        FileSystem fs = null;
		try {
			fs = has.createFileSystem(user, uri, jobConf);
		} catch (HadoopAccessorException e) {
			e.printStackTrace();
		}
        System.out.println("##fs##"+fs);
        System.out.println("##fs.defaultFS##"+jobConf.get("fs.defaultFS"));
        WorkflowAppService wps = Services.get().get(WorkflowAppService.class);
//        WorkflowApp app = null;
        try {
//			app = wps.parseDef(jobConf, configDefault);
//        	String workflowXml = wfxpd.readDefinition(appPath, user, jobConf);
        	String workflowXml = "";
        	try {
	        	String CONF_PREFIX = Service.CONF_PREFIX + "WorkflowAppService.";
	        	String CONFG_MAX_WF_LENGTH = CONF_PREFIX + "WorkflowDefinitionMaxLength";
	        	long maxWFLength = jobConf.getInt(CONFG_MAX_WF_LENGTH, 100000);
	        	// app path could be a directory
	            Path path = new Path(uri.getPath());
	            System.out.println("##path##"+path.toString());
	            FileStatus fsStatus = fs.getFileStatus(path);
	            if (fsStatus.getLen() > maxWFLength) {
	                throw new WorkflowException(ErrorCode.E0736, fsStatus.getLen(), maxWFLength);
	            }
	            Reader reader = new InputStreamReader(fs.open(path));
	            StringWriter writer = new StringWriter();
	            IOUtils.copyCharStream(reader, writer);
	            workflowXml = writer.toString();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}        	
        	System.out.println("##workflowXml##"+workflowXml);
        	wps.parseDef(workflowXml, jobConf);
		} catch (WorkflowException e) {
			e.printStackTrace();
		}
	}
}
