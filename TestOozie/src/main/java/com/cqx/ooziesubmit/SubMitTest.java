package com.cqx.ooziesubmit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.service.HadoopAccessorService;
import org.apache.oozie.service.Services;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

public class SubMitTest {

    public static void main(String[] args) {
//		org.apache.oozie.service.AuthorizationService a;
//		org.apache.oozie.servlet.BaseJobServlet b;
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
            Services services = new Services();
            services.init();
            HadoopAccessorService has = Services.get().get(HadoopAccessorService.class);
            URI uri = new Path(appPath).toUri();
            System.out.println("##uri.getAuthority##" + uri.getAuthority());
            Configuration fsConf = has.createJobConf(uri.getAuthority());
            System.out.println("##fsConf##" + fsConf);
            FileSystem fs = has.createFileSystem(user, uri, fsConf);
            System.out.println("##fs##" + fs);
            System.out.println("##fs.defaultFS##" + fsConf.get("fs.defaultFS"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submit(String oozie_url, Map<String, String> propertyMap) {
        OozieClient oozieClient = new OozieClient(oozie_url);
        Properties conf = oozieClient.createConfiguration();
//        conf.setProperty("nameNode", "hdfs://192.168.1.133:9000");
//        conf.setProperty("queueName", "default");
//        conf.setProperty("examplesRoot", "examples");
        conf.setProperty("oozie.wf.application.path", "${nameNode}/user/cenyuhai/${examplesRoot}/apps/map-reduce");
//        conf.setProperty("outputDir", "map-reduce");
//        conf.setProperty("jobTracker", "http://192.168.1.133:9001");
//        conf.setProperty("inputDir", input);
//        conf.setProperty("outputDir", output);
        try {
            String jobId = oozieClient.run(conf);
        } catch (OozieClientException e) {
            e.printStackTrace();
        }
    }
}
