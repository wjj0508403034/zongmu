package com.zongmu.ffmpeg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class HealthController {

    @Autowired
    private ThreadPoolTaskExecutor threadPool;
    
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    @ResponseBody
    public String getVideoInfo() {
        return "hello world";
    }
}
