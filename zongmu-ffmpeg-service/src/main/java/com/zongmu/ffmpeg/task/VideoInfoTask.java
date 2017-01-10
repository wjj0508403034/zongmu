package com.zongmu.ffmpeg.task;

import org.springframework.context.ApplicationContext;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.dto.VideoInfo;

public class VideoInfoTask extends VideoTask {

    private VideoInfo videoInfo;
    private AssetFileInfo assetFileInfo;

    public VideoInfoTask(ApplicationContext applicationContext, AssetFileInfo assetFileInfo) {
        super(applicationContext);
        this.assetFileInfo = assetFileInfo;
    }

    @Override
    public void run() {
        String fileName = this.assetFileInfo.getAssetNo() + "\\" + this.assetFileInfo.getFileName();
        this.videoInfo = this.getVideoInfo(fileName);
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

}
