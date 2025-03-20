package com.ghf;

import com.ghf.conmon.URL;
import com.ghf.protocal.RPCServer;
import com.ghf.protocal.ThreadPoolServer;
import com.ghf.register.LocalRegister;
import com.ghf.register.MapRemoteRegister;

public class Provider {
    public static void main(String[] args) {
//        本地注册
        LocalRegister.register(HelloService.class.getName(), HelloServiceImpl.class);
//        注册中心注册（服务注册），用于将提供的服务以及服务器信息远程注册到rpc中
        URL url = new URL("localhost", 8080);
        MapRemoteRegister.register(HelloService.class.getName(), url);

        RPCServer server = new ThreadPoolServer();
        server.start(url.getPort());

    }
}
