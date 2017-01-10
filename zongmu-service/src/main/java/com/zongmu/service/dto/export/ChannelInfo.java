package com.zongmu.service.dto.export;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.entity.mark.TaskMarkGroup;

public class ChannelInfo {

	private List<TaskMarkGroup> groups = new ArrayList<>();

	public List<TaskMarkGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<TaskMarkGroup> groups) {
		this.groups = groups;
	}
	
	
}
