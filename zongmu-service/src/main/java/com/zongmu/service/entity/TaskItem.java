package com.zongmu.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.mark.ShapeType;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.dto.taskrecord.TaskRecordObject;

@Entity
@Table
public class TaskItem implements Serializable {

	private static final long serialVersionUID = 5483655886163893319L;

	@Id
	@SequenceGenerator(name = "TASKITEM_SEQUENCE", sequenceName = "TASKITEM_SEQUENCE")
	@GeneratedValue(generator = "TASKITEM_SEQUENCE", strategy = GenerationType.AUTO)
	private long id;
	private String assetNo;
	private Long taskId;
	private String taskName;
	private String taskItemNo;
	private int orderNo;
	private int point;
	private int sideCount;
	private ShapeType shapeType;
	private TaskItemStatus status = TaskItemStatus.NEW;
	private TaskType taskType;
	private String taskRecordNo;
	private boolean top;
	private int priority = 3;
	private boolean showHome = true;
	private AssetType assetType;
	private DateTime createTime;
	private DateTime updateTime;
	private Long weatherTagId;
	private Long roadTagId;
	private Long algorithmId;
	private Float videoLength;

	@Transient
	private AssetTag roadTag;

	@Transient
	private AssetTag weatherTag;

	@Transient
	private Task task;

	@Transient
	private List<TaskItemFile> taskItemFiles = new ArrayList<>();

	@Transient
	private List<TaskItemXViewTag> viewTags = new ArrayList<>();

	@Transient
	private Float startTime;

	@Transient
	private Float endTime;
	
	@Transient
	private String ftpFolder;
	
	@Transient
    private List<TaskRecordObject> taskRecords = new ArrayList<>();
	
	@Transient
    private List<ReviewRecord> reviewRecords = new ArrayList<>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public String getTaskRecordNo() {
		return taskRecordNo;
	}

	public void setTaskRecordNo(String taskRecordNo) {
		this.taskRecordNo = taskRecordNo;
	}

	public TaskItemStatus getStatus() {
		return status;
	}

	public void setStatus(TaskItemStatus status) {
		this.status = status;
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

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public List<TaskItemFile> getTaskItemFiles() {
		return taskItemFiles;
	}

	public void setTaskItemFiles(List<TaskItemFile> taskItemFiles) {
		this.taskItemFiles = taskItemFiles;
	}

	public Long getWeatherTagId() {
		return weatherTagId;
	}

	public void setWeatherTagId(Long weatherTagId) {
		this.weatherTagId = weatherTagId;
	}

	public Long getRoadTagId() {
		return roadTagId;
	}

	public void setRoadTagId(Long roadTagId) {
		this.roadTagId = roadTagId;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
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

	public boolean isShowHome() {
		return showHome;
	}

	public void setShowHome(boolean showHome) {
		this.showHome = showHome;
	}

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public AssetTag getRoadTag() {
		return roadTag;
	}

	public void setRoadTag(AssetTag roadTag) {
		this.roadTag = roadTag;
	}

	public AssetTag getWeatherTag() {
		return weatherTag;
	}

	public void setWeatherTag(AssetTag weatherTag) {
		this.weatherTag = weatherTag;
	}

	public Float getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(Float videoLength) {
		this.videoLength = videoLength;
	}

	public List<TaskItemXViewTag> getViewTags() {
		return viewTags;
	}

	public void setViewTags(List<TaskItemXViewTag> viewTags) {
		this.viewTags = viewTags;
	}

	public Float getStartTime() {
		return startTime;
	}

	public void setStartTime(Float startTime) {
		this.startTime = startTime;
	}

	public Float getEndTime() {
		return endTime;
	}

	public void setEndTime(Float endTime) {
		this.endTime = endTime;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public List<TaskRecordObject> getTaskRecords() {
		return taskRecords;
	}

	public void setTaskRecords(List<TaskRecordObject> taskRecords) {
		this.taskRecords = taskRecords;
	}

	public List<ReviewRecord> getReviewRecords() {
		return reviewRecords;
	}

	public void setReviewRecords(List<ReviewRecord> reviewRecords) {
		this.reviewRecords = reviewRecords;
	}
}
