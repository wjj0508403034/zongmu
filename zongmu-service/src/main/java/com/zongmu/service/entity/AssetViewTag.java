package com.zongmu.service.entity;

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
public class AssetViewTag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6580172880505305273L;

	@Id
	@SequenceGenerator(name = "ASSET_VIEW_TAG_SEQUENCE", sequenceName = "ASSET_VIEW_TAG_SEQUENCE")
	@GeneratedValue(generator = "ASSET_VIEW_TAG_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private String name;
	
	@Transient
	private List<AssetViewTagItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AssetViewTagItem> getItems() {
		return items;
	}

	public void setItems(List<AssetViewTagItem> items) {
		this.items = items;
	}
	
	
}
