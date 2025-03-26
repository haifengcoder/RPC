package com.ghf;

import com.ghf.server.rpcServer.NettyServer;
import com.ghf.server.rpcServer.RPCServer;
import com.ghf.server.register.LocalRegister;
import com.ghf.server.register.ServiceRegister;
import com.ghf.server.register.ZKServiceRegister;

import java.net.InetSocketAddress;

public class Provider {
    public static void main(String[] args) {
//        本地注册
        LocalRegister.register(HelloService.class.getName(), HelloServiceImpl.class);
//        注册中心注册（服务注册），用于将提供的服务以及服务器信息远程注册到rpc中
        InetSocketAddress url = new InetSocketAddress("localhost", 8086);
        ServiceRegister serviceRegister = new ZKServiceRegister();
        serviceRegister.register(HelloService.class.getName(), url);

        RPCServer server = new NettyServer();
        server.start(url.getPort());

    }
}
