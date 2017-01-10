package com.zongmu.service.entity;

import java.io.Serializable;
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
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.dto.taskrecord.TaskRecordStatus;
import com.zongmu.service.entity.mark.TaskMarkRecord;

@Entity
@Table
public class TaskRecord implements Serializable {

	private static final long serialVersionUID = -5470954506946147800L;

	@Id
	@SequenceGenerator(name = "TASKRECORD_SEQUENCE", sequenceName = "TASKRECORD_SEQUENCE")
	@GeneratedValue(generator = "TASKRECORD_SEQUENCE", strategy = GenerationType.AUTO)
	private long id;

	private String taskRecordNo;

	private String taskItemNo;
	
	private Long taskId;
	
	private Long taskItemId;

	private Long userId;

	private DateTime startTime;

	private DateTime endTime;

	private DateTime updateTime;

	private TaskRecordStatus status;

	private String reviewRecordNo;

	private int point;

	private AssetType assetType;

	private TaskType taskType;
	
	private String assetNo;
	
	private Long algorithmId;

	@Transient
	private TaskItem taskItem;

	@Transient
	private TaskMark taskMark;

	@Transient
	private TaskRecord previousTaskRecord;

	@Transient
	private TaskRecord nextTaskRecord;

	@Transient
	private List<TaskMarkRecord> taskMarkRecords;
	
	@Transient
	private String taskName;
	
	@Transient
	private String userName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public TaskRecordStatus getStatus() {
		return status;
	}

	public void setStatus(TaskRecordStatus status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTaskRecordNo() {
		return taskRecordNo;
	}

	public void setTaskRecordNo(String taskRecordNo) {
		this.taskRecordNo = taskRecordNo;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public TaskItem getTaskItem() {
		return taskItem;
	}

	public void setTaskItem(TaskItem taskItem) {
		this.taskItem = taskItem;
	}

	public DateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(DateTime updateTime) {
		this.updateTime = updateTime;
	}

	public TaskMark getTaskMark() {
		return taskMark;
	}

	public void setTaskMark(TaskMark taskMark) {
		this.taskMark = taskMark;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getReviewRecordNo() {
		return reviewRecordNo;
	}

	public void setReviewRecordNo(String reviewRecordNo) {
		this.reviewRecordNo = reviewRecordNo;
	}

	public List<TaskMarkRecord> getTaskMarkRecords() {
		return taskMarkRecords;
	}

	public void setTaskMarkRecords(List<TaskMarkRecord> taskMarkRecords) {
		this.taskMarkRecords = taskMarkRecords;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public TaskRecord getPreviousTaskRecord() {
		return previousTaskRecord;
	}

	public void setPreviousTaskRecord(TaskRecord previousTaskRecord) {
		this.previousTaskRecord = previousTaskRecord;
	}

	public TaskRecord getNextTaskRecord() {
		return nextTaskRecord;
	}

	public void setNextTaskRecord(TaskRecord nextTaskRecord) {
		this.nextTaskRecord = nextTaskRecord;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getTaskItemId() {
		return taskItemId;
	}

	public void setTaskItemId(Long taskItemId) {
		this.taskItemId = taskItemId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
