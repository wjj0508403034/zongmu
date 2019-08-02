package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.configuration.BusinessObjectFacade;
import com.zongmu.service.dto.reviewrecord.RejectReasonObject;
import com.zongmu.service.dto.search.TaskRecordSearchParam;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.dto.taskrecord.TaskRecordStatus;
import com.zongmu.service.dto.user.BusinessRole;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskMark;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.TaskRecordService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.UserService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordService;
import com.zongmu.service.internal.service.AlgorithmService;
import com.zongmu.service.internal.service.AssetTagService;
import com.zongmu.service.internal.service.ReviewRecordService;
import com.zongmu.service.internal.service.TaskMarkService;
import com.zongmu.service.repository.TaskRecordRepository;
import com.zongmu.service.specification.TaskRecordSpecification;
import com.zongmu.service.util.CommonService;

@Service
public class TaskRecordServiceImpl implements TaskRecordService {

	private static Logger logger = Logger.getLogger(TaskRecordServiceImpl.class);

	@Autowired
	private TaskRecordRepository taskRecordRepo;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskMarkService taskMarkService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private ReviewRecordService reviewRecordService;

	@Autowired
	private TaskMarkRecordService taskMarkRecordService;

	@Autowired
	private AlgorithmService algorithmService;

	@Autowired
	private BusinessObjectFacade boFacade;

	@Autowired
	private AssetTagService assetTagService;

	@Autowired
	private UserService userService;


	@Override
	public Page<TaskRecord> search(Pageable pageable, TaskRecordSearchParam taskRecordSearchParam) {
		User user = this.boFacade.currentUser();
		TaskRecordSpecification taskRecordSpecification = new TaskRecordSpecification();
		Page<TaskRecord> taskRecords = this.taskRecordRepo
				.findAll(taskRecordSpecification.search(taskRecordSearchParam, user), pageable);
		for (TaskRecord taskRecord : taskRecords) {
			taskRecord.setTaskItem(this.taskService.getSimpleTaskItem(taskRecord.getTaskItemNo()));
		}

		return taskRecords;
	}

	
	@Override
	public Page<TaskRecord> getTaskRecords(Pageable pageable, int status) {
		User user = this.boFacade.currentUser();
		if (status == 1) {
			if (user.getBusinessRole() == BusinessRole.ADMIN) {
				return this.taskRecordRepo.getAdminTaskRecords(TaskRecordStatus.INPROGRESS, pageable);
			}
			return this.taskRecordRepo.getMyTaskRecords(user.getId(), TaskRecordStatus.INPROGRESS, pageable);
		} else if (status == 2) {
			if (user.getBusinessRole() == BusinessRole.ADMIN) {
				return this.taskRecordRepo.getAdminTaskRecords(TaskRecordStatus.WAITTING, pageable);
			}
			return this.taskRecordRepo.getMyTaskRecords(user.getId(), TaskRecordStatus.WAITTING, pageable);
		} else if (status == 3) {
			if (user.getBusinessRole() == BusinessRole.ADMIN) {
				return this.taskRecordRepo.getAdminTaskRecords(TaskRecordStatus.REVIEWING, pageable);
			}
			return this.taskRecordRepo.getMyTaskRecords(user.getId(), TaskRecordStatus.REVIEWING, pageable);
		} else if (status == 4) {
			if (user.getBusinessRole() == BusinessRole.ADMIN) {
				return this.taskRecordRepo.getAdminTaskRecords(TaskRecordStatus.ACCEPTED, pageable);
			}
			return this.taskRecordRepo.getMyTaskRecords(user.getId(), TaskRecordStatus.ACCEPTED, pageable);
		} else if (status == 5) {
			if (user.getBusinessRole() == BusinessRole.ADMIN) {
				return this.taskRecordRepo.getAdminTaskRecords(TaskRecordStatus.REJECTED, pageable);
			}
			return this.taskRecordRepo.getMyTaskRecords(user.getId(), TaskRecordStatus.REJECTED, pageable);
		} else {
			if (user.getBusinessRole() == BusinessRole.ADMIN) {
				return this.taskRecordRepo.getAdminTaskRecords(pageable);
			}
			return this.taskRecordRepo.getMyTaskRecords(user.getId(), pageable);
		}
	}

