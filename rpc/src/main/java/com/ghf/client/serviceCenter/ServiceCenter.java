package com.ghf.client.serviceCenter;


import java.net.InetSocketAddress;

public interface ServiceCenter {
    public InetSocketAddress serviceDiscovery(String serviceName);
}
