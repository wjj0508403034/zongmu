package com.zongmu.ffmpeg.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

@Entity
@Table
public class TaskItem {

    @Id
    @SequenceGenerator(name = "TASKITEM_SEQUENCE", sequenceName = "TASKITEM_SEQUENCE")
    @GeneratedValue(generator = "TASKITEM_SEQUENCE", strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String assetNo;

    @Column
    private Long taskId;

    @Column
    private String taskItemNo;

    @Column
    private TaskType taskType;
    
    @Column
    private AssetType assetType;

    @Column
    private int orderNo;

    @Column
    private DateTime updateTime;

    @Column
    private TaskItemStatus status = TaskItemStatus.NEW;
    
    @Transient
    private Task task;
    
    @Transient
    private List<AssetFile> assetFiles = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getTaskItemNo() {
        return taskItemNo;
    }

    public void setTaskItemNo(String taskItemNo) {
        this.taskItemNo = taskItemNo;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public TaskItemStatus getStatus() {
        return status;
    }

    public void setStatus(TaskItemStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<AssetFile> getAssetFiles() {
        return assetFiles;
    }

    public void setAssetFiles(List<AssetFile> assetFiles) {
        this.assetFiles = assetFiles;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }
}
