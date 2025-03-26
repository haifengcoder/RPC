package com.ghf.server.rpcServer;

import com.ghf.server.handler.NettyServerHandler;
import com.ghf.server.rateLimit.RateLimitProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyServer implements RPCServer {
    private Integer port;
    private RateLimitProvider rateLimitProvider;

    @Override
    public void start(Integer port) {
        this.port = port;
        rateLimitProvider = new RateLimitProvider();



        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
//                            channel.pipeline().addLast(new StringDecoder());
//                            channel.pipeline().addLast(new StringEncoder());
                            channel.pipeline().addLast(new ObjectEncoder());
                            channel.pipeline().addLast(new ObjectDecoder(new ClassResolver() {
                                @Override
                                public Class<?> resolve(String s) throws ClassNotFoundException {
                                    return Class.forName(s);
                                }
                            }));

                            channel.pipeline().addLast(new NettyServerHandler(rateLimitProvider));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            System.out.println("-----------服务已启动,端口：" + port);
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }

    @Override
    public void stop() {

    }
    static {

    }
}