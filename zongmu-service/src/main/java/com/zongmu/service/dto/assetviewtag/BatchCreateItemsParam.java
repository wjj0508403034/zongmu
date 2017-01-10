package com.zongmu.service.dto.assetviewtag;

import java.util.ArrayList;
import java.util.List;

public class BatchCreateItemsParam {

	private Long assetViewTagId;

	private List<String> items = new ArrayList<>();

	public Long getAssetViewTagId() {
		return assetViewTagId;
	}

	public void setAssetViewTagId(Long assetViewTagId) {
		this.assetViewTagId = assetViewTagId;
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}
}
