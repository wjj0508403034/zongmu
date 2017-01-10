package com.zongmu.service.entity.mark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class TaskMarkPoint {

	@Id
	@SequenceGenerator(name = "TaskMarkPoint_SEQUENCE", sequenceName = "TaskMarkPoint_SEQUENCE")
	@GeneratedValue(generator = "TaskMarkPoint_SEQUENCE",strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Long taskMarkGroupId;

	@Column
	private float x;

	@Column
	private float y;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Long getTaskMarkGroupId() {
		return taskMarkGroupId;
	}

	public void setTaskMarkGroupId(Long taskMarkGroupId) {
		this.taskMarkGroupId = taskMarkGroupId;
	}
}
