package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zongmu.service.configuration.BusinessObjectFacade;
import com.zongmu.service.dto.reviewrecord.BatchRejectObject;
import com.zongmu.service.dto.reviewrecord.RejectReasonObject;
import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.dto.search.ReviewRecordSearchParam;
import com.zongmu.service.dto.taskrecord.TaskRecordStatus;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.PointService;
import com.zongmu.service.internal.service.RejectReasonService;
import com.zongmu.service.internal.service.ReviewRecordService;
import com.zongmu.service.internal.service.TaskRecordService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.UserService;
import com.zongmu.service.repository.ReviewRecordRepository;
import com.zongmu.service.specification.ReviewRecordSpec;
import com.zongmu.service.util.CommonService;
import com.zongmu.service.util.FileService;

@Service
public class ReviewRecordServiceImpl implements ReviewRecordService {

	private static Logger logger = Logger.getLogger(ReviewRecordServiceImpl.class);

	@Autowired
	private ReviewRecordRepository reviewRecordRepo;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TaskRecordService taskRecordService;

	@Autowired
	private BusinessObjectFacade boFacade;

	@Autowired
	private TaskService taskService;

	@Autowired
	private UserService userService;

	@Override
	public ReviewRecord createReviewRecord(TaskRecord taskRecord) {
		ReviewRecord reviewRecord = null;
		if (StringUtils.isEmpty(taskRecord.getReviewRecordNo())) {
			reviewRecord = new ReviewRecord();
			reviewRecord.setReviewRecordNo(this.commonService.generateNo());
			reviewRecord.setTaskRecordNo(taskRecord.getTaskRecordNo());
			reviewRecord.setTaskItemNo(taskRecord.getTaskItemNo());
			reviewRecord.setAssetNo(taskRecord.getAssetNo());
			reviewRecord.setAssetType(taskRecord.getAssetType());
			reviewRecord.setAlgorithmId(taskRecord.getAlgorithmId());
			reviewRecord.setTaskId(taskRecord.getTaskId());
			reviewRecord.setTaskItemId(taskRecord.getTaskItemId());
		} else {
			reviewRecord = this.reviewRecordRepo.getReviewRecord(taskRecord.getReviewRecordNo());
		}
		reviewRecord.setStartTime(DateTime.now());
		reviewRecord.setStatus(ReviewRecordStatus.WAITTING);
		return this.reviewRecordRepo.save(reviewRecord);
	}

	@Override
	public List<ReviewRecord> getReviewRecordsByTaskItemNo(String taskItemNo) {
		List<ReviewRecord> reviewRecords = this.reviewRecordRepo.getReviewRecordsByTaskItemNo(taskItemNo);
		for (ReviewRecord reviewRecord : reviewRecords) {
			reviewRecord.setUserName(this.userService.getUserName(reviewRecord.getUserId()));
		}
		return reviewRecords;
	}

	@Override
	public void setReviewResult(TaskRecord taskRecord, Boolean isPass, RejectReasonObject rejectReasonObject)
			throws BusinessException {
		User user = this.boFacade.currentUser();
		ReviewRecord reviewRecord = this.getReviewRecord(taskRecord.getReviewRecordNo());
		reviewRecord.setUserId(user.getId());
		reviewRecord.setEndTime(DateTime.now());
		if (isPass) {
			this.boFacade.getService(PointService.class).create(reviewRecord, taskRecord);
			reviewRecord.setStatus(ReviewRecordStatus.PASS);
		} else {
			if (rejectReasonObject != null) {
				reviewRecord.setMemo(rejectReasonObject.getMemo());
				reviewRecord.setSubtotal(reviewRecord.getSubtotal() + 1);
				reviewRecord.setReasonId(rejectReasonObject.getReasonId());
			}
			reviewRecord.setStatus(ReviewRecordStatus.FAILED);
		}
		this.reviewRecordRepo.save(reviewRecord);
	}

	@Override
	public Page<ReviewRecord> getReviewRecords(Pageable pageable, int status) throws BusinessException {
		Page<ReviewRecord> page = null;
		if (status == 1) {
			page = this.reviewRecordRepo.getList(ReviewRecordStatus.WAITTING, pageable);
		} else if (status == 2) {
			page = this.reviewRecordRepo.getList(ReviewRecordStatus.INPROGRESS, pageable);
		} else if (status == 3) {
			page = this.reviewRecordRepo.getList(ReviewRecordStatus.PASS, pageable);
		} else if (status == 4) {
			page = this.reviewRecordRepo.getList(ReviewRecordStatus.FAILED, pageable);
		} else {
			page = this.reviewRecordRepo.findAll(pageable);
		}

		for (ReviewRecord reviewRecord : page.getContent()) {
			reviewRecord.setTaskRecord(this.taskRecordService.getTaskRecord(reviewRecord.getTaskRecordNo()));
		}

		return page;
	}

