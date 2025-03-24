package com.ghf.server.register;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;

/**
 * @author wxx
 * @version 1.0
 * @create 2024/5/3 17:28
 */
public class ZKServiceRegister implements ServiceRegister {
    // curator 提供的 Zookeeper 客户端
    private CuratorFramework client;
    // Zookeeper 根路径节点
    private static final String ROOT_PATH = "MyRPC";
    // 服务注册中心路径
    private static final String SERVICES_PATH = "/services";

    // 负责 Zookeeper 客户端的初始化，并与 Zookeeper 服务端进行连接
    public ZKServiceRegister() {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // Zookeeper 的地址固定，不管是服务提供者还是消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg 中的 tickTime 有关系，
        // zk 还会根据 minSessionTimeout 与 maxSessionTimeout 两个参数重新调整最后的超时值。默认分别为 tickTime 的 2 倍和 20 倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000).retryPolicy(policy).namespace(ROOT_PATH).build();
        this.client.start();
        System.out.println("Zookeeper 连接成功");

        // 确保注册中心路径存在
        try {
            if (client.checkExists().forPath(SERVICES_PATH) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(SERVICES_PATH);
                System.out.println("注册中心路径创建成功：" + SERVICES_PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 注册服务到注册中心
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            // 构造服务路径
            String servicePath = SERVICES_PATH + "/" + serviceName;

            // 创建服务名节点（永久节点）
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                System.out.println("服务名节点创建成功：" + servicePath);
            }

            // 构造服务实例路径
            String instanceName = getServiceAddress(serviceAddress); // 例如 "192.168.1.10:8080"
            String instancePath = servicePath + "/" + instanceName;

            // 创建服务实例节点（临时节点）
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(instancePath);
            System.out.println("服务实例注册成功：" + instancePath);
        } catch (Exception e) {
            System.out.println("注册服务失败：" + e.getMessage());
            e.printStackTrace();
        }
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