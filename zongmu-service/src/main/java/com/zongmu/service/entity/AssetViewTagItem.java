package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class AssetViewTagItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8713603992040766425L;

	@Id
	@SequenceGenerator(name = "ASSET_VIEW_TAG_ITEM_SEQUENCE", sequenceName = "ASSET_VIEW_TAG_ITEM_SEQUENCE")
	@GeneratedValue(generator = "ASSET_VIEW_TAG_ITEM_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long assetViewTagId;
	
	@Column
	private String name;
	
	@Column
	private boolean isDefault;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAssetViewTagId() {
		return assetViewTagId;
	}

	public void setAssetViewTagId(Long assetViewTagId) {
		this.assetViewTagId = assetViewTagId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}
