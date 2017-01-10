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
public class Asset2AssetViewTag {

	@Id
	@SequenceGenerator(name = "Asset2AssetViewTag_SEQUENCE", sequenceName = "Asset2AssetViewTag_SEQUENCE")
	@GeneratedValue(generator = "Asset2AssetViewTag_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long assetId;
	
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

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
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
