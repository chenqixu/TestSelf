package com.bussiness.bi.bigdata.oozie;

//import java.io.IOException;
//import java.util.Iterator;

import org.apache.oozie.workflow.WorkflowApp;
import org.apache.oozie.workflow.WorkflowException;
//import org.apache.oozie.util.XConfiguration;
//import org.apache.oozie.util.XmlUtils;
//import org.apache.oozie.command.wf.ActionXCommand;
//import org.apache.oozie.command.wf.ActionXCommand.ActionExecutorContext;
import org.apache.oozie.service.Services;
import org.apache.oozie.service.WorkflowAppService;
import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.oozie.WorkflowJobBean;
//import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.jdom.filter.ElementFilter;

public class OozieTest {
	
	public void test1(){
//		org.apache.curator.ConnectionState a;
	}
	
	public static void main(String[] args) {
//		org.apache.oozie.action.oozie.SubWorkflowActionExecutor s;
		org.apache.oozie.servlet.V1JobsServlet v1;
		org.apache.oozie.servlet.BaseJobsServlet bjs;
		org.apache.oozie.command.wf.SubmitXCommand sxc;
		org.apache.oozie.command.XCommand xc;
		org.apache.oozie.DagEngine de;
		
		Configuration conf = null;
		Configuration defaultConf = null;
		WorkflowAppService wps = Services.get().get(WorkflowAppService.class);
		WorkflowApp app = null;
//		Element workflowXml = null;
//		WorkflowJobBean workflow = new WorkflowJobBean();
//		FileSystem fs = null;
		
		try {
			app = wps.parseDef(conf, defaultConf);
		} catch (WorkflowException e) {
			e.printStackTrace();
		}

//        // Checking variable substitution for dryrun
//        ActionExecutorContext context = new ActionXCommand.ActionExecutorContext(workflow, null, false, false);
//
//        try {
//			workflowXml = XmlUtils.parseXml(app.getDefinition());
//		} catch (JDOMException e) {
//			e.printStackTrace();
//		}
//		
//		Iterator<Element> it = workflowXml.getDescendants(new ElementFilter("job-xml"));
//		// Checking all variable substitutions in job-xml files
//		while (it.hasNext()) {
//		    Element e = it.next();
//		    String jobXml = e.getTextTrim();
//		    Path xmlPath = new Path(workflow.getAppPath(), jobXml);
//		    Configuration jobXmlConf = null;
//			try {
//				jobXmlConf = new XConfiguration(fs.open(xmlPath));
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//
//		    String jobXmlConfString = XmlUtils.prettyPrint(jobXmlConf).toString();
//		    try {
//				jobXmlConfString = XmlUtils.removeComments(jobXmlConfString);
//			} catch (JDOMException e1) {
//				e1.printStackTrace();
//			}
//		    try {
//				context.getELEvaluator().evaluate(jobXmlConfString, String.class);
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
	}
}
