package com.zongmu.service.internal.service.mark;

import java.util.List;

import com.zongmu.service.entity.mark.TaskMarkPoint;

public interface TaskMarkPointService {

	List<TaskMarkPoint> getPoints(Long taskMarkGroupId);

	void save(TaskMarkPoint point);

	void deletePoint(Long taskMarkGroupId);
}
