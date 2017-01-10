package com.zongmu.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.reviewrecord.RejectReasonObject;
import com.zongmu.service.dto.search.TaskRecordSearchParam;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.TaskRecordService;

@Controller
@RequestMapping("/taskRecords")
public class TaskRecordController {

	@Autowired
	private TaskRecordService taskRecordService;

	@RequestMapping(value = "/{taskRecordNo}/taskMarks", method = RequestMethod.GET)
	@ResponseBody
	public TaskRecord getTaskMarks(@PathVariable("taskRecordNo") String taskRecordNo,
			@RequestParam(value = "status", required = false, defaultValue = "0") int status) throws BusinessException {
		return this.taskRecordService.getTaskMarks(taskRecordNo,status);
	}

	@RequestMapping(value = "/{taskRecordNo}/algorithm", method = RequestMethod.GET)
	@ResponseBody
	public Algorithm getTags(@PathVariable("taskRecordNo") String taskRecordNo) throws BusinessException {
		return this.taskRecordService.getAlgorithm(taskRecordNo);
	}

	@RequestMapping(value = "/{taskRecordNo}/taskMarks", method = RequestMethod.POST)
	@ResponseBody
	public void saveTaskMarks(@PathVariable("taskRecordNo") String taskRecordNo, @RequestBody TaskRecord taskRecord)
			throws BusinessException {
		this.taskRecordService.saveTaskMarks(taskRecordNo, taskRecord);
	}

	@RequestMapping(value = "/{taskRecordNo}", method = RequestMethod.GET)
	@ResponseBody
	public TaskRecord getTaskRecordDetail(@PathVariable("taskRecordNo") String taskRecordNo) throws BusinessException {
		return this.taskRecordService.getTaskRecordDetail(taskRecordNo);
	}

	@RequestMapping(value = "/my", method = RequestMethod.GET)
	@ResponseBody
	public Page<TaskRecord> getMyTaskRecords(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(value = "status", required = false, defaultValue = "0") int status) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.taskRecordService.getTaskRecords(pageable, status);
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	@ResponseBody
	public Page<TaskRecord> search(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestBody TaskRecordSearchParam taskRecordSearchParam) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.taskRecordService.search(pageable, taskRecordSearchParam);
	}

	@Transactional
	@RequestMapping(value = "/{taskRecordNo}/finish", method = RequestMethod.POST)
	@ResponseBody
	public void taskMarkFinish(@PathVariable("taskRecordNo") String taskRecordNo) throws BusinessException {
		this.taskRecordService.requestReview(taskRecordNo);
	}

	@RequestMapping(value = "/{taskRecordNo}/cancel", method = RequestMethod.POST)
	@ResponseBody
	public void cancel(@PathVariable("taskRecordNo") String taskRecordNo) throws BusinessException {
		this.taskRecordService.cancel(taskRecordNo);
	}

	@Transactional
	@RequestMapping(value = "/{taskRecordNo}/accept", method = RequestMethod.POST)
	@ResponseBody
	public void taskMarkReviewPass(@PathVariable("taskRecordNo") String taskRecordNo, @RequestBody String memo)
			throws BusinessException {
		this.taskRecordService.reviewPass(taskRecordNo, memo);
	}

	@Transactional
	@RequestMapping(value = "/{taskRecordNo}/reject", method = RequestMethod.POST)
	@ResponseBody
	public void taskMarkReviewFail(@PathVariable("taskRecordNo") String taskRecordNo,
			@RequestBody RejectReasonObject rejectReasonObject) throws BusinessException {
		this.taskRecordService.reviewFail(taskRecordNo, rejectReasonObject);
	}
}
