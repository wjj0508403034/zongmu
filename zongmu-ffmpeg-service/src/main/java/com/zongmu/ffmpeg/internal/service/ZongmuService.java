package com.zongmu.ffmpeg.internal.service;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.cut.dto.TaskInfo;
import com.zongmu.ffmpeg.dto.CutResult;
import com.zongmu.ffmpeg.dto.ShortVideoInfo;
import com.zongmu.ffmpeg.dto.VideoCutFailed;
import com.zongmu.ffmpeg.dto.VideoCutSuccess;

public interface ZongmuService {

    void compressFailed(AssetFileInfo assetFileInfo);

    void compressSuccess(AssetFileInfo assetFileInfo);

    void shortVideoCutSuccess(ShortVideoInfo shortVideoInfo);

    void shortVideoCutFailed(ShortVideoInfo shortVideoInfo);

    void videoCutFailed(VideoCutFailed videoCutFailed);

    void videoCutSuccess(VideoCutSuccess videoCutSuccess);

    void cut(TaskInfo taskInfo);

    void sendCutVideoResult(String taskItemNo, CutResult result);
}
