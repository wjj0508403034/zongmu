package com.zongmu.service.entity.mark;

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
public class TaskMarkShape {

	@Id
	@SequenceGenerator(name = "TaskMarkShape_SEQUENCE", sequenceName = "TaskMarkShape_SEQUENCE")
	@GeneratedValue(generator = "TaskMarkShape_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Long taskMarkRecordId;

	@Column
	private Integer startIndex;

	@Column
	private Integer endIndex;

	@Transient
	private List<TaskMarkGroup> groups = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskMarkRecordId() {
		return taskMarkRecordId;
	}

	public void setTaskMarkRecordId(Long taskMarkRecordId) {
		this.taskMarkRecordId = taskMarkRecordId;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}

	public List<TaskMarkGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<TaskMarkGroup> groups) {
		this.groups = groups;
	}
}
