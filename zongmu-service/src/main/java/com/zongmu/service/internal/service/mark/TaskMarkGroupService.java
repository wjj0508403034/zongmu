package com.zongmu.service.internal.service.mark;

import java.util.List;

import com.zongmu.service.entity.mark.TaskMarkGroup;

public interface TaskMarkGroupService {

	List<TaskMarkGroup> getGroups(Long taskMarkRecordId);

	void save(TaskMarkGroup group);

	void deleteGroup(Long taskMarkRecordId);
}
