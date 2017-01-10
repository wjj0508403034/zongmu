package com.zongmu.service.dto.asset;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.entity.Asset2AssetViewTag;

public class AssetViewTagParam {

	private List<Asset2AssetViewTag> items = new ArrayList<>();

	public List<Asset2AssetViewTag> getItems() {
		return items;
	}

	public void setItems(List<Asset2AssetViewTag> items) {
		this.items = items;
	}
}
