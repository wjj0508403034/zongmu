package com.zongmu.ffmpeg.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.cut.dto.TaskInfo;
import com.zongmu.ffmpeg.dto.CutResult;
import com.zongmu.ffmpeg.dto.ShortVideoInfo;
import com.zongmu.ffmpeg.dto.VideoCutFailed;
import com.zongmu.ffmpeg.dto.VideoCutSuccess;
import com.zongmu.ffmpeg.internal.service.ZongmuService;
import com.zongmu.ffmpeg.properties.ZongmuServiceProperties;
import com.zongmu.ffmpeg.util.HttpClientWrapper;

@Service
public class ZongmuServiceImpl implements ZongmuService {

    @Autowired
    private HttpClientWrapper httpClient;

    @Autowired
    private ZongmuServiceProperties zongmuServiceProperties;

    @Override
    public void compressFailed(AssetFileInfo assetFileInfo) {
        String url = this.zongmuServiceProperties.getHost() + "assets/" + assetFileInfo.getAssetFileNo() + "/compressFailure";
        httpClient.post(url, null);
    }

    @Override
    public void compressSuccess(AssetFileInfo assetFileInfo) {
        String url = this.zongmuServiceProperties.getHost() + "assets/" + assetFileInfo.getAssetFileNo() + "/compressSuccess";
        httpClient.post(url, null);
    }

    @Override
    public void shortVideoCutSuccess(ShortVideoInfo shortVideoInfo) {
        String url = this.zongmuServiceProperties.getHost() + "tasks/cutItemSuccess";
        httpClient.post(url, shortVideoInfo);

    }

    @Override
    public void shortVideoCutFailed(ShortVideoInfo shortVideoInfo) {
        String url = this.zongmuServiceProperties.getHost() + "tasks/cutItemFailure";
        httpClient.post(url, shortVideoInfo);
    }

    @Override
    public void videoCutFailed(VideoCutFailed videoCutFailed) {
        String url = this.zongmuServiceProperties.getHost() + "tasks/cutFailure";
        httpClient.post(url, videoCutFailed);
    }

    @Override
    public void videoCutSuccess(VideoCutSuccess videoCutSuccess) {
        String url = this.zongmuServiceProperties.getHost() + "tasks/cutSuccess";
        httpClient.post(url, videoCutSuccess);
    }

    @Override
    public void cut(TaskInfo taskInfo) {
        String url = this.zongmuServiceProperties.getHost() + "tasks/" + taskInfo.getTaskNo() + "/cutCallback";
        httpClient.post(url, taskInfo);
    }

    @Override
    public void sendCutVideoResult(String taskItemNo, CutResult result) {
        String url = this.zongmuServiceProperties.getHost() + "tasks/cutFinished";
        httpClient.post(url, result);
    }
}
