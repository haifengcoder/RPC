package com.ghf.client.loadbalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance{
    @Override
    public String balance(List<String> list) {
        Random random = new Random();
        int choose = random.nextInt(list.size());
        return list.get(choose);
    }

    @Override
    public void addNode(String node) {

    }

    @Override
    public void delNode(String node) {

    }
}
