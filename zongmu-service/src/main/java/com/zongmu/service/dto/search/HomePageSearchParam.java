package com.zongmu.service.dto.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.task.TaskItemStatus;

public class HomePageSearchParam {

	private String taskName;
	private String taskItemNo;
	private String assetName;
	private String assetNo;
	private List<AssetType> assetTypes = new ArrayList<>();
	private List<Long> algorithmIds = new ArrayList<>();
	private Compare point;
	private List<TaskItemStatus> taskItemStatus = new ArrayList<>();
	private DateRange createDate;
	private DateRange uploadDate;
	private Compare videoLength;
	private DateRange taskFinishDate;
	private DateRange videoRecordDate;
	private List<Long> assetViewTagItemIds = new ArrayList<>();
	private List<Long> viewTagItemIds = new ArrayList<>();

	public boolean isNull() {
		if (!StringUtils.isEmpty(taskName)) {
			return false;
		}

		if (!StringUtils.isEmpty(taskItemNo)) {
			return false;
		}

		if (!StringUtils.isEmpty(assetName)) {
			return false;
		}

		if (!StringUtils.isEmpty(assetNo)) {
			return false;
		}

		if (assetTypes.size() != 0) {
			return false;
		}

		if (algorithmIds.size() != 0) {
			return false;
		}

		if (point != null && !point.isNull()) {
			return false;
		}

		if (taskItemStatus.size() != 0) {
			return false;
		}

		if (createDate != null && !createDate.isNull()) {
			return false;
		}

		if (uploadDate != null && !uploadDate.isNull()) {
			return false;
		}

		if (videoLength != null && !videoLength.isNull()) {
			return false;
		}

		if (taskFinishDate != null && !taskFinishDate.isNull()) {
			return false;
		}

		if (videoRecordDate != null && !videoRecordDate.isNull()) {
			return false;
		}

		if (assetViewTagItemIds.size() != 0) {
			return false;
		}

		if (viewTagItemIds.size() != 0) {
			return false;
		}

		return true;
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

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getAssetNo() {
		return assetNo;
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

	public List<Long> getAlgorithmIds() {
		return algorithmIds;
	}

	public void setAlgorithmIds(List<Long> algorithmIds) {
		this.algorithmIds = algorithmIds;
	}

	public Compare getPoint() {
		return point;
	}

	public void setPoint(Compare point) {
		this.point = point;
	}

	public List<TaskItemStatus> getTaskItemStatus() {
		return taskItemStatus;
	}

	public void setTaskItemStatus(List<TaskItemStatus> taskItemStatus) {
		this.taskItemStatus = taskItemStatus;
	}

	public DateRange getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateRange createDate) {
		this.createDate = createDate;
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

	public DateRange getVideoRecordDate() {
		return videoRecordDate;
	}

	public void setVideoRecordDate(DateRange videoRecordDate) {
		this.videoRecordDate = videoRecordDate;
	}

}
