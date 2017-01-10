package com.zongmu.ffmpeg.internal.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.compress.dto.AssetInfo;
import com.zongmu.ffmpeg.cut.dto.TaskInfo;
import com.zongmu.ffmpeg.dto.CutVideoParam;
import com.zongmu.ffmpeg.dto.VideoInfo;
import com.zongmu.ffmpeg.internal.service.VideoService;
import com.zongmu.ffmpeg.task.CompressTask;
import com.zongmu.ffmpeg.task.CutTask;
import com.zongmu.ffmpeg.task.CutVideoTask;
import com.zongmu.ffmpeg.task.ExtractTask;
import com.zongmu.ffmpeg.task.VideoInfoTask;

@Service
public class VideoServiceImpl implements VideoService {
    private static Logger logger = Logger.getLogger(VideoServiceImpl.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThreadPoolTaskExecutor threadPool;

    @Override
    public VideoInfo getVideoInfo(AssetFileInfo assetFileInfo) {
        VideoInfoTask task = new VideoInfoTask(applicationContext, assetFileInfo);
        task.run();
        return task.getVideoInfo();
    }

    @Override
    public void compressAsset(AssetInfo assetInfo) {
        for (AssetFileInfo assetFileInfo : assetInfo.getAssetFileInfos()) {
            this.threadPool.execute(new CompressTask(applicationContext, assetFileInfo));
        }
    }

    @Override
    public void cut(TaskInfo taskInfo) {
        this.threadPool.execute(new CutTask(applicationContext, taskInfo));
    }

    @Override
    public void extract(TaskInfo taskInfo) {
        this.threadPool.execute(new ExtractTask(applicationContext, taskInfo));
    }

    @Override
    public void cutVideo(CutVideoParam cutVideoParam) {
        this.threadPool.execute(new CutVideoTask(applicationContext, cutVideoParam));
    }

}
