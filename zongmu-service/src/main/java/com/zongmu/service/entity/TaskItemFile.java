package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.zongmu.service.dto.task.FileUploadStatus;
import com.zongmu.service.dto.task.TaskItemFileStatus;

@Entity
@Table
public class TaskItemFile implements Serializable {

	private static final long serialVersionUID = 1723190165736838195L;

	@Id
	@SequenceGenerator(name = "TASKITEMFILE_SEQUENCE", sequenceName = "TASKITEMFILE_SEQUENCE")
	@GeneratedValue(generator = "TASKITEMFILE_SEQUENCE",strategy = GenerationType.AUTO)
	private long id;

	private String taskItemNo;
	private String assetFileNo;
	private String taskItemFileNo;
	private float height;
	private float width;
	private int fps;
	private float fromTime;
	private float toTime;
	private String path;
	private Long taskId;
	private TaskItemFileStatus status;
	private FileUploadStatus uploadStatus = FileUploadStatus.None;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTaskItemFileNo() {
		return taskItemFileNo;
	}

	public void setTaskItemFileNo(String taskItemFileNo) {
		this.taskItemFileNo = taskItemFileNo;
	}

	public String getAssetFileNo() {
		return assetFileNo;
	}

	public void setAssetFileNo(String assetFileNo) {
		this.assetFileNo = assetFileNo;
	}

	public float getFromTime() {
		return fromTime;
	}

	public void setFromTime(float fromTime) {
		this.fromTime = fromTime;
	}

	public float getToTime() {
		return toTime;
	}

	public void setToTime(float toTime) {
		this.toTime = toTime;
	}

	public TaskItemFileStatus getStatus() {
		return status;
	}

	public void setStatus(TaskItemFileStatus status) {
		this.status = status;
	}

	public FileUploadStatus getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(FileUploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

}
