package com.zongmu.service.internal.service.mark;

import java.util.List;

import com.zongmu.service.entity.mark.TaskMarkShape;

public interface TaskMarkShapeService {

	List<TaskMarkShape> getShapes(Long taskMarkRecordId);
	
	void deleteShapes(Long taskMarkRecordId);

	void save(TaskMarkShape shape);
}
