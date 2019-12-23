package com.cqx.oozie.action;

import com.cqx.oozie.common.FileUtil;
import org.apache.oozie.ErrorCode;
import org.apache.oozie.action.ActionExecutor;
import org.apache.oozie.action.ActionExecutorException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.util.XmlUtils;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 创建随机内容的文件
 *
 * @author chenqixu
 */
public class CreateRandomFileAction extends ActionExecutor {

    private static final String NODENAME = "createrandomfile";
    private static final String SUCCEEDED = "OK";
    private static final String FAILED = "FAIL";
    private static final String KILLED = "KILLED";

    //1- Constructor，一定要有一个没有参数的构造方法，内部调用super(ACTION_TYPE)，这样oozie才会初始化这个aciton的类型
    protected CreateRandomFileAction(String type) {
        super(NODENAME);
    }

    protected CreateRandomFileAction(String type, long defaultRetryInterval) {
        super(NODENAME, defaultRetryInterval);
    }

    //2- initActionType()
    @Override
    public void initActionType() {
        super.initActionType();
    }

    //3- start(Context context, WorkflowAction workflowAction) ，这里基本写的就是这个action的逻辑代码
    @Override
    public void start(Context context, WorkflowAction workflowAction) throws ActionExecutorException {
        Element actionXml;
        try {
            actionXml = XmlUtils.parseXml(workflowAction.getConf());
            Namespace ns = Namespace.getNamespace("uri:custom:" + NODENAME + "-action:0.1");
            // 此处为该action想要在代码里做的事情
            // 功能：创建随机内容的文件
            // 1.获取参数：文件路径、文件名长度、文件名后缀、文件行数、文件内容长度
            // 获得名叫argname的参数，在后面的xsd中有体现
            String file_path = actionXml.getChildTextTrim("file_path", ns);
            String file_name_length = actionXml.getChildTextTrim("file_name_length", ns);
            String file_name_suffix = actionXml.getChildTextTrim("file_name_suffix", ns);
            String file_content_size = actionXml.getChildTextTrim("file_content_size", ns);
            String file_content_length = actionXml.getChildTextTrim("file_content_length", ns);
            // 生成文件名
            FileUtil fileUtil = new FileUtil();
            int _file_name_length = file_name_length != null && file_name_length.trim().length() > 0
                    ? Integer.valueOf(file_name_length) : 5;// 默认5
            String file_name = FileUtil.endWith(file_path) + fileUtil.getRandomStr(_file_name_length) + file_name_suffix;
            // 生成随机内容的文件
            int _file_content_size = file_content_size != null && file_content_size.trim().length() > 0
                    ? Integer.valueOf(file_content_size) : 5;// 默认5
            int _file_content_length = file_content_length != null && file_content_length.trim().length() > 0
                    ? Integer.valueOf(file_content_length) : 5;// 默认5
            fileUtil.createRandomFile(file_name, _file_content_size, _file_content_length);
            //=====================
            context.setExecutionData(SUCCEEDED, null);
        } catch (Exception e) {
            context.setExecutionData(FAILED, null);
            throw new ActionExecutorException(ActionExecutorException.ErrorType.FAILED,
                    ErrorCode.E0000.toString(), e.getMessage());
        }
    }

    //4- end(Context context, WorkflowAction workflowAction)
    @Override
    public void end(Context context, WorkflowAction workflowAction) throws ActionExecutorException {
        String externalStatus = workflowAction.getExternalStatus();
        WorkflowAction.Status status = externalStatus.equals(SUCCEEDED) ?
                WorkflowAction.Status.OK : WorkflowAction.Status.ERROR;
        context.setEndData(status, getActionSignal(status));
    }

    //5- check(Context context, WorkflowAction workflowAction) ，如果你的action是sync action的话不会调用这个方法，所以这里应该抛出异常
    @Override
    public void check(Context context, WorkflowAction action) throws ActionExecutorException {
        throw new UnsupportedOperationException();
    }

    //6- kill(Context context, WorkflowAction workflowAction)
    @Override
    public void kill(Context context, WorkflowAction workflowAction) throws ActionExecutorException {
        context.setExternalStatus(KILLED);
        context.setExecutionData(KILLED, null);
    }

    //7- isCompleted(String s)
    @Override
    public boolean isCompleted(String externalStatus) {
        return true;
    }
}
