package com.zongmu.service.ffmpeg;

import com.zongmu.service.dto.task.TaskType;

public class CutVideoParam {

	private String assetNo;
	private String taskNo;
	private String taskItemNo;
	private String fileName;
	private int orderNo;
	private float timeLength;
	private String assetFileNo;
	private TaskType taskType;

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public float getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(float timeLength) {
		this.timeLength = timeLength;
	}

	public String getAssetFileNo() {
		return assetFileNo;
	}

	public void setAssetFileNo(String assetFileNo) {
		this.assetFileNo = assetFileNo;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
}
