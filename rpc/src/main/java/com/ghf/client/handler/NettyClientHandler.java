package com.ghf.client.handler;

import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        Channel channel = channelHandlerContext.channel();
        AttributeKey attributeKey = AttributeKey.valueOf("RPCResponse");
        channel.attr(attributeKey).set(rpcResponse);
        channelHandlerContext.channel().close();
    }
}
