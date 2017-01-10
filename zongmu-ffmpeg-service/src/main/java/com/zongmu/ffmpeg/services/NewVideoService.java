package com.zongmu.ffmpeg.services;

import com.zongmu.ffmpeg.entities.AssetFile;
import com.zongmu.ffmpeg.entities.TaskItem;

public interface NewVideoService {

    void run(TaskItem taskItem);
    
    void compress(AssetFile assetFile);
    
    void compressFailed(AssetFile assetFile);
}
