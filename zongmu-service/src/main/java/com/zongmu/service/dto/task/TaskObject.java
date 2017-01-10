package com.zongmu.service.dto.task;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.DTOObject;
import com.zongmu.service.dto.mark.ShapeType;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.entity.TaskXViewTag;

public class TaskObject extends DTOObject {

    private static final long serialVersionUID = 6864005019527777246L;
    private String taskNo;
    private String assetNo;
    private String taskName;
    private TaskType taskType;
    private String memo;
    private int timeInterval;
    private ShapeType shapeType;
    private int sideCount;
    private int point;
    private List<AssetTag> assetTags = new ArrayList<>();
    private boolean top;
	private int priority = 3;
	private Long algorithmId;
	private boolean showHome = true;
	private List<TaskXViewTag> viewTags = new ArrayList<>();
	private String ftpFolder;

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public List<AssetTag> getAssetTags() {
        return assetTags;
    }

    public void setAssetTags(List<AssetTag> assetTags) {
        this.assetTags = assetTags;
    }

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public boolean isShowHome() {
		return showHome;
	}

	public void setShowHome(boolean showHome) {
		this.showHome = showHome;
	}

	public List<TaskXViewTag> getViewTags() {
		return viewTags;
	}

	public void setViewTags(List<TaskXViewTag> viewTags) {
		this.viewTags = viewTags;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

}
