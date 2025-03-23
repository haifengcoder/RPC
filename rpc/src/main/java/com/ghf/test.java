//package com.ghf;
//
//public class NettyClient implements RPCClient {
//    private String hostName;
//    private Integer port;
//    private static Bootstrap bootstrap;
//    private static NioEventLoopGroup eventExecutors;
//
//    public NettyClient(String hostName, Integer port) {
//        this.hostName = hostName;
//        this.port = port;
//    }
//
//    static {
//        eventExecutors = new NioEventLoopGroup();
//        bootstrap = new Bootstrap();
//        bootstrap.group(eventExecutors)
//                .channel(NioSocketChannel.class)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
////                        ch.pipeline().addLast(new StringEncoder());
////                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new NettyClientHandler2());
//                    }
//                });
//    }
//
//    public void sendMessage(String str) {
//        try {
//            ChannelFuture channelFuture = bootstrap.connect(hostName, port).sync();
//            System.out.println("-------客户端绑定成功");
//            Channel channel = channelFuture.channel();
//            System.out.println("-------客户端绑定成功2");
//            channel.writeAndFlush(str);
//            System.out.println("-------客户端绑定成功3");
//            channel.closeFuture().sync();
//            System.out.println("-------客户端绑定成功4");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public RpcResponse sendRPCRequest(RpcRequest request) throws InterruptedException {
//        return null;
//    }
//}