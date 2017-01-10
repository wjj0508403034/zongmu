package com.zongmu.service.dto.asset;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.entity.TaskItemXViewTag;

public class ViewTagParam {

	private List<TaskItemXViewTag> items = new ArrayList<>();

	public List<TaskItemXViewTag> getItems() {
		return items;
	}

	public void setItems(List<TaskItemXViewTag> items) {
		this.items = items;
	}
}
