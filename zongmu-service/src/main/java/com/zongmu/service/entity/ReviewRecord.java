package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;

@Entity
@Table
public class ReviewRecord implements Serializable {

	private static final long serialVersionUID = -7735455538179257348L;

	@Id
	@SequenceGenerator(name = "REVIEWRECORD_SEQUENCE", sequenceName = "REVIEWRECORD_SEQUENCE")
	@GeneratedValue(generator = "REVIEWRECORD_SEQUENCE", strategy = GenerationType.AUTO)
	private long id;

	private String reviewRecordNo;
	private String taskRecordNo;
	private Long taskItemId;
	private String taskItemNo;
	private ReviewRecordStatus status;
	private DateTime startTime;
	private DateTime endTime;
	private String memo;
	private Long reasonId;
	private int subtotal;
	private Long userId;
	private String assetNo;
	private AssetType assetType;
	private Long algorithmId;
	private Long taskId;
	
	@Transient
	private RejectReason reason;

	@Transient
	private String userName;

	@Transient
	private TaskRecord taskRecord;
	
	@Transient
	private TaskItem taskItem;
	
	@Transient
	private String ftpFolder;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReviewRecordNo() {
		return reviewRecordNo;
	}

	public void setReviewRecordNo(String reviewRecordNo) {
		this.reviewRecordNo = reviewRecordNo;
	}

	public String getTaskRecordNo() {
		return taskRecordNo;
	}

	public void setTaskRecordNo(String taskRecordNo) {
		this.taskRecordNo = taskRecordNo;
	}

	public ReviewRecordStatus getStatus() {
		return status;
	}

	public void setStatus(ReviewRecordStatus status) {
		this.status = status;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public int getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(int subtotal) {
		this.subtotal = subtotal;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public TaskRecord getTaskRecord() {
		return taskRecord;
	}

	public void setTaskRecord(TaskRecord taskRecord) {
		this.taskRecord = taskRecord;
	}

	public RejectReason getReason() {
		return reason;
	}

	public void setReason(RejectReason reason) {
		this.reason = reason;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public TaskItem getTaskItem() {
		return taskItem;
	}

	public void setTaskItem(TaskItem taskItem) {
		this.taskItem = taskItem;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
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

}
