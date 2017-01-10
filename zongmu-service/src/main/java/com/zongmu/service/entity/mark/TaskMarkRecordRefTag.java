package com.zongmu.service.entity.mark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.zongmu.service.entity.TagItem;

@Entity
@Table
public class TaskMarkRecordRefTag {

	@Id
	@SequenceGenerator(name = "TaskMarkRecordRefTag_SEQUENCE", sequenceName = "TaskMarkRecordRefTag_SEQUENCE")
	@GeneratedValue(generator = "TaskMarkRecordRefTag_SEQUENCE",strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Long taskMarkRecordId;

	@Column
	private Long tagItemId;

	@Transient
	private TagItem tagItem;

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

	public TagItem getTagItem() {
		return tagItem;
	}

	public void setTagItem(TagItem tagItem) {
		this.tagItem = tagItem;
	}

	public Long getTagItemId() {
		return tagItemId;
	}

	public void setTagItemId(Long tagItemId) {
		this.tagItemId = tagItemId;
	}
}
