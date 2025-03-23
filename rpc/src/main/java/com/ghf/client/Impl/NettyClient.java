package com.ghf.client.Impl;

import com.ghf.client.RPCClient;
import com.ghf.client.handler.NettyClientHandler;
import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

public class NettyClient implements RPCClient {
    private String hostName;
    private Integer port;
    private static Bootstrap bootstrap;
    private static NioEventLoopGroup eventExecutors;

    public NettyClient(String hostName, Integer port) {
        this.hostName = hostName;
        this.port = port;
    }

    static{
        eventExecutors = new NioEventLoopGroup();
        //创建bootstrap对象，配置参数
        bootstrap = new Bootstrap();
        //设置线程组
        bootstrap.group(eventExecutors)
                //设置客户端的通道实现类型
                .channel(NioSocketChannel.class)
                //使用匿名内部类初始化通道
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new StringEncoder());
//                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(new ClassResolver() {
                            @Override
                            public Class<?> resolve(String s) throws ClassNotFoundException {
                                return Class.forName(s);
                            }
                        }));
                        //添加客户端通道的处理器
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }
    public void sendMessage(String str)
    {
        try{
            ChannelFuture channelFuture = bootstrap.connect(hostName, port).sync();
            System.out.println("-------客户端绑定成功");
            Channel channel = channelFuture.channel();
            System.out.println("-------客户端绑定成功2");
            channel.writeAndFlush(str);
            System.out.println("-------客户端绑定成功3");
            channel.closeFuture();
            System.out.println("-------客户端绑定成功4");

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public RpcResponse sendRPCRequest(RpcRequest request)  {

        try{
            ChannelFuture channelFuture = bootstrap.connect(hostName, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            AttributeKey<RpcResponse>attributeKey = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(attributeKey).get();

            return response;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }
}
