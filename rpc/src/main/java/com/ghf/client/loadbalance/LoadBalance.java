package com.ghf.client.loadbalance;

import java.util.List;

public interface LoadBalance {
    public String balance(List<String> list);
    public void addNode(String node);
    public void delNode(String node);
}
