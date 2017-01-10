package com.zongmu.service.internal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zongmu.service.dto.reviewrecord.BatchRejectObject;
import com.zongmu.service.dto.reviewrecord.RejectReasonObject;
import com.zongmu.service.dto.search.ReviewRecordSearchParam;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.exception.BusinessException;

public interface ReviewRecordService {

	ReviewRecord createReviewRecord(TaskRecord taskRecord);

	ReviewRecord getReviewRecord(String reviewRecordNo) throws BusinessException;

	List<ReviewRecord> getReviewRecordsByTaskItemNo(String taskItemNo);

	void setReviewResult(TaskRecord taskRecord, Boolean isPass, RejectReasonObject rejectReasonObject)
			throws BusinessException;

	Page<ReviewRecord> getReviewRecords(Pageable pageable, int status) throws BusinessException;

	ReviewRecord startReview(String reviewRecordNo) throws BusinessException;

	void batchReviewPass(List<String> reviewRecordNos);

	void reviewPass(String reviewRecordNo) throws BusinessException;

	void batchReviewFailed(BatchRejectObject batchRejectObject);

	void reviewFailed(String reviewRecordNo, RejectReasonObject rejectReasonObject) throws BusinessException;

	Long countByReason(Long reasonId);

	void deleteReviewRecords(TaskItem taskItem);

	Long getFinishTaskCount(Long taskId);

	void newTask(String reviewRecordNo) throws BusinessException;

	Page<ReviewRecord> queryReviewRecords(Pageable pageable, ReviewRecordSearchParam reviewRecordSearchParam)
			throws BusinessException;
}
