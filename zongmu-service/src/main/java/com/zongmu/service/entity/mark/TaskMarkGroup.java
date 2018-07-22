package com.zongmu.service.entity.mark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class TaskMarkGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -989939855421137986L;

	@Id
	@SequenceGenerator(name = "TaskMarkGroup_SEQUENCE", sequenceName = "TaskMarkGroup_SEQUENCE")
	@GeneratedValue(generator = "TaskMarkGroup_SEQUENCE",strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Long taskMarkRecordId;
	
	@Column
	private Long taskItemFileId;

	@Column
	private float time;

	@Column
	private Long frameIndex;

	@Transient
	private List<TaskMarkPoint> points = new ArrayList<>();
	
	@Transient
	private String taskItemFileName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<TaskMarkPoint> getPoints() {
		return points;
	}

	public void setPoints(List<TaskMarkPoint> points) {
		this.points = points;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public Long getFrameIndex() {
		return frameIndex;
	}

	public void setFrameIndex(Long frameIndex) {
		this.frameIndex = frameIndex;
	}

	public Long getTaskMarkRecordId() {
		return taskMarkRecordId;
	}

	public void setTaskMarkRecordId(Long taskMarkRecordId) {
		this.taskMarkRecordId = taskMarkRecordId;
	}

	public Long getTaskItemFileId() {
		return taskItemFileId;
	}

	public void setTaskItemFileId(Long taskItemFileId) {
		this.taskItemFileId = taskItemFileId;
	}

	public String getTaskItemFileName() {
		return taskItemFileName;
	}

	public void setTaskItemFileName(String taskItemFileName) {
		this.taskItemFileName = taskItemFileName;
	}
}
