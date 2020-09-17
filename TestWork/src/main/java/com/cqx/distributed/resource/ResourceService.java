package com.cqx.distributed.resource;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.distributed.BaseService;
import com.cqx.distributed.coordinatio.CoordinatioFactory;
import com.cqx.distributed.coordinatio.CoordinatioInf;
import com.cqx.distributed.manager.ManagerServiceBean;
import com.cqx.distributed.net.InternalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 资源服务
 *
 * @author chenqixu
 */
public class ResourceService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private CoordinatioInf coordinatioInf;
    private InternalClient internalClient;
    private ResourceServiceBean resourceServiceBean;
    private volatile boolean isRegister = false;

    public ResourceService() throws Exception {
        //从分布式应用程序协调服务获取管理服务信息
        coordinatioInf = CoordinatioFactory.getInstance();
    }

    private void register() {
        String serverMsg = coordinatioInf.read("/managerservice");
        logger.info("serverMsg：{}", serverMsg);
        if (serverMsg != null) {
            ManagerServiceBean managerServiceBean = JSON.parseObject(serverMsg, ManagerServiceBean.class);
            //管理服务客户端初始化
            internalClient = new InternalClient();
            internalClient.init(managerServiceBean.getIp(), managerServiceBean.getPort());
            //资源bean
            resourceServiceBean = new ResourceServiceBean();
            resourceServiceBean.setResource_name(UUID.randomUUID().toString());
            resourceServiceBean.setIp("127.0.0.1");
            //向管理服务进行注册
            try {
                internalClient.register(resourceServiceBean);
                isRegister = true;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void run() {
        while (isRun()) {
            if (!isRegister) {//未注册
                register();
            } else {//已注册

            }
            SleepUtil.sleepMilliSecond(500);
        }
    }

    public String getResource_name() {
        return resourceServiceBean.getResource_name();
    }
}
