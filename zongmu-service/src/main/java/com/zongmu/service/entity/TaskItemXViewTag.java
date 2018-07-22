package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class TaskItemXViewTag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7285086454346208799L;

	@Id
	@SequenceGenerator(name = "TaskItemXViewTag_SEQUENCE", sequenceName = "TaskItemXViewTag_SEQUENCE")
	@GeneratedValue(generator = "TaskItemXViewTag_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	private Long taskItemId;

	private Long viewTagId;

	private Long viewTagItemId;

	@Transient
	private ViewTag viewTag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getViewTagId() {
		return viewTagId;
	}

	public void setViewTagId(Long viewTagId) {
		this.viewTagId = viewTagId;
	}

	public Long getViewTagItemId() {
		return viewTagItemId;
	}

	public void setViewTagItemId(Long viewTagItemId) {
		this.viewTagItemId = viewTagItemId;
	}

	public ViewTag getViewTag() {
		return viewTag;
	}

	public void setViewTag(ViewTag viewTag) {
		this.viewTag = viewTag;
	}

	public Long getTaskItemId() {
		return taskItemId;
	}

	public void setTaskItemId(Long taskItemId) {
		this.taskItemId = taskItemId;
	}
}
