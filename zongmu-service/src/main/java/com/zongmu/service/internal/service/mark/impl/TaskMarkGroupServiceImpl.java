package com.zongmu.service.internal.service.mark.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.entity.mark.TaskMarkGroup;
import com.zongmu.service.entity.mark.TaskMarkPoint;
import com.zongmu.service.internal.service.mark.TaskMarkGroupService;
import com.zongmu.service.internal.service.mark.TaskMarkPointService;
import com.zongmu.service.repository.mark.TaskMarkGroupRepository;

@Service
public class TaskMarkGroupServiceImpl implements TaskMarkGroupService {

	@Autowired
	private TaskMarkGroupRepository taskMarkGroupRepo;

	@Autowired
	private TaskMarkPointService taskMarkPointService;

	@Override
	public List<TaskMarkGroup> getGroups(Long taskMarkRecordId) {
		List<TaskMarkGroup> groups = this.taskMarkGroupRepo.getShapes(taskMarkRecordId);
		for (TaskMarkGroup group : groups) {
			group.setPoints(this.taskMarkPointService.getPoints(group.getId()));
		}
		return groups;
	}

	@Override
	public void save(TaskMarkGroup group) {
		TaskMarkGroup newGroup = this.taskMarkGroupRepo.save(group);
		for (TaskMarkPoint point : group.getPoints()) {
			point.setTaskMarkGroupId(newGroup.getId());
			this.taskMarkPointService.save(point);
		}
	}

	@Override
	public void deleteGroup(Long taskMarkRecordId) {
		List<TaskMarkGroup> groups = this.taskMarkGroupRepo.getShapes(taskMarkRecordId);

		for (TaskMarkGroup group : groups) {
			this.taskMarkPointService.deletePoint(group.getId());
		}
		
		this.taskMarkGroupRepo.deleteShapes(taskMarkRecordId);
	}

}