	@Override
	public void saveTaskMarks(String taskRecordNo, TaskMark taskMark) throws BusinessException {
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);
		taskRecord.setStatus(TaskRecordStatus.INPROGRESS);
		taskRecord.setUpdateTime(DateTime.now());
		this.taskRecordRepo.save(taskRecord);
		this.taskMarkService.saveTaskMarks(taskRecord.getId(), taskMark);
	}

	@Override
	public void requestReview(String taskRecordNo) throws BusinessException {
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);
		taskRecord.setStatus(TaskRecordStatus.WAITTING);
		taskRecord.setUpdateTime(DateTime.now());
		taskRecord.setEndTime(DateTime.now());

		ReviewRecord reviewRecord = this.reviewRecordService.createReviewRecord(taskRecord);

		taskRecord.setReviewRecordNo(reviewRecord.getReviewRecordNo());
		this.taskRecordRepo.save(taskRecord);
	}

	//@CachePut(value = "taskMarkCache", key = "#taskRecordNo")
	@Override
	public TaskRecord reviewPass(String taskRecordNo, String memo) throws BusinessException {
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);

		TaskItem taskItem = this.taskService.getTaskItem(taskRecord.getTaskItemNo());
		if (taskItem == null) {
			throw new BusinessException(ErrorCode.TASKITEM_NOT_FOUND);
		}

		taskItem.setStatus(TaskItemStatus.FINISHED);
		taskItem.setUpdateTime(DateTime.now());
		this.taskService.saveTaskItem(taskItem);

		taskRecord.setStatus(TaskRecordStatus.ACCEPTED);
		taskRecord.setUpdateTime(DateTime.now());
		taskRecord.setEndTime(DateTime.now());
		taskRecord.setPoint(taskItem.getPoint());
		this.taskRecordRepo.save(taskRecord);
		this.reviewRecordService.setReviewResult(taskRecord, true, null);
		return this.getTaskMarks(taskRecord.getTaskRecordNo(), 0);
	}

	//@CachePut(value = "taskMarkCache", key = "#taskRecordNo")
	@Override
	public TaskRecord reviewFail(String taskRecordNo, RejectReasonObject rejectReasonObject) throws BusinessException {
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);
		
		TaskItem taskItem = this.taskService.getTaskItem(taskRecord.getTaskItemNo());
		if (taskItem == null) {
			throw new BusinessException(ErrorCode.TASKITEM_NOT_FOUND);
		}

		taskItem.setStatus(TaskItemStatus.REVIEWFAILED);
		taskItem.setUpdateTime(DateTime.now());
		this.taskService.saveTaskItem(taskItem);

		taskRecord.setStatus(TaskRecordStatus.REJECTED);
		taskRecord.setUpdateTime(DateTime.now());
		taskRecord.setEndTime(DateTime.now());
		this.taskRecordRepo.save(taskRecord);
		this.reviewRecordService.setReviewResult(taskRecord, false, rejectReasonObject);
		return this.getTaskMarks(taskRecord.getTaskRecordNo(), 0);
	}

	@Override
	public Long countRejectRecords() {
		User user = this.boFacade.currentUser();
		return this.taskRecordRepo.countRejectRecords(user.getId());
	}

	@Override
	public TaskRecord createNewTaskRecord(TaskItem taskItem) {
		User user = this.boFacade.currentUser();
		TaskRecord taskRecord = new TaskRecord();
		taskRecord.setTaskItemNo(taskItem.getTaskItemNo());
		taskRecord.setTaskRecordNo(this.commonService.generateNo());
		taskRecord.setUpdateTime(DateTime.now());
		taskRecord.setStartTime(DateTime.now());
		taskRecord.setStatus(TaskRecordStatus.INPROGRESS);
		taskRecord.setAssetType(taskItem.getAssetType());
		taskRecord.setUserId(user.getId());
		taskRecord.setTaskType(taskItem.getTaskType());
		taskRecord.setPoint(taskItem.getPoint());
		taskRecord.setAssetNo(taskItem.getAssetNo());
		taskRecord.setAlgorithmId(taskItem.getAlgorithmId());
		taskRecord.setTaskId(taskItem.getTaskId());
		taskRecord.setTaskItemId(taskItem.getId());
		this.taskRecordRepo.save(taskRecord);
		return taskRecord;
	}

	@Override
	public TaskRecord getTaskRecord(String taskRecordNo) throws BusinessException {
		TaskRecord taskRecord = this.taskRecordRepo.getTaskRecord(taskRecordNo);
		if (taskRecord == null) {
			throw new BusinessException(ErrorCode.TASK_RECORD_NOT_FOUND);
		}

		taskRecord.setTaskItem(this.taskService.getSimpleTaskItem(taskRecord.getTaskItemNo()));
		return taskRecord;
	}

	@Override
	public List<TaskRecord> getTaskRecords(String taskItemNo) throws BusinessException {
		User user = this.boFacade.currentUser();
		List<TaskRecord> taskRecords = this.taskRecordRepo.getTaskRecords(taskItemNo);
		for (TaskRecord taskRecord : taskRecords) {
			if (user.getBusinessRole() != BusinessRole.ADMIN && user.getBusinessRole() != BusinessRole.REVIEW) {
				if (!user.getId().equals(taskRecord.getUserId())) {
					throw new BusinessException(ErrorCode.Task_Not_Belongs_To);
				}
			}
			taskRecord.setUserName(this.userService.getUserName(taskRecord.getUserId()));
		}
		return taskRecords;
	}
	
	@Override
	public List<TaskRecord> getTaskRecordsByTaskId(Long taskId){
		return this.taskRecordRepo.getTaskRecordsByTaskId(taskId);
	}
	

	@Override
	public TaskRecord getTaskRecordDetail(String taskRecordNo) throws BusinessException {
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);

		TaskItem taskItem = this.taskService.getTaskItem(taskRecord.getTaskItemNo());
		if (taskItem == null) {
			throw new BusinessException("Cannot find task item");
		}

		taskRecord.setTaskItem(taskItem);
		TaskMark taskMark = this.taskMarkService.getTaskMark(taskRecord.getId());
		taskRecord.setTaskMark(taskMark);
		return taskRecord;
	}

	@Override
	public TaskRecord save(TaskRecord taskRecord) {
		return this.taskRecordRepo.save(taskRecord);
	}

	@Transactional
	@Override
	public void cancel(String taskRecordNo) throws BusinessException {
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);
		this.cancelTask(taskRecord);
	}

	@Transactional
	@Override
	public void cancelTimeoutTasks() {
		DateTime startTime = DateTime.now().minusDays(7);
		List<TaskRecord> records = this.taskRecordRepo.getTimeoutRecords(startTime, TaskRecordStatus.INPROGRESS);
		for (TaskRecord record : records) {
			try {
				this.cancelTask(record);
			} catch (BusinessException ex) {
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}

	/*
	 * #state = 0,我的任务 #state = 1,审核任务
	 */
	//@Cacheable(value="taskMarkCache",key="#taskRecordNo")
	@Override
	public TaskRecord getTaskMarks(String taskRecordNo, int state) throws BusinessException {
		User user = this.boFacade.currentUser();
		TaskRecord taskRecord = this.getTaskRecord(taskRecordNo);
		if (user.getBusinessRole() != BusinessRole.ADMIN && user.getBusinessRole() != BusinessRole.REVIEW) {
			if (!user.getId().equals(taskRecord.getUserId())) {
				throw new BusinessException(ErrorCode.Task_Not_Belongs_To);
			}
		}
		taskRecord.setTaskMarkRecords(this.taskMarkRecordService.getRecords(taskRecord.getId()));
		TaskItem taskItem = this.taskService.getTaskItem(taskRecord.getTaskItemNo());

		AssetTag roadTag = this.assetTagService.getAssetTag(taskItem.getRoadTagId());
		taskItem.setRoadTag(roadTag);
		AssetTag weatherTag = this.assetTagService.getAssetTag(taskItem.getWeatherTagId());
		taskItem.setWeatherTag(weatherTag);

		taskRecord.setTaskItem(taskItem);
		if (state == 0) {
			this.myTask(taskRecord, user);
		} else if (state == 1) {
			this.myReviewTask(taskRecord, user);
		}

		return taskRecord;
	}

	private void myTask(TaskRecord taskRecord, User user) {
		TaskRecord pervious = null;
		TaskRecord next = null;
		if (user.getBusinessRole() == BusinessRole.ADMIN) {
			pervious = this.taskRecordRepo.getAdminPervious(taskRecord.getId());
			next = this.taskRecordRepo.getAdminNext(taskRecord.getId());
		} else {
			pervious = this.taskRecordRepo.getPervious(taskRecord.getId(), taskRecord.getUserId());
			next = this.taskRecordRepo.getNext(taskRecord.getId(), taskRecord.getUserId());
		}
		taskRecord.setPreviousTaskRecord(pervious);
		taskRecord.setNextTaskRecord(next);
	}

	private void myReviewTask(TaskRecord taskRecord, User user) {
		TaskRecord pervious = null;
		TaskRecord next = null;
		if (user.getBusinessRole() == BusinessRole.ADMIN) {
			pervious = this.taskRecordRepo.getAdminReviewPervious(taskRecord.getId());
			next = this.taskRecordRepo.getAdminReviewNext(taskRecord.getId());
		} else {
			pervious = this.taskRecordRepo.getReviewPervious(taskRecord.getId(), taskRecord.getUserId());
			next = this.taskRecordRepo.getReviewNext(taskRecord.getId(), taskRecord.getUserId());
		}
		taskRecord.setPreviousTaskRecord(pervious);
		taskRecord.setNextTaskRecord(next);
	}

	@Override
	public Algorithm getAlgorithm(String taskRecordNo) throws BusinessException {
		Task task = this.taskService.getTaskByTaskRecord(taskRecordNo);
		return this.algorithmService.getAlgorithmDetail(task.getAlgorithmId());
	}

	//@CachePut(value = "taskMarkCache", key = "#taskRecord.getTaskRecordNo()")
	@Transactional
	@Override
	public TaskRecord saveTaskMarks(TaskRecord taskRecord) throws BusinessException {
		TaskRecord oldTaskRecord = this.getTaskRecord(taskRecord.getTaskRecordNo());
		oldTaskRecord.setUpdateTime(DateTime.now());
		this.taskRecordRepo.save(oldTaskRecord);

		this.taskMarkRecordService.saveRecord(oldTaskRecord.getId(), taskRecord.getTaskMarkRecords());
		return this.getTaskMarks(taskRecord.getTaskRecordNo(), 0);
	}

	private void cancelTask(TaskRecord record) throws BusinessException {
		TaskItem taskItem = this.taskService.getTaskItem(record.getTaskItemNo());
		if (taskItem == null) {
			throw new BusinessException("Cannot find task item");
		}

		taskItem.setTaskRecordNo(null);
		taskItem.setStatus(TaskItemStatus.NEW);
		this.taskService.saveTaskItem(taskItem);
		this.deleteTaskRecord(taskItem);
		//this.taskMarkRecordService.deleteRecords(record.getId());
	}

	@Override
	public void deleteTaskRecord(TaskItem taskItem) {
		List<Long> taskRecordIds = this.taskRecordRepo.getTaskRecordIdsByTaskItemNo(taskItem.getTaskItemNo());
		for(Long taskRecordId : taskRecordIds){
			this.taskMarkRecordService.deleteRecords(taskRecordId);
		}
		this.taskRecordRepo.deleteTaskRecords(taskItem.getTaskItemNo());
		this.reviewRecordService.deleteReviewRecords(taskItem);
	}

	@Override
	public void clearReviewRecord(String taskRecordNo) {
		TaskRecord taskRecord = this.taskRecordRepo.getTaskRecord(taskRecordNo);
		if (taskRecord != null) {
			taskRecord.setReviewRecordNo(null);
			taskRecord.setStatus(TaskRecordStatus.INPROGRESS);
			taskRecord.setUserId(null);
			this.taskRecordRepo.save(taskRecord);
		}
	}

}
