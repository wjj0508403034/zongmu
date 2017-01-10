package com.zongmu.ffmpeg.task;

import org.springframework.context.ApplicationContext;

import com.zongmu.ffmpeg.properties.ZongmuServiceProperties;
import com.zongmu.ffmpeg.util.HttpClientWrapper;

public abstract class SendNotificationTask extends BaseTask {

    protected HttpClientWrapper httpClient;
    private ZongmuServiceProperties zongmuServiceProperties;

    public SendNotificationTask(ApplicationContext applicationContext) {
        super(applicationContext);
        this.httpClient = applicationContext.getBean(HttpClientWrapper.class);
        this.zongmuServiceProperties = applicationContext.getBean(ZongmuServiceProperties.class);
    }

    public final String getRequestUrl(String path) {
        return this.zongmuServiceProperties.getHost() + path;
    }
}
