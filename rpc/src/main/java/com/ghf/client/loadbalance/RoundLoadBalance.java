package com.ghf.client.loadbalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance{
    private int choose = -1;
    @Override
    public String balance(List<String> list) {
        choose++;
        choose = choose%(list.size());
        return list.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
