package com.ghf.proxy;

import com.ghf.conmon.RpcRequest;
import com.ghf.conmon.RpcResponse;
import com.ghf.conmon.URL;
import com.ghf.loadbalance.LoadBalance;
import com.ghf.protocal.HttpClient;
import com.ghf.register.LocalRegister;
import com.ghf.register.MapRemoteRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactory {
//    这里泛型的T是隐式类型推断，根据方法调用时被赋值给的变量类型为HelloService helloService = ProxyFactory.getProxy(HelloService.class);推断出为HelloService类型
    public static  <T> T getProxy(Class interfaceClass)
    {
        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest rpcRequest = new RpcRequest(interfaceClass.getName(),method.getName(), method.getParameterTypes(),args);

//              服务发现（获取提供指定接口名称的服务器的URL信息）
//                List<URL> list = MapRemoteRegister.get(interfaceClass.getName());

//                负载均衡，从服务器列表中挑选一个服务器
//                URL url = LoadBalance.random(list);
//                服务调用
                RpcResponse response = HttpClient.send("localhost",8080, rpcRequest);
                return response.getData();
            }
        });
        return (T)proxyInstance;
    }
}
