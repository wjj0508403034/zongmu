package com.zongmu.ffmpeg.services;

import java.util.List;

import com.zongmu.ffmpeg.entities.AssetFile;
import com.zongmu.ffmpeg.entities.Task;
import com.zongmu.ffmpeg.entities.TaskItem;
import com.zongmu.ffmpeg.entities.TaskItemFile;
import com.zongmu.ffmpeg.task.CommandResult;

public interface TaskService {

    List<TaskItem> getPendingTaskItems(int count);
    
    List<TaskItem> getFailedTaskItems(int count);
    
    List<AssetFile> getPendingAssetFiles(int count);
    
    List<AssetFile> getCompressFailedAssetFile(int count);
    
    List<TaskItemFile> getPendingUploadFiles(int count);
    
    Task getTask(String taskNo);
    
    void afterExecutionCommand(CommandResult commandResult);
    
    void setCompressState(AssetFile assetFile);
    
    void updateAssetFile(AssetFile assetFile);
    
    void moveDeleteAssetFiles(int count);
    
    void updateTaskItemFile(TaskItemFile taskItemFile);
}
