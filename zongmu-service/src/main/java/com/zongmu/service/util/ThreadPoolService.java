package com.zongmu.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ThreadPoolService {

    @Autowired
    private ThreadPoolTaskExecutor threadPool;

    public void run(Runnable runable) {
        this.threadPool.execute(runable);
    }
}
