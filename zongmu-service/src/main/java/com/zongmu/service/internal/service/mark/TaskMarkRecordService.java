package com.zongmu.service.internal.service.mark;

import java.util.List;

import com.zongmu.service.entity.mark.TaskMarkRecord;

public interface TaskMarkRecordService {

	List<TaskMarkRecord> getRecords(Long taskRecordId);
	
	void saveRecord(Long taskRecordId, List<TaskMarkRecord> taskMarkRecords);
	
	Long countByColorTag(Long colorTagId);

	void deleteRecords(Long taskRecordId);

	List<TaskMarkRecord> getSimpleRecords(Long taskRecordId);
}
