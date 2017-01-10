package com.zongmu.service.entity;

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
public class Task2AssetViewTag {

	@Id
	@SequenceGenerator(name = "Task2AssetViewTag_SEQUENCE", sequenceName = "Task2AssetViewTag_SEQUENCE")
	@GeneratedValue(generator = "Task2AssetViewTag_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long taskId;
	
	@Column
	private Long assetViewTagId;
	
	@Column
	private Long assetViewTagItemId;
	
	@Transient
	private AssetViewTag viewTag;
	
	@Transient
	private AssetViewTagItem viewTagItem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getAssetViewTagId() {
		return assetViewTagId;
	}

	public void setAssetViewTagId(Long assetViewTagId) {
		this.assetViewTagId = assetViewTagId;
	}

	public Long getAssetViewTagItemId() {
		return assetViewTagItemId;
	}

	public void setAssetViewTagItemId(Long assetViewTagItemId) {
		this.assetViewTagItemId = assetViewTagItemId;
	}

	public AssetViewTag getViewTag() {
		return viewTag;
	}

	public void setViewTag(AssetViewTag viewTag) {
		this.viewTag = viewTag;
	}

	public AssetViewTagItem getViewTagItem() {
		return viewTagItem;
	}

	public void setViewTagItem(AssetViewTagItem viewTagItem) {
		this.viewTagItem = viewTagItem;
	}
}
