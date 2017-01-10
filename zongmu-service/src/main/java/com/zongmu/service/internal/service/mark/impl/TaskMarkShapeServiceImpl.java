package com.zongmu.service.internal.service.mark.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.entity.mark.TaskMarkGroup;
import com.zongmu.service.entity.mark.TaskMarkShape;
import com.zongmu.service.internal.service.mark.TaskMarkGroupService;
import com.zongmu.service.internal.service.mark.TaskMarkShapeService;
import com.zongmu.service.repository.mark.TaskMarkShapeRepository;

@Service
public class TaskMarkShapeServiceImpl implements TaskMarkShapeService {

	@Autowired
	private TaskMarkShapeRepository taskMarkShapeRepo;

	@Autowired
	private TaskMarkGroupService taskMarkGroupService;

	@Override
	public List<TaskMarkShape> getShapes(Long taskMarkRecordId) {
		List<TaskMarkShape> shapes = this.taskMarkShapeRepo.getShapes(taskMarkRecordId);
		for (TaskMarkShape shape : shapes) {
			shape.setGroups(this.taskMarkGroupService.getGroups(shape.getId()));
		}

		return shapes;
	}

	@Override
	public void deleteShapes(Long taskMarkRecordId) {
		List<TaskMarkShape> shapes = this.taskMarkShapeRepo.getShapes(taskMarkRecordId);
		for(TaskMarkShape shape: shapes){
			this.taskMarkGroupService.deleteGroup(shape.getId());
		}
		
		this.taskMarkShapeRepo.deleteShapes(taskMarkRecordId);
	}

	@Override
	public void save(TaskMarkShape shape) {
		TaskMarkShape newShape = this.taskMarkShapeRepo.save(shape);
		for(TaskMarkGroup group: shape.getGroups()){
			//group.setTaskMarkShapeId(newShape.getId());
			this.taskMarkGroupService.save(group);
		}
	}
}
