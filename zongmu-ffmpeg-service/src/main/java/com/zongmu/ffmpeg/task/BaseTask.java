package com.zongmu.ffmpeg.task;

import org.springframework.context.ApplicationContext;

public abstract class BaseTask implements Runnable {

    protected ApplicationContext applicationContext;

    public BaseTask(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    

}
