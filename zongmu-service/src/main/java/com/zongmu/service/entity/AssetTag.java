package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.zongmu.service.dto.tag.TagCategory;

@Entity
@Table
public class AssetTag implements Serializable {

	private static final long serialVersionUID = 2804127183503260954L;

	@Id
	@SequenceGenerator(name = "ASSETTAG_SEQUENCE", sequenceName = "ASSETTAG_SEQUENCE")
	@GeneratedValue(generator = "ASSETTAG_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private TagCategory category;

	private boolean isDefault;

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

	public TagCategory getCategory() {
		return category;
	}

	public void setCategory(TagCategory category) {
		this.category = category;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

}
