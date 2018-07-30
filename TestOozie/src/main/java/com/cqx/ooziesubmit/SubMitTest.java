package com.cqx.ooziesubmit;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.ErrorCode;
import org.apache.oozie.service.AuthorizationException;
import org.apache.oozie.service.ConfigurationService;
import org.apache.oozie.service.HadoopAccessorService;
import org.apache.oozie.service.Services;

public class SubMitTest {
	public static void main(String[] args) {
		org.apache.oozie.service.AuthorizationService a;
		org.apache.oozie.servlet.BaseJobServlet b;
		/*
		oozie-site.xml
		oozie.service.HadoopAccessorService.hadoop.configurations
		*=hadoop-conf 
		loadHadoopConfigs
		ConfigurationService.getStrings(serviceConf, HADOOP_CONFS)
		public static final String HADOOP_CONFS = CONF_PREFIX + "hadoop.configurations";
		
		AuthorizationService.authorizeForApp
		Check if the user+group is authorized to use the specified application
		if (!fs.exists(path)) {
            incrCounter(INSTR_FAILED_AUTH_COUNTER, 1);
            throw new AuthorizationException(ErrorCode.E0504, appPath);
        }
		*/
		try {
			String appPath = "/user/edc_base/udap/xml/101169491131@2018010707000002";
			String user = "edc_base";
			HadoopAccessorService has = Services.get().get(HadoopAccessorService.class);
	        URI uri = new Path(appPath).toUri();
	        System.out.println("##uri.getAuthority##"+uri.getAuthority());
	        Configuration fsConf = has.createJobConf(uri.getAuthority());
	        System.out.println("##fsConf##"+fsConf);
	        FileSystem fs = has.createFileSystem(user, uri, fsConf);
	        System.out.println("##fs##"+fs);
	        System.out.println("##fs.defaultFS##"+fsConf.get("fs.defaultFS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
