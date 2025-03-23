package com.ghf.server.handler;

import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;
import com.ghf.register.LocalRegister;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response;
        try{
            Class clazz = LocalRegister.get(request.getInterfaceName());
            Method method = clazz.getMethod(request.getMethodName(),request.getParameterTypes());
            Object object = method.invoke(clazz.newInstance(), request.getParameters());
            response = RpcResponse.success(object);
        }catch (Exception e)
        {
            e.printStackTrace();
            response = RpcResponse.fail();
        }
        channelHandlerContext.writeAndFlush(response);
        channelHandlerContext.close();
    }
}
