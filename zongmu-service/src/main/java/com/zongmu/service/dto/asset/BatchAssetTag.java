package com.zongmu.service.dto.asset;

import com.zongmu.service.dto.tag.TagCategory;

public class BatchAssetTag {

	private TagCategory category;
	private String value = "";

	public TagCategory getCategory() {
		return category;
	}

	public void setCategory(TagCategory category) {
		this.category = category;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
