package com.zongmu.service.internal.service;

import com.zongmu.service.entity.TaskMark;

public interface TaskMarkService {

    TaskMark createTaskMark(Long taskRecordId);

    TaskMark getTaskMark(Long taskRecordId);

    void saveTaskMarks(Long taskRecordId, TaskMark taskMark);

	void deleteTaskMarks(Long taskRecordId);
}
