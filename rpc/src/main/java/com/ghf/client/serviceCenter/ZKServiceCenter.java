package com.ghf.client.serviceCenter;

import com.ghf.client.loadbalance.ConsistencyHashBalance;
import com.ghf.client.loadbalance.LoadBalance;
import com.ghf.client.loadbalance.RandomLoadBalance;
import com.ghf.client.loadbalance.RoundLoadBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceCenter implements ServiceCenter {
    private CuratorFramework client;
    // Zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";

    private ServiceCache serviceCache;

    private LoadBalance loadBalance = new RandomLoadBalance();
//    private LoadBalance loadBalance = new ConsistencyHashBalance();

    // 负责 Zookeeper 客户端的初始化，并与 Zookeeper 服务端进行连接
    public ZKServiceCenter() throws InterruptedException {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // Zookeeper 的地址固定，不管是服务提供者还是消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg 中的 tickTime 有关系，
        // zk 还会根据 minSessionTimeout 与 maxSessionTimeout 两个参数重新调整最后的超时值。默认分别为 tickTime 的 2 倍和 20 倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        System.out.println("Zookeeper 连接成功");
        serviceCache = new ServiceCache();

        WatcherZK watcherZK = new WatcherZK(client, serviceCache);
        watcherZK.watchToUpdate("/");

    }

    // 根据服务名（接口名）返回地址
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {


        try {
            List<String> serviceList = serviceCache.getServcieFromCache(serviceName);
            if(serviceList == null)
            {
                // 构造服务路径
                String servicePath = "/" + serviceName;
                serviceList = client.getChildren().forPath(servicePath);
            }
            if (serviceList == null || serviceList.isEmpty()) {
                System.out.println("未找到服务实例：" + serviceName);
                return null;
            }

//            负载均衡
            String instance = loadBalance.balance(serviceList);
            return parseAddress(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    // 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }
}