	@Override
	public Page<ReviewRecord> queryReviewRecords(Pageable pageable, ReviewRecordSearchParam param)
			throws BusinessException {
		User user = this.boFacade.currentUser();
		ReviewRecordSpec specification = new ReviewRecordSpec();
		Page<ReviewRecord> page = this.reviewRecordRepo.findAll(specification.search(param, user), pageable);

		for (ReviewRecord reviewRecord : page.getContent()) {
			reviewRecord.setTaskRecord(this.taskRecordService.getTaskRecord(reviewRecord.getTaskRecordNo()));
		}
		return page;
	}

	@Override
	public ReviewRecord startReview(String reviewRecordNo) throws BusinessException {
		User user = this.boFacade.currentUser();
		ReviewRecord reviewRecord = this.getReviewRecord(reviewRecordNo);
		TaskRecord taskRecord = this.taskRecordService.getTaskRecord(reviewRecord.getTaskRecordNo());
		if (taskRecord == null) {
			throw new BusinessException(ErrorCode.TASK_RECORD_NOT_FOUND);
		}

		taskRecord.setStatus(TaskRecordStatus.REVIEWING);
		this.taskRecordService.save(taskRecord);
		reviewRecord.setUserId(user.getId());
		reviewRecord.setStatus(ReviewRecordStatus.INPROGRESS);
		return this.reviewRecordRepo.save(reviewRecord);
	}

	@Override
	public void batchReviewPass(List<String> reviewRecordNos) {
		for (String reviewRecordNo : reviewRecordNos) {
			try {
				this.reviewPass(reviewRecordNo);
			} catch (BusinessException ex) {
				logger.error(ex.getCode());
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}

	@Override
	public void batchReviewFailed(BatchRejectObject batchRejectObject) {
		RejectReasonObject reason = new RejectReasonObject();
		reason.setReasonId(batchRejectObject.getReasonId());
		reason.setMemo(batchRejectObject.getMemo());
		for (String reviewRecordNo : batchRejectObject.getReviewRecordNos()) {
			try {
				this.reviewFailed(reviewRecordNo, reason);
			} catch (BusinessException ex) {
				logger.error(ex.getCode());
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}

	}

	@Override
	public void reviewPass(String reviewRecordNo) throws BusinessException {
		ReviewRecord reviewRecord = this.getReviewRecord(reviewRecordNo);
		this.taskRecordService.reviewPass(reviewRecord.getTaskRecordNo(), null);
	}

	@Override
	public void reviewFailed(String reviewRecordNo, RejectReasonObject rejectReasonObject) throws BusinessException {
		ReviewRecord reviewRecord = this.getReviewRecord(reviewRecordNo);
		this.taskRecordService.reviewFail(reviewRecord.getTaskRecordNo(), rejectReasonObject);
	}

	@Autowired
	private RejectReasonService rejectReasonService;

	@Autowired
	private FileService fileService;

	@Override
	public ReviewRecord getReviewRecord(String reviewRecordNo) throws BusinessException {
		ReviewRecord reviewRecord = this.reviewRecordRepo.getReviewRecord(reviewRecordNo);
		if (reviewRecord == null) {
			throw new BusinessException(ErrorCode.REVIEW_RECORD_NOT_FOUND);
		}

		if (reviewRecord.getReasonId() != null) {
			reviewRecord.setReason(rejectReasonService.get(reviewRecord.getReasonId()));
		}

		Task task = this.taskService.getTaskByTaskRecord(reviewRecord.getTaskRecordNo());
		reviewRecord.setFtpFolder(this.fileService.getFTPPath(task));
		return reviewRecord;
	}

	@Override
	public Long countByReason(Long reasonId) {
		return this.reviewRecordRepo.countByReason(reasonId);
	}

	@Override
	public void deleteReviewRecords(TaskItem taskItem) {
		this.reviewRecordRepo.deleteRecords(taskItem.getTaskItemNo());
	}

	@Override
	public Long getFinishTaskCount(Long taskId) {
		return this.reviewRecordRepo.finishTaskCount(taskId);
	}

	@Override
	public void newTask(String reviewRecordNo) throws BusinessException {
		ReviewRecord reviewRecord = this.reviewRecordRepo.getReviewRecord(reviewRecordNo);
		if (reviewRecord == null) {
			throw new BusinessException(ErrorCode.REVIEW_RECORD_NOT_FOUND);
		}
		this.taskRecordService.clearReviewRecord(reviewRecord.getTaskRecordNo());
		this.taskService.newTask(reviewRecord.getTaskItemNo());
		this.reviewRecordRepo.delete(reviewRecord);
	}

}
