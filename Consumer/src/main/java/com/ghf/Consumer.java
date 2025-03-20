package com.ghf;

import com.ghf.proxy.ProxyFactory;

public class Consumer {

    public static void main(String[] args) {

        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("ghf");
        System.out.println(result);

    }
}
