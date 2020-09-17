package com.cqx.distributed.manager;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.distributed.BaseService;
import com.cqx.distributed.coordinatio.CoordinatioFactory;
import com.cqx.distributed.coordinatio.CoordinatioInf;
import com.cqx.distributed.net.InternalServer;
import com.cqx.distributed.resource.ResourceServiceBean;
import com.cqx.netty.util.ICallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理服务
 *
 * @author chenqixu
 */
public class ManagerService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);
    private Map<String, ResourceServiceBean> resourceServiceBeanMap = new HashMap<>();
    private CoordinatioInf coordinatioInf;
    private InternalServer internalServer;

    public ManagerService() {
        ManagerServiceBean managerServiceBean = new ManagerServiceBean();
        managerServiceBean.setIp("127.0.0.1");
        managerServiceBean.setPort(11000);
        //启动管理服务
        internalServer = new InternalServer(new ICallBack<ResourceServiceBean>() {
            @Override
            public void callBack(ResourceServiceBean resourceServiceBean) {
                //资源服务进行注册
                resourceServiceRegister(resourceServiceBean);
            }
        });
        try {
            internalServer.start(managerServiceBean.getPort());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //把资源注册服务的ip和端口写入到分布式应用程序协调服务
        coordinatioInf = CoordinatioFactory.getInstance();
        coordinatioInf.write("/managerservice", JSON.toJSONString(managerServiceBean));
    }

    @Override
    public void run() {
        while (isRun()) {
            SleepUtil.sleepMilliSecond(500);
        }
    }

    /**
     * 资源服务进行注册
     *
     * @param resourceServiceBean
     */
    private void resourceServiceRegister(ResourceServiceBean resourceServiceBean) {
        resourceServiceBeanMap.put(resourceServiceBean.getResource_name(), resourceServiceBean);
        logger.info("资源服务进行注册：{}", resourceServiceBean.getResource_name());
    }

    /**
     * 推送配置，管理服务分配任务到资源
     *
     * @param params
     */
    public void init(Map<String, Object> params) {
        //分配任务到资源

        //通知资源启动任务

    }
}
