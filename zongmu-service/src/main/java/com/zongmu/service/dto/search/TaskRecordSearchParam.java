package com.zongmu.service.dto.search;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.taskrecord.TaskRecordStatus;

public class TaskRecordSearchParam {

	private String taskName;
	private String taskItemNo;
	private List<AssetType> assetTypes = new ArrayList<>();
	private List<TaskRecordStatus> taskRecordStatus = new ArrayList<>();
	private List<Long> algorithmIds = new ArrayList<>();
	private List<Long> assetViewTagItemIds = new ArrayList<>();
	private List<Long> viewTagItemIds = new ArrayList<>();
	private Compare point;
	private DateRange createDate;
	private DateRange uploadDate;
	private DateRange taskFinishDate;
	private DateRange videoRecordDate;

	public String getTaskName() {
		return taskName;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public List<AssetType> getAssetTypes() {
		return assetTypes;
	}

	public List<TaskRecordStatus> getTaskRecordStatus() {
		return taskRecordStatus;
	}

	public List<Long> getAlgorithmIds() {
		return algorithmIds;
	}

	public List<Long> getAssetViewTagItemIds() {
		return assetViewTagItemIds;
	}

	public List<Long> getViewTagItemIds() {
		return viewTagItemIds;
	}

	public Compare getPoint() {
		return point;
	}

	public DateRange getCreateDate() {
		return createDate;
	}

	public DateRange getUploadDate() {
		return uploadDate;
	}

	public DateRange getTaskFinishDate() {
		return taskFinishDate;
	}

	public DateRange getVideoRecordDate() {
		return videoRecordDate;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public void setAssetTypes(List<AssetType> assetTypes) {
		this.assetTypes = assetTypes;
	}

	public void setTaskRecordStatus(List<TaskRecordStatus> taskRecordStatus) {
		this.taskRecordStatus = taskRecordStatus;
	}

	public void setAlgorithmIds(List<Long> algorithmIds) {
		this.algorithmIds = algorithmIds;
	}

	public void setAssetViewTagItemIds(List<Long> assetViewTagItemIds) {
		this.assetViewTagItemIds = assetViewTagItemIds;
	}

	public void setViewTagItemIds(List<Long> viewTagItemIds) {
		this.viewTagItemIds = viewTagItemIds;
	}

	public void setPoint(Compare point) {
		this.point = point;
	}

	public void setCreateDate(DateRange createDate) {
		this.createDate = createDate;
	}

	public void setUploadDate(DateRange uploadDate) {
		this.uploadDate = uploadDate;
	}

	public void setTaskFinishDate(DateRange taskFinishDate) {
		this.taskFinishDate = taskFinishDate;
	}

	public void setVideoRecordDate(DateRange videoRecordDate) {
		this.videoRecordDate = videoRecordDate;
	}
}
