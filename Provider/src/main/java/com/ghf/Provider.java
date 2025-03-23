package com.ghf;

import com.ghf.conmon.URL;
import com.ghf.server.Impl.NettyServer;
import com.ghf.server.RPCServer;
import com.ghf.server.Impl.ThreadPoolServer;
import com.ghf.register.LocalRegister;
import com.ghf.register.MapRemoteRegister;

public class Provider {
    public static void main(String[] args) {
//        本地注册
        LocalRegister.register(HelloService.class.getName(), HelloServiceImpl.class);
//        注册中心注册（服务注册），用于将提供的服务以及服务器信息远程注册到rpc中
        URL url = new URL("localhost", 8086);
        MapRemoteRegister.register(HelloService.class.getName(), url);

        RPCServer server = new NettyServer();
        server.start(url.getPort());

    }
}
