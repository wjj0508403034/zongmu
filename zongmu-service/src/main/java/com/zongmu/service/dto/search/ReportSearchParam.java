package com.zongmu.service.dto.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportSearchParam {

	private String assetName;

	private String assetNo;

	private Long algorithmId;

	private List<Long> assetViewItemIds = new ArrayList<>();

	private Map<Long, ArrayList<Long>> viewTagItemMap = new HashMap<Long, ArrayList<Long>>();

	private String taskName;

	private String taskItemNo;

	private DateRange assetRecordDate;

	private Compare recordLength;

	private List<SearchTaskStatus> taskItemStatus = new ArrayList<>();

	private SearchTaskStatus taskStatus;

	private DateRange taskDate;

	private DateRange uploadDate;

	private DateRange taskFinishDate;

	public boolean inViewTagRange(Long viewTagId, Long viewTagItemId) {
		if(this.viewTagItemMap.containsKey(viewTagId)){
			List<Long> list = this.viewTagItemMap.get(viewTagId);
			return list.contains(viewTagItemId);
		}
		
		return true;
	}

	public String getAssetName() {
		return assetName;
	}

	public List<Long> getAssetViewItemIds() {
		return assetViewItemIds;
	}

	public String getTaskName() {
		return taskName;
	}

	public DateRange getAssetRecordDate() {
		return assetRecordDate;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public void setAssetViewItemIds(List<Long> assetViewItemIds) {
		this.assetViewItemIds = assetViewItemIds;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setAssetRecordDate(DateRange assetRecordDate) {
		this.assetRecordDate = assetRecordDate;
	}

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public Compare getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(Compare recordLength) {
		this.recordLength = recordLength;
	}

	public SearchTaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(SearchTaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public DateRange getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(DateRange taskDate) {
		this.taskDate = taskDate;
	}

	public DateRange getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(DateRange uploadDate) {
		this.uploadDate = uploadDate;
	}

	public DateRange getTaskFinishDate() {
		return taskFinishDate;
	}

	public void setTaskFinishDate(DateRange taskFinishDate) {
		this.taskFinishDate = taskFinishDate;
	}

	public Map<Long, ArrayList<Long>> getViewTagItemMap() {
		return viewTagItemMap;
	}

	public void setViewTagItemMap(Map<Long, ArrayList<Long>> viewTagItemMap) {
		this.viewTagItemMap = viewTagItemMap;
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

	public List<SearchTaskStatus> getTaskItemStatus() {
		return taskItemStatus;
	}

	public void setTaskItemStatus(List<SearchTaskStatus> taskItemStatus) {
		this.taskItemStatus = taskItemStatus;
	}
}
