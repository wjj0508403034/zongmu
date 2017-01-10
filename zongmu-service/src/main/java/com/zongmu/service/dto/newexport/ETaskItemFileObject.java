package com.zongmu.service.dto.newexport;

public class ETaskItemFileObject {

	private Long id;
	private String assetFileNo;
	private String taskFileItemNo;

	public Long getId() {
		return id;
	}

	public String getAssetFileNo() {
		return assetFileNo;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAssetFileNo(String assetFileNo) {
		this.assetFileNo = assetFileNo;
	}

	public String getTaskFileItemNo() {
		return taskFileItemNo;
	}

	public void setTaskFileItemNo(String taskFileItemNo) {
		this.taskFileItemNo = taskFileItemNo;
	}
}
