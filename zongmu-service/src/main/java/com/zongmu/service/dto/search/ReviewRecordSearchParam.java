package com.zongmu.service.dto.search;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;

public class ReviewRecordSearchParam {
	private String taskName;
	private String taskItemNo;
	private String assetName;
	private String assetNo;
	private String userName;
	private List<AssetType> assetTypes = new ArrayList<>();
	private List<Long> algorithmIds = new ArrayList<>();
	private List<Long> reasonIds = new ArrayList<>();
	private Compare point;
	private Compare videoLength;
	private List<ReviewRecordStatus> status = new ArrayList<>();
	private DateRange createDate;
	private DateRange uploadDate;
	private DateRange taskFinishDate;
	private DateRange videoRecordDate;
	private List<Long> assetViewTagItemIds = new ArrayList<>();
	private List<Long> viewTagItemIds = new ArrayList<>();

	public String getTaskName() {
		return taskName;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public String getAssetName() {
		return assetName;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
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

	public List<ReviewRecordStatus> getStatus() {
		return status;
	}

	public void setStatus(List<ReviewRecordStatus> status) {
		this.status = status;
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

	public List<Long> getReasonIds() {
		return reasonIds;
	}

	public void setReasonIds(List<Long> reasonIds) {
		this.reasonIds = reasonIds;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Compare getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(Compare videoLength) {
		this.videoLength = videoLength;
	}

}
