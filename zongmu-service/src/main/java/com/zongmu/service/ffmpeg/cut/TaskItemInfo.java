package com.zongmu.service.ffmpeg.cut;

import java.util.ArrayList;
import java.util.List;

public class TaskItemInfo {

	private int order;
	private List<TaskItemFileInfo> taskItemFileInfos = new ArrayList<>();

	public List<TaskItemFileInfo> getTaskItemFileInfos() {
		return taskItemFileInfos;
	}

	public void setTaskItemFileInfos(List<TaskItemFileInfo> taskItemFileInfos) {
		this.taskItemFileInfos = taskItemFileInfos;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
