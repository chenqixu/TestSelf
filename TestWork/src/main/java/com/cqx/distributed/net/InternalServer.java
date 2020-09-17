package com.cqx.distributed.net;

import com.alibaba.fastjson.JSON;
import com.cqx.distributed.resource.ResourceServiceBean;
import com.cqx.netty.bean.NettyBaseBean;
import com.cqx.netty.util.ICallBack;
import com.cqx.netty.util.IServer;
import com.cqx.netty.util.IServerHandler;
import io.netty.buffer.ByteBuf;

/**
 * 内部服务
 *
 * @author chenqixu
 */
public class InternalServer {
    private ICallBack<ResourceServiceBean> iCallBack;
    private IServer iServer;

    public InternalServer() {
        iServer = IServer.newbuilder();
    }

    public InternalServer(ICallBack<ResourceServiceBean> iCallBack) {
        this.iCallBack = iCallBack;
    }

    public void start(int port) throws Exception {
        iServer.setPort(port)
                .setiServerHandler(new RegisterServerHandler())
                .start();
    }

    public void close() {
        if (iServer != null) iServer.close();
    }

    class RegisterServerHandler extends IServerHandler {

        @Override
        protected void init() {

        }

        @Override
        protected ByteBuf dealHandler(ByteBuf buf) {
            NettyBaseBean responseBean = new NettyBaseBean();
            NettyBaseBean requestBean = new NettyBaseBean(buf);
            int head = requestBean.getHead();
            String body = requestBean.getBodyString();
            switch (ServerCodeEnum.valueOf(head)) {
                case Register://资源服务注册
                    //注册成功
                    responseBean.setHead(ServerCodeEnum.Success.getServerCode());
                    responseBean.setBody("资源服务注册成功");
                    //外部回调
                    if (iCallBack != null) {
                        iCallBack.callBack(JSON.parseObject(body, ResourceServiceBean.class));
                    }
                    break;
                default:
                    break;
            }
            return responseBean.serialize();
        }
    }
}
