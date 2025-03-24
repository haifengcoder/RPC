package com.ghf.proxy;

import com.ghf.client.Impl.NettyClient;
import com.ghf.client.serviceCenter.ServiceCenter;
import com.ghf.client.serviceCenter.ZKServiceCenter;
import com.ghf.conmon.RpcRequest;
import com.ghf.client.RPCClient;
import com.ghf.client.Impl.SimpleSocketClient;
import com.ghf.conmon.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

public class ProxyFactory {
    private static RPCClient rpcClient;
    private static ServiceCenter serviceCenter;
    static {
        if (rpcClient == null)
        {
            rpcClient = new NettyClient();
            try {
                serviceCenter = new ZKServiceCenter();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


//    这里泛型的T是隐式类型推断，根据方法调用时被赋值给的变量类型为HelloService helloService = ProxyFactory.getProxy(HelloService.class);推断出为HelloService类型
    public static  <T> T getProxy(Class interfaceClass)
    {
        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest rpcRequest = new RpcRequest(interfaceClass.getName(),method.getName(), method.getParameterTypes(),args);


//                负载均衡，从服务器列表中挑选一个服务器
//                URL url = LoadBalance.random(list);
//                服务调用
                InetSocketAddress inetSocketAddress = serviceCenter.serviceDiscovery(interfaceClass.getName());
                RpcResponse response = rpcClient.sendRPCRequest(inetSocketAddress.getHostName(),inetSocketAddress.getPort(),rpcRequest);
                return response.getData();

            }
        });
        return (T)proxyInstance;
    }
}
