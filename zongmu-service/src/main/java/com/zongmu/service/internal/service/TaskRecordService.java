package com.zongmu.service.internal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zongmu.service.dto.reviewrecord.RejectReasonObject;
import com.zongmu.service.dto.search.TaskRecordSearchParam;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskMark;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.exception.BusinessException;

public interface TaskRecordService {

	Page<TaskRecord> getTaskRecords(Pageable pageable, int status);

	List<TaskRecord> getTaskRecords(String taskItemNo) throws BusinessException;

	TaskRecord getTaskRecord(String taskRecordNo) throws BusinessException;

	TaskRecord getTaskRecordDetail(String taskRecordNo) throws BusinessException;

	void saveTaskMarks(String taskRecordNo, TaskMark taskMark) throws BusinessException;

	void saveTaskMarks(String taskRecordNo, TaskRecord taskRecord) throws BusinessException;

	TaskRecord getTaskMarks(String taskRecordNo,int state) throws BusinessException;

	TaskRecord save(TaskRecord taskRecord);

	void requestReview(String taskRecordNo) throws BusinessException;

	void cancel(String taskRecordNo) throws BusinessException;

	void cancelTimeoutTasks();

	TaskRecord createNewTaskRecord(TaskItem taskItem);

	void reviewFail(String taskRecordNo, RejectReasonObject rejectReasonObject) throws BusinessException;

	void reviewPass(String taskRecordNo, String memo) throws BusinessException;

	Algorithm getAlgorithm(String taskRecordNo) throws BusinessException;
	
	Long countRejectRecords();

	void deleteTaskRecord(TaskItem taskItem);

	Page<TaskRecord> search(Pageable pageable, TaskRecordSearchParam taskRecordSearchParam);

	void clearReviewRecord(String taskRecordNo);

	List<TaskRecord> getTaskRecordsByTaskId(Long taskId);
}
