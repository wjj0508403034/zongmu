package com.zongmu.service.internal.service.mark.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.entity.mark.TaskMarkPoint;
import com.zongmu.service.internal.service.mark.TaskMarkPointService;
import com.zongmu.service.repository.mark.TaskMarkPointRepository;

@Service
public class TaskMarkPointServiceImpl implements TaskMarkPointService {

	@Autowired
	private TaskMarkPointRepository taskMarkPointRepo;

	@Override
	public List<TaskMarkPoint> getPoints(Long taskMarkGroupId) {
		return this.taskMarkPointRepo.getShapes(taskMarkGroupId);
	}

	@Override
	public void save(TaskMarkPoint point) {
		this.taskMarkPointRepo.save(point);
	}

	@Override
	public void deletePoint(Long taskMarkGroupId) {
		this.taskMarkPointRepo.deletePoints(taskMarkGroupId);
	}

}
