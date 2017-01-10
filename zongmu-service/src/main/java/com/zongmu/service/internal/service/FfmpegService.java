package com.zongmu.service.internal.service;

import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.video.VideoInfo;

public interface FfmpegService {

	VideoInfo getVideoInfo(AssetFile assetFile);

	void cut(Task task, AssetFile assetFile, TaskItem taskItem);
}
