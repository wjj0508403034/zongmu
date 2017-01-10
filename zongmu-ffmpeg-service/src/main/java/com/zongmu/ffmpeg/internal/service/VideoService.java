package com.zongmu.ffmpeg.internal.service;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.compress.dto.AssetInfo;
import com.zongmu.ffmpeg.cut.dto.TaskInfo;
import com.zongmu.ffmpeg.dto.CutVideoParam;
import com.zongmu.ffmpeg.dto.VideoInfo;

public interface VideoService {


    VideoInfo getVideoInfo(AssetFileInfo assetFileInfo);
    
    void compressAsset(AssetInfo assetInfo);
    
    void cut(TaskInfo taskInfo);
    
    void extract(TaskInfo taskInfo);

    void cutVideo(CutVideoParam cutVideoParam);
}
