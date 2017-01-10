package com.zongmu.service.dto.search;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.task.TaskItemStatus;

public class AssetSearchParam {

	private String assetName;
	private String assetNo;
	private String taskName;
	private String taskItemNo;
	private List<AssetType> assetTypes = new ArrayList<>();
	private List<Long> algorithmIds = new ArrayList<>();
	private List<TaskItemStatus> taskItemStatus = new ArrayList<>();
	private DateRange uploadDate;
	private DateRange videoRecordDate;
	private Compare videoLength;
	private DateRange taskFinishDate;
	private List<Long> assetViewTagItemIds = new ArrayList<>();
	private List<Long> viewTagItemIds = new ArrayList<>();

	public String getAssetName() {
		return assetName;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public List<AssetType> getAssetTypes() {
		return assetTypes;
	}

	public void setAssetTypes(List<AssetType> assetTypes) {
		this.assetTypes = assetTypes;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public List<Long> getAlgorithmIds() {
		return algorithmIds;
	}

	public void setAlgorithmIds(List<Long> algorithmIds) {
		this.algorithmIds = algorithmIds;
	}

	public List<TaskItemStatus> getTaskItemStatus() {
		return taskItemStatus;
	}

	public void setTaskItemStatus(List<TaskItemStatus> taskItemStatus) {
		this.taskItemStatus = taskItemStatus;
	}

	public DateRange getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(DateRange uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Compare getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(Compare videoLength) {
		this.videoLength = videoLength;
	}

	public DateRange getVideoRecordDate() {
		return videoRecordDate;
	}

	public void setVideoRecordDate(DateRange videoRecordDate) {
		this.videoRecordDate = videoRecordDate;
	}

	public DateRange getTaskFinishDate() {
		return taskFinishDate;
	}

	public void setTaskFinishDate(DateRange taskFinishDate) {
		this.taskFinishDate = taskFinishDate;
	}

	public List<Long> getAssetViewTagItemIds() {
		return assetViewTagItemIds;
	}

	public void setAssetViewTagItemIds(List<Long> assetViewTagItemIds) {
		this.assetViewTagItemIds = assetViewTagItemIds;
	}

	public List<Long> getViewTagItemIds() {
		return viewTagItemIds;
	}

	public void setViewTagItemIds(List<Long> viewTagItemIds) {
		this.viewTagItemIds = viewTagItemIds;
	}
}
