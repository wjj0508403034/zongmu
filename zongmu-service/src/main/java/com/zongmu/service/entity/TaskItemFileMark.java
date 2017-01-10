package com.zongmu.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class TaskItemFileMark implements Serializable {

	private static final long serialVersionUID = -7101800566516531294L;

	@Id
	@SequenceGenerator(name = "TASKITEMFILEMARK_SEQUENCE", sequenceName = "TASKITEMFILEMARK_SEQUENCE")
	@GeneratedValue(generator = "TASKITEMFILEMARK_SEQUENCE",strategy = GenerationType.AUTO)
	private Long id;
	private Long taskMarkId;
	private String taskItemFileNo;

	@Transient
	private List<MarkShape> markShapes = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskMarkId() {
		return taskMarkId;
	}

	public void setTaskMarkId(Long taskMarkId) {
		this.taskMarkId = taskMarkId;
	}

	public String getTaskItemFileNo() {
		return taskItemFileNo;
	}

	public void setTaskItemFileNo(String taskItemFileNo) {
		this.taskItemFileNo = taskItemFileNo;
	}

	public List<MarkShape> getMarkShapes() {
		return markShapes;
	}

	public void setMarkShapes(List<MarkShape> markShapes) {
		this.markShapes = markShapes;
	}
}
