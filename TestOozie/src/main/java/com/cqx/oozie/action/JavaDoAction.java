package com.cqx.oozie.action;

import org.apache.oozie.action.ActionExecutor;
import org.apache.oozie.action.ActionExecutorException;
import org.apache.oozie.client.WorkflowAction;
//import org.apache.oozie.util.XmlUtils;
//import org.jdom.Element;
//import org.jdom.Namespace; 

/**
 * 打印环境变量
 */
public class JavaDoAction extends ActionExecutor {
    private static final String NODENAME = "javado";

    private static final String SUCCEEDED = "OK";
    private static final String FAILED = "FAIL";
    private static final String KILLED = "KILLED";

    public JavaDoAction() {
        super(NODENAME);
    }

    @Override
    public void start(Context context, WorkflowAction workflowAction)
            throws ActionExecutorException {
        try {
//            Element actionXml = XmlUtils.parseXml(workflowAction.getConf());
//            Namespace ns = Namespace.getNamespace("uri:custom:javado-action:0.1"); 
            // 打印环境变量
            System.out.println("##env.LD_LIBRARY_PATH##" + System.getenv().get("LD_LIBRARY_PATH"));
//            System.out.println("##all.env.start##");
//            for(java.util.Map.Entry<String, String> env : System.getenv().entrySet()){
//            	System.out.println("##"+env.getKey()+"##"+env.getValue());
//            }
//            System.out.println("##all.env.end##");
//            System.out.println("##env##"+System.getenv().toString());
            // 休眠10秒
            System.out.println("##sleep.start##");
            Thread.sleep(10000);
            System.out.println("##sleep.end##");
            // 成功
            context.setExecutionData(SUCCEEDED, null);
        } catch (Exception e) {
            // 失败
            context.setExecutionData(FAILED, null);
            throw new ActionExecutorException(ActionExecutorException.ErrorType.FAILED,
                    NODENAME + "失败", e.getMessage());
        }
    }

    @Override
    public void end(Context context, WorkflowAction workflowAction)
            throws ActionExecutorException {
        String externalStatus = workflowAction.getExternalStatus();
        WorkflowAction.Status status = externalStatus.equals(SUCCEEDED) ?
                WorkflowAction.Status.OK : WorkflowAction.Status.ERROR;
        context.setEndData(status, getActionSignal(status));
    }

    @Override
    public void check(Context context, WorkflowAction workflowAction)
            throws ActionExecutorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCompleted(String externalStatus) {
//		return false;
        System.out.println("##externalStatus##" + externalStatus);
        return true;
    }

    @Override
    public void kill(Context context, WorkflowAction workflowAction)
            throws ActionExecutorException {
        context.setExternalStatus(KILLED);
        context.setExecutionData(KILLED, null);
    }
}
