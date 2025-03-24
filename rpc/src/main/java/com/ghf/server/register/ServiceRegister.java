package com.ghf.server.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    public void register(String serviceName, InetSocketAddress inetSocketAddress);
}
