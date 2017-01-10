package com.zongmu.service.dto.task;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.zongmu.service.dto.DTOObject;
import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.mark.ShapeType;
import com.zongmu.service.dto.taskrecord.TaskRecordObject;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.TaskItemFile;

public class TaskItemObject extends DTOObject {

    private static final long serialVersionUID = 6864005019527777246L;

    private long id;
    private String taskItemNo;
    private String taskName;
    private TaskType taskType;
    private int subTotal;
    private int orderNo;
    private TaskItemStatus status;
    private String src;
    private int fps;
    private int sideCount;
    private ShapeType shapeType;
    private int point;
    private DateTime createTime;
    private DateTime updateTime;
    private String taskRecordNo;
    private AssetType assetType;
    private List<TaskRecordObject> taskRecords = new ArrayList<>();
    private List<ReviewRecord> reviewRecords = new ArrayList<>();
    private List<TaskItemFile> taskItemFiles = new ArrayList<>();
    private String ftpFolder;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getTaskItemNo() {
        return taskItemNo;
    }

    public void setTaskItemNo(String taskItemNo) {
        this.taskItemNo = taskItemNo;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public TaskItemStatus getStatus() {
        return status;
    }

    public void setStatus(TaskItemStatus status) {
        this.status = status;
    }

    public List<TaskRecordObject> getTaskRecords() {
        return taskRecords;
    }

    public void setTaskRecords(List<TaskRecordObject> taskRecords) {
        this.taskRecords = taskRecords;
    }

    public String getTaskRecordNo() {
        return taskRecordNo;
    }

    public void setTaskRecordNo(String taskRecordNo) {
        this.taskRecordNo = taskRecordNo;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public List<ReviewRecord> getReviewRecords() {
        return reviewRecords;
    }

    public void setReviewRecords(List<ReviewRecord> reviewRecords) {
        this.reviewRecords = reviewRecords;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

	public List<TaskItemFile> getTaskItemFiles() {
		return taskItemFiles;
	}

	public void setTaskItemFiles(List<TaskItemFile> taskItemFiles) {
		this.taskItemFiles = taskItemFiles;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
}
