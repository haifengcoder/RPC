package com.ghf.loadbalance;

import com.ghf.conmon.URL;

import java.util.List;
import java.util.Random;

public class LoadBalance {
    public static URL random(List<URL> list)
    {
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
