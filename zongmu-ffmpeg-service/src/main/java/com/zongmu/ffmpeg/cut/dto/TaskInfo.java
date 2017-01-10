package com.zongmu.ffmpeg.cut.dto;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.ffmpeg.compress.dto.AssetInfo;

public class TaskInfo {

    private String taskNo;
    private String assetNo;
    private int timeInterval;
    private AssetInfo assetInfo;
    private List<TaskItemInfo> taskItemInfos = new ArrayList<>();

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public List<TaskItemInfo> getTaskItemInfos() {
        return taskItemInfos;
    }

    public void setTaskItemInfos(List<TaskItemInfo> taskItemInfos) {
        this.taskItemInfos = taskItemInfos;
    }
}
